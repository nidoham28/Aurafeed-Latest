package com.nidoham.aurafeed.features.auth.state

/**
 * Aurafeed — Auth UI State
 * ───────────────────────────────────────────────────────────
 *  Pure-Kotlin state holders for the auth screens.
 *  No Compose / Android / iOS / Desktop dependency — unit-testable.
 *
 *  Per project rule: "Never trust the client" — all sign-in / sign-up
 *  operations POST to Supabase Edge Functions, never call Supabase
 *  client SDK directly from the UI. The UI just renders [AuthUiState].
 */

// ─────────────────────────────────────────────────────────────────────────────
// 1. UI STATE — Idle / Loading / Error / Success
// ─────────────────────────────────────────────────────────────────────────────

/** Which control started the in-flight request, so only that one shows progress. */
enum class AuthAction { Email, Google }

sealed interface AuthUiState<out T> {
    data object Idle : AuthUiState<Nothing>

    /** Carries the originating [action] so the other button stays readable. */
    data class Loading(val action: AuthAction) : AuthUiState<Nothing>

    /**
     * [messageKey] is a *key*, never an English string — resolve it with
     * [AuthStrings.forKey] (or a platform string lookup) before rendering.
     */
    data class Error(val messageKey: String, val code: String? = null) : AuthUiState<Nothing>

    data class Success<out T>(val data: T) : AuthUiState<T>
}

val AuthUiState<*>.isBusy: Boolean
    get() = this is AuthUiState.Loading

fun AuthUiState<*>.isLoading(action: AuthAction): Boolean =
    this is AuthUiState.Loading && this.action == action

/** Convenience for the platform layer: build an Error straight from an Edge Function code. */
fun authError(code: String?): AuthUiState.Error =
    AuthUiState.Error(messageKey = mapAuthErrorCode(code), code = code)

// ─────────────────────────────────────────────────────────────────────────────
// 2. AUTH SESSION — returned by Edge Functions on success
// ─────────────────────────────────────────────────────────────────────────────

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val expiresAt: Long,         // epoch millis
    val deviceSessionId: String, // for the device-tracking + security-alert system
)

// ─────────────────────────────────────────────────────────────────────────────
// 3. DEVICE EVIDENCE — captured on every login/signup per project rule
// ─────────────────────────────────────────────────────────────────────────────

data class DeviceEvidence(
    val platform: String,        // "android" / "ios" / "desktop-jvm"
    val platformVersion: String, // OS version
    val deviceId: String,        // stable per-platform device fingerprint
    val appVersion: String,
    val appBuildCode: Int,
    val locale: String,          // e.g. "en-US"
    val timezone: String,        // e.g. "Asia/Dhaka"
    val timestamp: Long,         // epoch millis at request time
)

// ─────────────────────────────────────────────────────────────────────────────
// 4. EDGE FUNCTION REQUEST MODELS
// ─────────────────────────────────────────────────────────────────────────────

data class SignInRequest(
    val email: String,
    val password: String,
    val deviceEvidence: DeviceEvidence,
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val deviceEvidence: DeviceEvidence,
)

data class GoogleOAuthRequest(
    val idToken: String, // Google ID token from platform OAuth flow
    val deviceEvidence: DeviceEvidence,
)

// ─────────────────────────────────────────────────────────────────────────────
// 5. FORM VALIDATION — pure functions, reusable on every platform
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Typed field errors. Returning an enum instead of a bare String makes it
 * impossible to leak a raw key like "auth.error.email_required" into the UI —
 * the previous version rendered those keys verbatim under the text fields.
 */
enum class AuthFieldError(val key: String) {
    EmailRequired("auth.error.email_required"),
    EmailInvalid("auth.error.email_invalid"),
    PasswordRequired("auth.error.password_required"),
    PasswordTooShort("auth.error.password_too_short"),
    PasswordWeak("auth.error.password_weak"),
}

/** RFC-5322 simplified email pattern — sufficient for a client-side hint. */
private val EMAIL_REGEX = Regex(
    pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
)

fun validateEmail(email: String): AuthFieldError? {
    val trimmed = email.trim()
    if (trimmed.isEmpty()) return AuthFieldError.EmailRequired
    if (!EMAIL_REGEX.matches(trimmed)) return AuthFieldError.EmailInvalid
    return null
}

/** Password policy: min 8 chars, at least one letter and one digit. */
fun validatePassword(password: String): AuthFieldError? {
    if (password.isEmpty()) return AuthFieldError.PasswordRequired
    if (password.length < PASSWORD_MIN_LENGTH) return AuthFieldError.PasswordTooShort
    val checks = passwordChecks(password)
    if (!checks.hasLetter || !checks.hasDigit) return AuthFieldError.PasswordWeak
    return null
}

/**
 * Sign-in is deliberately laxer than sign-up: an existing account may predate
 * the current policy, so we only require a non-empty password to submit. The
 * server is still the authority.
 */
fun validateSignInPassword(password: String): AuthFieldError? =
    if (password.isEmpty()) AuthFieldError.PasswordRequired else null

fun isFormValid(email: String, password: String): Boolean =
    validateEmail(email) == null && validatePassword(password) == null

const val PASSWORD_MIN_LENGTH: Int = 8

/** Per-requirement breakdown so sign-up can show live progress instead of one blunt error. */
data class PasswordChecks(
    val longEnough: Boolean,
    val hasLetter: Boolean,
    val hasDigit: Boolean,
) {
    val allMet: Boolean get() = longEnough && hasLetter && hasDigit
}

fun passwordChecks(password: String): PasswordChecks = PasswordChecks(
    longEnough = password.length >= PASSWORD_MIN_LENGTH,
    hasLetter = password.any { it.isLetter() },
    hasDigit = password.any { it.isDigit() },
)

// ─────────────────────────────────────────────────────────────────────────────
// 6. ERROR MAPPING + MESSAGE RESOLUTION
// ─────────────────────────────────────────────────────────────────────────────

fun mapAuthErrorCode(code: String?): String {
    if (code == null) return "auth.error.unknown"
    return when (code) {
        "AUTH_INVALID_CREDENTIALS"      -> "auth.error.invalid_credentials"
        "AUTH_EMAIL_NOT_CONFIRMED"      -> "auth.error.email_not_confirmed"
        "AUTH_EMAIL_ALREADY_REGISTERED" -> "auth.error.email_registered"
        "AUTH_RATE_LIMITED"             -> "auth.error.rate_limited"
        "AUTH_DEVICE_FLAGGED"           -> "auth.error.device_flagged"
        "AUTH_SESSION_CLOSED"           -> "auth.error.session_closed"
        "AUTH_GOOGLE_TOKEN_INVALID"     -> "auth.error.google_token"
        "AUTH_NETWORK"                  -> "auth.error.network"
        else                            -> "auth.error.unknown"
    }
}

/**
 * Single place that turns a key into text. Swap the map for a platform string
 * lookup (stringResource / NSLocalizedString) when localisation lands — the
 * call sites in the UI do not change.
 *
 * Copy rules: say what happened and what to do next, no apologies, no jargon.
 */
object AuthStrings {

    fun forKey(key: String): String = MESSAGES[key] ?: MESSAGES.getValue("auth.error.unknown")

    fun forError(error: AuthFieldError): String = forKey(error.key)

    private val MESSAGES: Map<String, String> = mapOf(
        // Field-level
        "auth.error.email_required"      to "Enter your email address.",
        "auth.error.email_invalid"       to "That doesn't look like an email address.",
        "auth.error.password_required"   to "Enter your password.",
        "auth.error.password_too_short"  to "Use at least $PASSWORD_MIN_LENGTH characters.",
        "auth.error.password_weak"       to "Add at least one letter and one number.",
        // Server-level
        "auth.error.invalid_credentials" to "That email and password don't match. Check both and try again.",
        "auth.error.email_not_confirmed" to "Confirm your email first — open the link we sent you.",
        "auth.error.email_registered"    to "That email already has an account. Sign in instead.",
        "auth.error.rate_limited"        to "Too many attempts. Wait a few minutes, then try again.",
        "auth.error.device_flagged"      to "This device isn't recognised. Approve it from the email we just sent.",
        "auth.error.session_closed"      to "Your session ended. Sign in again to continue.",
        "auth.error.google_token"        to "Google sign-in didn't finish. Try again.",
        "auth.error.network"             to "No connection. Check your network and try again.",
        "auth.error.unknown"             to "That didn't work. Try again.",
    )
}