package com.nidoham.aurafeed.ui.platform

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op or custom gesture back on iOS
}
