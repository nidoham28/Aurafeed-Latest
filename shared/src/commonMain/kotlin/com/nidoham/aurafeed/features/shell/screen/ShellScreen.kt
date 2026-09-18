package com.nidoham.aurafeed.features.shell.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Aurafeed — Shell
 * ───────────────────────────────────────────────────────────
 *  [ShellScaffold] is what LoginScreen / RegisterScreen wrap themselves in.
 *  The uploaded ShellScreen.kt was an empty stub while both auth screens
 *  already called ShellScaffold, so nothing compiled — this is the minimum
 *  that makes them build.
 *
 *  Deliberately plain: background colour and safe-drawing insets, no chrome.
 *  If your real shell lives elsewhere, delete this and fix the import in the
 *  auth screens instead.
 */

@Composable
fun ShellScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        // Insets only — the screens handle their own ime padding so the
        // keyboard pushes the focused field, not the whole layout.
        contentWindowInsets = WindowInsets.safeDrawing,
        content = content,
    )
}

@Composable
fun ShellScreen() {
    ShellScaffold { }
}