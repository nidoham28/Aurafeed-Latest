package com.nidoham.aurafeed.features.shell.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Aurafeed — Shell
 * ───────────────────────────────────────────────────────────
 *  [ShellScaffold] is what the auth layout wraps itself in.
 *
 *  Deliberately plain: background colour and safe-drawing insets, no chrome.
 *  If your real shell lives elsewhere, delete this and fix the import in
 *  [com.nidoham.aurafeed.features.auth.component.AuthLayout] instead.
 *
 *  v2: fills the window — without [fillMaxSize] the scaffold could collapse
 *  to wrap-content height on some targets and the auth layout's vertical
 *  centring would never engage.
 */

@Composable
fun ShellScaffold(
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        // Insets only — the auth layout handles its own ime padding so the
        // keyboard pushes the focused field, not the whole layout.
        contentWindowInsets = WindowInsets.safeDrawing,
        content = content,
    )
}

@Composable
fun ShellScreen() {
    ShellScaffold { }
}