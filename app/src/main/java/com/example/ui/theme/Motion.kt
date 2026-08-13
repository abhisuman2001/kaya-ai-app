package com.example.ui.theme

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

/**
 * Duration-as-semantics: how long an animation takes encodes what it means. An alarm should
 * feel urgent; ambient scanning should feel patient. Centralised here so applying motion
 * consistently across screens later is a config change, not a rewrite.
 */
object KayaMotion {
    /** Urgent, attention-grabbing states (hazard alarms). */
    const val DURATION_ALARM_MS = 500

    /** Live/active states (glasses connected, streaming). */
    const val DURATION_LIVE_MS = 800

    /** Idle-but-alive breathing states (connected, waiting). */
    const val DURATION_IDLE_ALIVE_MS = 1100

    /** Ambient/background scanning states (low urgency). */
    const val DURATION_AMBIENT_SCAN_MS = 2500

    // Pulse periods for concentric rings/dots. Deliberately non-shared factors so rings
    // drift out of phase with each other instead of visually pulsing in lockstep.
    private val PULSE_PERIODS_MS = listOf(500, 600, 800, 900, 700)

    /** Returns [ringCount] pulse periods, no two adjacent rings sharing a period. */
    fun pulseDurationsMs(ringCount: Int): List<Int> =
        List(ringCount) { index -> PULSE_PERIODS_MS[index % PULSE_PERIODS_MS.size] }
}

/**
 * True when the user has requested reduced/no motion at the OS level
 * (Settings > Accessibility > Remove animations). Call sites should shorten or skip
 * non-essential animation when this is true.
 */
@Composable
@ReadOnlyComposable
fun rememberReducedMotionEnabled(): Boolean {
    val context = LocalContext.current
    val scale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return scale == 0f
}
