package com.nidoham.aurafeed.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Desktop (JVM) `actual` — intentional no-op.
 *
 * Desktop windows (Windows / macOS / Linux) have no OS-level "status bar" —
 * only an optional title bar, which is owned by the `Window`/`ComposeWindow`
 * composable in the desktop entry point, not by the theme layer. If a custom
 * title-bar tint is desired, set it where the `Window { }` is created (see
 * the desktop `main()` entry point), using `WindowState`/`Window` params.
 */
@Composable
actual fun PlatformSystemBars(background: Color, useDarkIcons: Boolean) {
    // Intentional no-op — see class doc above.
}