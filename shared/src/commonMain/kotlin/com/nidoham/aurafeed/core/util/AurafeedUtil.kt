package com.nidoham.aurafeed.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nidoham.aurafeed.ui.theme.AurafeedFormFactor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Aurafeed — Screen Mode Utility
 * ───────────────────────────────────────────────────────────
 *  Binary DESKTOP / MOBILE screen-mode signal, derived from the app's single
 *  source of truth for responsive layout — [AurafeedFormFactor] — which is
 *  computed once, at the root, from the real measured window width (see
 *  `App.kt`). [AurafeedUtil] does not measure anything itself; it only
 *  re-publishes that decision as:
 *
 *   1. A [StateFlow] ([AurafeedUtil.screenMode]) for idiomatic Kotlin/coroutines
 *      consumers (ViewModels, repositories, platform bridges) on every target.
 *   2. A plain callback API ([AurafeedUtil.addListener]) for call sites that
 *      don't want to touch coroutines at all — e.g. iOS/Android interop code
 *      that just wants "tell me when it changes".
 *   3. A [rememberAurafeedScreenMode] composable for UI code that wants the
 *      binary signal reactively without pulling in the whole theme.
 *
 *  [AurafeedFormFactor.Tablet] collapses to [AurafeedScreenMode.MOBILE] by
 *  default (touch-first, single-pane-capable) since this utility is
 *  intentionally binary — pass `tabletIsDesktop = true` where a given screen
 *  wants tablets treated as desktop instead.
 *
 *  Platform target: Kotlin Compose Multiplatform — Android, iOS, Desktop (JVM)
 *  only. Pure `commonMain`; no platform-specific or deprecated APIs.
 *
 *  Thread-safety: [AurafeedUtil.updateFormFactor] must only be called from the
 *  main/UI thread — it is driven by composition, same as all Compose state.
 *  Reads of [AurafeedUtil.screenMode] / [AurafeedUtil.currentScreenMode] are
 *  safe from any thread; [StateFlow] guarantees that.
 */

/** Binary screen mode Aurafeed renders for. */
enum class AurafeedScreenMode {
    MOBILE,
    DESKTOP;

    companion object {
        /** Collapses the 3-tier [AurafeedFormFactor] into a binary [AurafeedScreenMode]. */
        fun from(formFactor: AurafeedFormFactor, tabletIsDesktop: Boolean = false): AurafeedScreenMode =
            when (formFactor) {
                AurafeedFormFactor.Desktop -> DESKTOP
                AurafeedFormFactor.Tablet -> if (tabletIsDesktop) DESKTOP else MOBILE
                AurafeedFormFactor.Mobile -> MOBILE
            }
    }
}

/**
 * SAM callback for imperative (non-coroutine) screen-mode listeners, e.g.:
 * ```
 * val unregister = AurafeedUtil.addListener { mode ->
 *     println("Screen mode is now $mode")
 * }
 * // later: unregister()
 * ```
 */
fun interface AurafeedScreenModeCallback {
    fun onScreenModeChanged(mode: AurafeedScreenMode)
}

/**
 * Process-wide, reactive DESKTOP / MOBILE signal. See the file-level doc for
 * how this fits into the wider Aurafeed theming/layout system.
 */
object AurafeedUtil {

    private val _screenMode = MutableStateFlow(AurafeedScreenMode.MOBILE)

    /** Observe screen-mode changes as a hot [StateFlow]; always has a current value. */
    val screenMode: StateFlow<AurafeedScreenMode> = _screenMode.asStateFlow()

    /** Latest known screen mode. Safe to read from any thread. */
    val currentScreenMode: AurafeedScreenMode get() = _screenMode.value

    val isDesktop: Boolean get() = currentScreenMode == AurafeedScreenMode.DESKTOP
    val isMobile: Boolean get() = currentScreenMode == AurafeedScreenMode.MOBILE

    private val listeners = mutableListOf<AurafeedScreenModeCallback>()

    /**
     * Registers [callback] and immediately invokes it once with the current
     * value, matching [StateFlow]'s "always has a value" semantics — so
     * callers never have to special-case "what's the value right now?".
     *
     * @return an unregister function. Call it (e.g. in `onDispose`, `deinit`,
     *   or a ViewModel's `onCleared`) to stop receiving callbacks and avoid
     *   leaking the listener.
     */
    fun addListener(callback: AurafeedScreenModeCallback): () -> Unit {
        callback.onScreenModeChanged(currentScreenMode)
        listeners += callback
        return { listeners -= callback }
    }

    /**
     * Updates the published screen mode from a freshly resolved
     * [AurafeedFormFactor]. Called once, at the app root (see `App.kt`), so
     * there is a single source of truth shared by [AurafeedTheme] and this
     * utility. No-ops (and never notifies listeners) if the resolved mode
     * hasn't actually changed, so it is safe to call on every recomposition.
     *
     * Screens should not call this directly — it is app-root plumbing.
     */
    fun updateFormFactor(formFactor: AurafeedFormFactor, tabletIsDesktop: Boolean = false) {
        val next = AurafeedScreenMode.from(formFactor, tabletIsDesktop)
        if (next == _screenMode.value) return
        _screenMode.value = next
        listeners.forEach { it.onScreenModeChanged(next) }
    }
}

/**
 * Reactive Compose accessor for [AurafeedUtil.screenMode]. Prefer reading
 * [AurafeedFormFactor] via `AurafeedTheme.formFactor` for layout decisions
 * inside themed UI; use this when a composable needs the plain binary
 * signal without pulling in the whole theme (e.g. an un-themed root-level
 * branch, or a widget shared with a non-Aurafeed host).
 */
@Composable
fun rememberAurafeedScreenMode(): AurafeedScreenMode {
    val mode by AurafeedUtil.screenMode.collectAsState()
    return mode
}