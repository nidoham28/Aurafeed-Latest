@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.nidoham.aurafeed.config

import java.io.File
import java.util.Properties

/**
 * Desktop (JVM) `actual` — reads from process environment variables or
 * falls back to a `local.properties` file in the project root.
 *
 * This allows you to set:
 * ```
 * export SUPABASE_URL=https://xyzcompany.supabase.co
 * export SUPABASE_ANON_KEY=your-anon-key
 * ```
 * or simply define them in your local `local.properties` (git-ignored).
 */
actual object SupabaseCredentials {

    private val localProperties by lazy {
        Properties().apply {
            // Search for local.properties in the working directory and parent levels
            // to handle different execution contexts (Gradle, IDE, CLI).
            var current: File? = File(".").absoluteFile
            repeat(4) {
                if (current == null) return@repeat
                val file = File(current, "local.properties")
                if (file.exists()) {
                    file.inputStream().use { load(it) }
                    return@apply
                }
                current = current.parentFile
            }
        }
    }

    private fun getSecret(key: String): String? =
        System.getenv(key) ?: localProperties.getProperty(key)

    actual val url: String by lazy {
        requireNotNull(getSecret("SUPABASE_URL")) {
            "Missing SUPABASE_URL — set it as an environment variable or in local.properties."
        }.also { require(it.isNotBlank()) { "SUPABASE_URL is blank." } }
    }

    actual val anonKey: String by lazy {
        requireNotNull(getSecret("SUPABASE_ANON_KEY")) {
            "Missing SUPABASE_ANON_KEY — set it as an environment variable or in local.properties."
        }.also { require(it.isNotBlank()) { "SUPABASE_ANON_KEY is blank." } }
    }
}
