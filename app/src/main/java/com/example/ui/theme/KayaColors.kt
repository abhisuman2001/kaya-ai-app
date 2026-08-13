package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Status colours for hazard/report/task states. Theme-aware: the dark variants are
 * lifted so they stay legible on a #111 background.
 */
data class KayaStatusColors(
    val success: Color,
    val warning: Color,
    val error: Color,
    val info: Color
)

/** One colour per Glass machine state, driving glow + border + badge + dot together. */
data class KayaGlassColors(
    val idle: Color,
    val connected: Color,
    val listening: Color,
    val thinking: Color,
    val analyzing: Color,
    val speaking: Color,
    val charging: Color,
    val offline: Color
)

/**
 * Tokens that Material 3's ColorScheme has no slot for.
 *
 * [accent] is the important one: M3 `primary` carries *ink* in this design, so the blue
 * needs its own home. Reach for [accent] on small emphasis surfaces — active nav icons,
 * progress arcs, link text, tinted chips — never on large fills.
 */
data class KayaColors(
    val accent: Color,
    val hairline: Color,
    val muted: Color,
    val mutedForeground: Color,
    val status: KayaStatusColors,
    val glass: KayaGlassColors
)

val LightKayaColors = KayaColors(
    accent = KayaAccent,
    hairline = KayaLightHairline,
    muted = KayaLightMuted,
    mutedForeground = KayaLightMutedForeground,
    status = KayaStatusColors(
        success = KayaSuccess,
        warning = KayaWarning,
        error = KayaDestructive,
        info = KayaInfo
    ),
    glass = KayaGlassColors(
        idle = KayaGlassIdle,
        connected = KayaGlassConnected,
        listening = KayaGlassListening,
        thinking = KayaGlassThinking,
        analyzing = KayaGlassAnalyzing,
        speaking = KayaGlassSpeaking,
        charging = KayaGlassCharging,
        offline = KayaGlassOffline
    )
)

val DarkKayaColors = LightKayaColors.copy(
    accent = KayaAccentDark,
    hairline = KayaDarkHairline,
    muted = KayaDarkMuted,
    mutedForeground = KayaDarkMutedForeground,
    status = KayaStatusColors(
        success = KayaSuccessDark,
        warning = KayaWarningDark,
        error = KayaDestructiveDark,
        info = KayaAccentDark
    )
)

/** Defaults to the light set, matching the app's default theme. */
val LocalKayaColors = staticCompositionLocalOf { LightKayaColors }

@Deprecated("Kept for source compatibility; prefer LightKayaColors / DarkKayaColors.")
val DefaultKayaColors = LightKayaColors
