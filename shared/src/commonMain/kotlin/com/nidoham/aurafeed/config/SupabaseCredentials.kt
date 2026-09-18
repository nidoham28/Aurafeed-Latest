@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.nidoham.aurafeed.config

/**
 * Aurafeed — Supabase Credentials
 * ───────────────────────────────────────────────────────────
 *  Per-platform source of the Supabase project URL and **anon/public** key.
 *  `expect`/`actual` so each target reads its secrets from wherever they
 *  actually live on that platform — never hardcoded here in `commonMain`.
 *
 *  Actual implementations:
 *   - `SupabaseCredentials.android.kt` — reads `BuildConfig` fields, which
 *     Gradle populates from `local.properties` (git-ignored).
 *   - `SupabaseCredentials.ios.kt` — reads `Info.plist`, populated via
 *     Xcode `.xcconfig` variable substitution (the `.xcconfig` file itself
 *     is git-ignored; only a `.xcconfig.example` template is committed).
 *   - `SupabaseCredentials.desktop.kt` — reads process environment variables.
 *
 *  Every `actual` fails fast with a clear message if its source is missing,
 *  rather than silently falling back to an empty string that would only
 *  surface as a confusing network error later.
 *
 *  Only the anon/public key belongs here on any platform — see the security
 *  note in `SupabaseService.kt` for why the `service_role` key must never
 *  ship inside a client app.
 */
expect object SupabaseCredentials {
    val url: String
    val anonKey: String
}