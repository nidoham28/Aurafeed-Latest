package com.nidoham.aurafeed.features.auth.state

/**
 * Aurafeed — Auth Mode
 * ───────────────────────────────────────────────────────────
 *  Which auth surface is showing. [com.nidoham.aurafeed.features.auth.screen.AuthScreen] only renders [SignIn] and
 *  [SignUp]; [ForgotPassword] and [VerifyEmail] are separate routes in
 *  NavigationManager and fall back to sign-in if they reach AuthScreen.
 */
enum class AuthMode {
    SignIn,
    SignUp,
    ForgotPassword,
    VerifyEmail,
}
