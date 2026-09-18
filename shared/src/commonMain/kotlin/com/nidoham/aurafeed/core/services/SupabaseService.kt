package com.nidoham.aurafeed.core.services

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlin.concurrent.Volatile

/**
 * Aurafeed — Supabase Service
 * ───────────────────────────────────────────────────────────
 *  Single process-wide [SupabaseClient] holder, installed with the modules
 *  Aurafeed actually uses (Auth, Postgrest, Realtime, Storage, Functions).
 *  Mirrors the same "call [initialize] once, at app startup, then read a
 *  plain accessor" pattern already used by `AurafeedUtil` in this project —
 *  no dependency-injection framework required, but this plays nicely with
 *  one if you add it later (just wrap [client] in your DI module).
 *
 *  Platform target: Kotlin Compose Multiplatform — Android, iOS, Desktop (JVM)
 *  only. This file is pure `commonMain`; supabase-kt resolves its Ktor HTTP
 *  engine automatically from whichever engine artifact is on each source
 *  set's classpath (see the Gradle notes below) — no platform-specific code
 *  needed here.
 *
 *  ── Security ──────────────────────────────────────────────────────────
 *  Only ever pass the **anon/public** key to [initialize]. It is safe to
 *  ship on-device *because* Supabase's Row Level Security (RLS) policies —
 *  not secrecy of this key — are what actually protect your data. Never
 *  embed a `service_role` / admin key in a client app: anything that needs
 *  elevated privileges belongs in a Supabase Edge Function or your own
 *  backend, called from the client, not shipped inside it.
 *
 *  ── Where the url/key should come from ───────────────────────────────
 *  Don't hardcode them here. Read them per-platform and call [initialize]
 *  once at startup:
 *   - Android: `BuildConfig.SUPABASE_URL` / `BuildConfig.SUPABASE_ANON_KEY`,
 *     populated from `local.properties` / Gradle `buildConfigField` so real
 *     values never get committed.
 *   - iOS: an `.xcconfig` / Info.plist entry, read via your Swift host and
 *     passed into Kotlin at launch (e.g. through your `MainViewController` bridge).
 *   - Desktop (JVM): an environment variable or a local, git-ignored
 *     properties file loaded in `main()`.
 *  Call [initialize] once, then everything downstream (repositories,
 *  ViewModels) just reads [SupabaseService.client] / [auth] / [postgrest] etc.
 *
 *  ── Required Gradle dependencies (per source set) ────────────────────
 *  commonMain:
 *    implementation(platform("io.github.jan-tennert.supabase:bom:<version>"))
 *    implementation("io.github.jan-tennert.supabase:auth-kt")
 *    implementation("io.github.jan-tennert.supabase:postgrest-kt")
 *    implementation("io.github.jan-tennert.supabase:realtime-kt")
 *    implementation("io.github.jan-tennert.supabase:storage-kt")
 *    implementation("io.github.jan-tennert.supabase:functions-kt")
 *  androidMain: implementation("io.ktor:ktor-client-okhttp")
 *  iosMain:     implementation("io.ktor:ktor-client-darwin")
 *  desktopMain: implementation("io.ktor:ktor-client-cio")
 */
object SupabaseService {

    @Volatile
    private var _client: SupabaseClient? = null

    /**
     * The active [SupabaseClient]. Throws with a clear, actionable message if
     * read before [initialize] has run — mirrors `LocalAurafeedTheme`'s
     * "not provided" error so a mistake here fails loudly instead of
     * silently returning nulls deeper in a repository.
     */
    val client: SupabaseClient
        get() = _client ?: error(
            "SupabaseService not initialized. Call SupabaseService.initialize(url, anonKey) " +
                    "once at app startup — before any code touches SupabaseService.client, " +
                    ".auth, .postgrest, .realtime, .storage, or .functions."
        )

    /** Whether [initialize] has already run. Safe to check from any thread. */
    val isInitialized: Boolean get() = _client != null

    /**
     * Creates and installs the shared [SupabaseClient]. Intended to be called
     * exactly once, from app startup (e.g. each platform's `main()` /
     * `Application.onCreate()`, or a single `LaunchedEffect(Unit)` in the
     * root composable) — not from arbitrary screens.
     *
     * Idempotent: a second call is a no-op rather than replacing the client,
     * so accidentally calling this again (e.g. on process restore) can't
     * tear down an in-flight session.
     *
     * @param url     Your project's Supabase URL, e.g. `https://xyzcompany.supabase.co`.
     * @param anonKey Your project's **anon/public** API key. Never the `service_role` key.
     */
    fun initialize(url: String, anonKey: String) {
        require(url.isNotBlank()) { "Supabase url must not be blank." }
        require(anonKey.isNotBlank()) { "Supabase anonKey must not be blank." }
        if (_client != null) return

        _client = createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = anonKey,
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
            install(Functions)
        }
    }

    /** Shorthand for [client]'s `Auth` plugin — sign-in, sign-up, sessions. */
    val auth get() = client.auth

    /** Shorthand for [client]'s `Postgrest` plugin — typed database queries. */
    val postgrest get() = client.postgrest

    /** Shorthand for [client]'s `Realtime` plugin — live table/channel subscriptions. */
    val realtime get() = client.realtime

    /** Shorthand for [client]'s `Storage` plugin — file/bucket uploads & downloads. */
    val storage get() = client.storage

    /** Shorthand for [client]'s `Functions` plugin — invoking Supabase Edge Functions. */
    val functions get() = client.functions
}