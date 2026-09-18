package com.nidoham.aurafeed.config

/**
 * Android `actual` — reads from the generated `BuildConfig` fields of the app module
 * via reflection at runtime to avoid compile-time circular dependencies.
 *
 * Add to your app module's `build.gradle.kts` (values pulled from
 * `local.properties`, which stays out of version control):
 * ```
 * android {
 *     buildFeatures { buildConfig = true }
 *     defaultConfig {
 *         val localProperties = java.util.Properties().apply {
 *             val file = rootProject.file("local.properties")
 *             if (file.exists()) load(file.inputStream())
 *         }
 *         buildConfigField("String", "SUPABASE_URL", "\"${localProperties["SUPABASE_URL"] ?: ""}\"")
 *         buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties["SUPABASE_ANON_KEY"] ?: ""}\"")
 *     }
 * }
 * ```
 * and in `local.properties` (git-ignored):
 * ```
 * SUPABASE_URL=https://xyzcompany.supabase.co
 * SUPABASE_ANON_KEY=your-anon-key
 * ```
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object SupabaseCredentials {
    private val buildConfigClass: Class<*>? by lazy {
        try {
            Class.forName("com.nidoham.aurafeed.BuildConfig")
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    private fun getBuildConfigField(fieldName: String): String {
        val clazz = buildConfigClass ?: throw IllegalStateException(
            "com.nidoham.aurafeed.BuildConfig class not found. Ensure buildConfig = true is set in the androidApp module's build.gradle.kts."
        )
        return try {
            clazz.getField(fieldName).get(null) as String
        } catch (e: Exception) {
            throw IllegalStateException("Field $fieldName not found in BuildConfig.", e)
        }
    }

    actual val url: String by lazy {
        getBuildConfigField("SUPABASE_URL").also {
            require(it.isNotBlank()) { "SUPABASE_URL is blank — check local.properties in androidApp." }
        }
    }

    actual val anonKey: String by lazy {
        getBuildConfigField("SUPABASE_ANON_KEY").also {
            require(it.isNotBlank()) { "SUPABASE_ANON_KEY is blank — check local.properties in androidApp." }
        }
    }
}
