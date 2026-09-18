package com.nidoham.aurafeed.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * iOS `actual` — intentional no-op.
 *
 * iOS has no per-app-settable "status bar color"; the status bar is
 * transparent by default and its *style* (`.lightContent` / `.darkContent`)
 * is owned by the hosting `UIViewController` (set via `preferredStatusBarStyle`
 * in the Swift/UIKit host, or `View.statusBarHidden`/`.preferredColorScheme`
 * if the host is SwiftUI). Compose Multiplatform on iOS does not own that
 * controller, so there is nothing safe to mutate from common code here.
 *
 * If per-screen status-bar style control is needed, expose it from the
 * iOS host app (e.g. via a small platform bridge) rather than from this
 * theme layer.
 */
@Composable
actual fun PlatformSystemBars(background: Color, useDarkIcons: Boolean) {
    // Intentional no-op — see class doc above.
}