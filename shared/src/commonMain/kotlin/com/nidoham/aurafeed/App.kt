package com.nidoham.aurafeed

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.config.SupabaseCredentials
import com.nidoham.aurafeed.core.services.SupabaseService
import com.nidoham.aurafeed.core.util.AurafeedUtil
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedTheme

/**
 * Aurafeed — App Root
 * ───────────────────────────────────────────────────────────
 *  Single composable entry point shared by every target's platform `main()`:
 *  Android `MainActivity`, iOS `MainViewController`, and the Desktop (JVM)
 *  `main()` all call [App] directly. It is the one place that decides which
 *  [AurafeedFormFactor] the rest of the app should render for, based on the
 *  real available window width — not the target platform itself, since a
 *  large Android tablet or a small desktop window can both need [AurafeedFormFactor.Tablet].
 *
 *  This is also where [SupabaseService] gets bootstrapped, once, before any
 *  themed UI composes — see the comment at the call site for why that's a
 *  plain `remember` and not a `LaunchedEffect`.
 *
 *  Platform target: Kotlin Compose Multiplatform — Android, iOS, Desktop (JVM) only.
 */

/** Material 3 standard window-size breakpoints (compact / medium / expanded). */
private val TabletBreakpoint: Dp = 600.dp
private val DesktopBreakpoint: Dp = 840.dp

@Composable
fun App() {
    // SupabaseService.initialize() is a plain synchronous, idempotent call —
    // not a suspend function — so it belongs in `remember`, not
    // `LaunchedEffect`. `remember` runs inline during this composition,
    // before Compose recurses into any children below, guaranteeing the
    // client exists before AurafeedApp (or anything it composes) could ever
    // read SupabaseService.client. A LaunchedEffect only runs *after* the
    // first composition completes, which would leave a real — if narrow —
    // window where a child composed on that first pass could read the
    // client before it exists and crash.
    remember {
        SupabaseService.initialize(
            url = SupabaseCredentials.url,
            anonKey = SupabaseCredentials.anonKey,
        )
    }

    // fillMaxSize is required here: BoxWithConstraints only reports the real
    // window/screen width in `maxWidth` when it is actually asked to fill the
    // available space. Without this modifier it sizes to its content instead,
    // and formFactor detection below would be measuring the wrong thing.
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val formFactor = remember(maxWidth) { resolveFormFactor(maxWidth) }

        // Publish the same resolved formFactor to AurafeedUtil so non-UI code
        // (ViewModels, platform bridges) can observe DESKTOP/MOBILE without
        // needing a composition. SideEffect runs after a successful
        // composition; updateFormFactor no-ops internally when unchanged.
        SideEffect {
            AurafeedUtil.updateFormFactor(formFactor)
        }

        AurafeedTheme(formFactor = formFactor) {
            AurafeedApp()
        }
    }
}

/**
 * Maps an available width to an [AurafeedFormFactor] using Material 3's
 * standard compact/medium/expanded breakpoints. Pulled out as a plain
 * function (rather than inlined in a `when`) so it's independently testable
 * without needing a composition.
 */
private fun resolveFormFactor(width: Dp): AurafeedFormFactor = when {
    width >= DesktopBreakpoint -> AurafeedFormFactor.Desktop
    width >= TabletBreakpoint -> AurafeedFormFactor.Tablet
    else -> AurafeedFormFactor.Mobile
}