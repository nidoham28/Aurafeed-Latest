package com.nidoham.aurafeed

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nidoham.aurafeed.core.navigation.AurafeedNavHost
import com.nidoham.aurafeed.core.navigation.AurafeedNavigationManager
import com.nidoham.aurafeed.core.navigation.MutableScreenRegistry
import com.nidoham.aurafeed.core.navigation.NavigationManager
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import com.nidoham.aurafeed.ui.theme.AurafeedTheme

/**
 * Aurafeed — Root App Composable
 * ───────────────────────────────────────────────────────────
 *  Production launch shell. Wires [AurafeedTheme] → [AurafeedNavHost] so
 *  the whole app shares one theme + one navigation graph.
 *
 *  Per project rules:
 *   • Mobile + Desktop are separate variants — auto-detected at runtime
 *     via [BoxWithConstraints] on window width (the NavHost then renders
 *     a bottom-bar on Mobile vs a sidebar on Desktop, all from one graph).
 *   • No plain spinner / loader anywhere — the NavHost uses shimmer
 *     skeletons for every unwired route (see ui/components/shimmer).
 *   • Never trust the client — the [NavigationManager] auth-gates routes
 *     via [NavigationManager.setAuthGate]; the auth repository should
 *     install that gate at app startup (see wiring in platform entry points).
 */

@Composable
fun App() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val widthDp = maxWidth
        val formFactor = when {
            widthDp < 600.dp -> AurafeedFormFactor.Mobile
            widthDp < 840.dp -> AurafeedFormFactor.Tablet
            else -> AurafeedFormFactor.Desktop
        }

        // Single NavigationManager instance per app session.
        // The auth repository should call `navManager.setAuthGate { route -> ... }`
        // once the session state resolves.
        val navManager: NavigationManager = remember { AurafeedNavigationManager() }

        // Single ScreenRegistry instance — extend at app startup by registering
        // real screen implementations per Route subtype.
        val registry = remember {
            MutableScreenRegistry().apply {
                // ── Wire real screens here as you build them ─────────────
                // register<Route.Feed> { FeedScreen() }
                // register<Route.Explore> { ExploreScreen() }
                // register<Route.ProfileView> { route -> ProfileScreen(route.userId) }
                // register<Route.PostDetail> { route -> PostDetailScreen(route.postId) }
                // register<Route.DmThread> { route -> DmThreadScreen(route.threadId) }
                // ... etc.
                //
                // Until screens are wired, the NavHost shows shimmer placeholders,
                // so the app remains visually complete during development.
            }
        }

        AurafeedTheme(formFactor = formFactor) {
            AurafeedNavHost(
                navManager = navManager,
                registry = registry,
            )
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    App()
}