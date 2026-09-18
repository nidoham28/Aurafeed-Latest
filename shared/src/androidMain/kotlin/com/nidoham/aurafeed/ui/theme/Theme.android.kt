package com.nidoham.aurafeed.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Android `actual` — tints the status bar to match the current theme
 * background and switches status-bar icon contrast (light vs. dark icons)
 * so content stays legible over any background color.
 *
 * Uses `WindowCompat.getInsetsController` (the current, non-deprecated
 * AndroidX API) instead of the deprecated `View.setSystemUiVisibility` /
 * `SYSTEM_UI_FLAG_LIGHT_STATUS_BAR` flags.
 *
 * No-ops safely in design-time preview / edit mode, where there is no
 * attached [Activity].
 */
@Composable
actual fun PlatformSystemBars(background: Color, useDarkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    val activity = view.context as? Activity ?: return
    val window = activity.window

    SideEffect {
        window.statusBarColor = background.toArgb()
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = useDarkIcons
            isAppearanceLightNavigationBars = useDarkIcons
        }
    }
}