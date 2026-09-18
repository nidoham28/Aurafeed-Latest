@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.nidoham.aurafeed.config

import platform.Foundation.NSBundle

/**
 * iOS `actual` — reads from `Info.plist`, itself populated by Xcode
 * `.xcconfig` variable substitution so the real values never get committed:
 *
 * `Info.plist`:
 * ```xml
 * <key>SUPABASE_URL</key>
 * <string>$(SUPABASE_URL)</string>
 * <key>SUPABASE_ANON_KEY</key>
 * <string>$(SUPABASE_ANON_KEY)</string>
 * ```
 *
 * `Secrets.xcconfig` (git-ignored; commit a `Secrets.xcconfig.example`
 * template instead), referenced from your target's build settings:
 * ```
 * SUPABASE_URL = https://xyzcompany.supabase.co
 * SUPABASE_ANON_KEY = your-anon-key
 * ```
 */
actual object SupabaseCredentials {
    actual val url: String = requireNotNull(
        NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_URL") as? String
    ) { "Missing SUPABASE_URL in Info.plist — check your .xcconfig." }
        .also { require(it.isNotBlank()) { "SUPABASE_URL in Info.plist is blank." } }

    actual val anonKey: String = requireNotNull(
        NSBundle.mainBundle.objectForInfoDictionaryKey("SUPABASE_ANON_KEY") as? String
    ) { "Missing SUPABASE_ANON_KEY in Info.plist — check your .xcconfig." }
        .also { require(it.isNotBlank()) { "SUPABASE_ANON_KEY in Info.plist is blank." } }
}