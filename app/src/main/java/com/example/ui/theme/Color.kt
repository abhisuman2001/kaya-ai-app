package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Kaya AI palette — ported from the v1 design system (`kaya-ai-v1/src/styles.css`),
 * converted from oklch to sRGB.
 *
 * The defining decision, and the one that makes v1 read as premium: **primary is ink,
 * not blue.** Primary buttons are near-black in light mode and near-white in dark mode
 * (the Apple pattern). Blue #007AFF is an *accent* — used for small pills, progress
 * arcs, links and active icons only. Painting whole surfaces blue is what made the
 * previous build look amateur.
 */

// ---- Light scheme (the default; v1's `:root`) ----
val KayaLightBackground = Color(0xFFFAFAFA)   // oklch(0.985 0 0)
val KayaLightForeground = Color(0xFF121212)   // oklch(0.16 0 0) — "ink"
val KayaLightSurface = Color(0xFFFFFFFF)      // oklch(1 0 0)
val KayaLightSecondary = Color(0xFFF5F5F5)    // oklch(0.96 0 0)
val KayaLightMuted = Color(0xFFF6F6F6)        // oklch(0.965 0 0)
val KayaLightMutedForeground = Color(0xFF767676) // oklch(0.5 0 0)
val KayaLightHairline = Color(0xFFEBEBEB)     // oklch(0.92 0 0)
val KayaLightInput = Color(0xFFF0F0F0)        // oklch(0.94 0 0)

// ---- Dark scheme (v1's `.dark`) ----
val KayaDarkBackground = Color(0xFF111111)    // oklch(0.16 0 0)
val KayaDarkForeground = Color(0xFFFAFAFA)    // oklch(0.98 0 0)
val KayaDarkSurface = Color(0xFF1C1C1C)       // oklch(0.205 0 0)
val KayaDarkSecondary = Color(0xFF2A2A2A)     // oklch(0.24 0 0)
val KayaDarkMuted = Color(0xFF2A2A2A)
val KayaDarkMutedForeground = Color(0xFFADADAD) // oklch(0.68 0 0)
val KayaDarkHairline = Color(0x14FFFFFF)      // white 8%
val KayaDarkInput = Color(0x1AFFFFFF)         // white 10%

// ---- Accent (the one saturated colour, used sparingly) ----
val KayaAccent = Color(0xFF007AFF)            // oklch(0.62 0.19 253)
val KayaAccentDark = Color(0xFF3395FF)        // oklch(0.68 0.18 253) — lifted for dark bg

// ---- Semantic status ----
val KayaSuccess = Color(0xFF34C759)
val KayaSuccessDark = Color(0xFF30D158)
val KayaWarning = Color(0xFFFF9500)
val KayaWarningDark = Color(0xFFFFA00A)
val KayaDestructive = Color(0xFFFF3B30)       // oklch(0.62 0.22 25)
val KayaDestructiveDark = Color(0xFFFF453A)   // oklch(0.68 0.2 25)
val KayaInfo = Color(0xFF007AFF)

// ---- Glass machine states ----
val KayaGlassIdle = Color(0xFF9CA3AF)
val KayaGlassConnected = KayaSuccess
val KayaGlassListening = KayaAccent
val KayaGlassThinking = Color(0xFFAF52DE)
val KayaGlassAnalyzing = Color(0xFF00C7BE)
val KayaGlassSpeaking = Color(0xFF34C759)
val KayaGlassCharging = KayaWarning
val KayaGlassOffline = KayaDestructive

// ---------------------------------------------------------------------------
// Legacy constants. Retained only so files not yet migrated keep compiling —
// they are dark-only values and produce wrong results in light mode, which is
// exactly why they are deprecated. Read colour from MaterialTheme.colorScheme
// or LocalKayaColors instead.
// ---------------------------------------------------------------------------

@Deprecated("Blue is an accent, not primary. Use LocalKayaColors.current.accent, or colorScheme.primary for ink.")
val MetaBlue = KayaAccent
@Deprecated("Use MaterialTheme.colorScheme.primaryContainer")
val MetaBlueLight = Color(0xFFE5F1FF)
@Deprecated("Use LocalKayaColors.current.glass.analyzing")
val MetaCyan = Color(0xFF00D2FF)
@Deprecated("Use MaterialTheme.colorScheme.surface")
val MetaDarkSlate = Color(0xFF111827)

@Deprecated("Use MaterialTheme.colorScheme.background", ReplaceWith("MaterialTheme.colorScheme.background"))
val SiteBackgroundLight = KayaLightBackground
@Deprecated("Use MaterialTheme.colorScheme.surface", ReplaceWith("MaterialTheme.colorScheme.surface"))
val SiteSurfaceLight = KayaLightSurface
@Deprecated("Use MaterialTheme.colorScheme.surfaceVariant", ReplaceWith("MaterialTheme.colorScheme.surfaceVariant"))
val SiteSurfaceVariantLight = KayaLightMuted

@Deprecated("Use MaterialTheme.colorScheme.background", ReplaceWith("MaterialTheme.colorScheme.background"))
val SiteBackgroundDark = KayaDarkBackground
@Deprecated("Use MaterialTheme.colorScheme.surface", ReplaceWith("MaterialTheme.colorScheme.surface"))
val SiteSurfaceDark = KayaDarkSurface
@Deprecated("Use MaterialTheme.colorScheme.surfaceVariant", ReplaceWith("MaterialTheme.colorScheme.surfaceVariant"))
val SiteSurfaceVariantDark = KayaDarkSecondary

@Deprecated("Use MaterialTheme.colorScheme.onSurface", ReplaceWith("MaterialTheme.colorScheme.onSurface"))
val TextPrimaryLight = KayaLightForeground
@Deprecated("Use MaterialTheme.colorScheme.onSurfaceVariant", ReplaceWith("MaterialTheme.colorScheme.onSurfaceVariant"))
val TextSecondaryLight = KayaLightMutedForeground
@Deprecated("Use MaterialTheme.colorScheme.onSurface", ReplaceWith("MaterialTheme.colorScheme.onSurface"))
val TextPrimaryDark = KayaDarkForeground
@Deprecated("Use MaterialTheme.colorScheme.onSurfaceVariant", ReplaceWith("MaterialTheme.colorScheme.onSurfaceVariant"))
val TextSecondaryDark = KayaDarkMutedForeground
@Deprecated("Use MaterialTheme.colorScheme.onSurfaceVariant with an alpha")
val TextMutedDark = Color(0x66FFFFFF)
@Deprecated("Use MaterialTheme.colorScheme.outline", ReplaceWith("MaterialTheme.colorScheme.outline"))
val BorderDark = KayaDarkHairline

@Deprecated("Use LocalKayaColors.current.status.success", ReplaceWith("LocalKayaColors.current.status.success"))
val StatusSuccess = KayaSuccess
@Deprecated("Use LocalKayaColors.current.status.warning", ReplaceWith("LocalKayaColors.current.status.warning"))
val StatusWarning = KayaWarning
@Deprecated("Use LocalKayaColors.current.status.error", ReplaceWith("LocalKayaColors.current.status.error"))
val StatusError = KayaDestructive
@Deprecated("Use LocalKayaColors.current.status.info", ReplaceWith("LocalKayaColors.current.status.info"))
val StatusInfo = KayaInfo

@Deprecated("Use LocalKayaColors.current.glass.idle", ReplaceWith("LocalKayaColors.current.glass.idle"))
val GlassIdle = KayaGlassIdle
@Deprecated("Use LocalKayaColors.current.glass.connected", ReplaceWith("LocalKayaColors.current.glass.connected"))
val GlassConnected = KayaGlassConnected
@Deprecated("Use LocalKayaColors.current.glass.listening", ReplaceWith("LocalKayaColors.current.glass.listening"))
val GlassListening = KayaGlassListening
@Deprecated("Use LocalKayaColors.current.glass.thinking", ReplaceWith("LocalKayaColors.current.glass.thinking"))
val GlassThinking = KayaGlassThinking
@Deprecated("Use LocalKayaColors.current.glass.analyzing", ReplaceWith("LocalKayaColors.current.glass.analyzing"))
val GlassAnalyzing = KayaGlassAnalyzing
@Deprecated("Use LocalKayaColors.current.glass.speaking", ReplaceWith("LocalKayaColors.current.glass.speaking"))
val GlassSpeaking = KayaGlassSpeaking
@Deprecated("Use LocalKayaColors.current.glass.charging", ReplaceWith("LocalKayaColors.current.glass.charging"))
val GlassCharging = KayaGlassCharging
@Deprecated("Use LocalKayaColors.current.glass.offline", ReplaceWith("LocalKayaColors.current.glass.offline"))
val GlassOffline = KayaGlassOffline
