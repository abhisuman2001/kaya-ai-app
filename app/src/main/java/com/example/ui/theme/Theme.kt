package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Kaya AI theme, ported from the v1 design system.
 *
 * The mapping to worth knowing: **`primary` is ink, not blue** — near-black in light,
 * near-white in dark, so primary buttons render as solid ink the way they do in v1.
 * The blue accent lives on `LocalKayaColors.current.accent`. `surfaceVariant` carries
 * v1's `muted` (the soft grey behind stat tiles and icon chips), and `outline` carries
 * the hairline.
 */
private val LightColorScheme = lightColorScheme(
    primary = KayaLightForeground,
    onPrimary = KayaLightSurface,
    primaryContainer = KayaLightSecondary,
    onPrimaryContainer = KayaLightForeground,
    secondary = KayaLightForeground,
    onSecondary = KayaLightSurface,
    secondaryContainer = KayaLightMuted,
    onSecondaryContainer = KayaLightForeground,
    tertiary = KayaAccent,
    onTertiary = KayaLightSurface,
    background = KayaLightBackground,
    onBackground = KayaLightForeground,
    surface = KayaLightSurface,
    onSurface = KayaLightForeground,
    surfaceVariant = KayaLightMuted,
    onSurfaceVariant = KayaLightMutedForeground,
    surfaceContainer = KayaLightMuted,
    surfaceContainerHigh = KayaLightSecondary,
    outline = KayaLightHairline,
    outlineVariant = KayaLightHairline,
    error = KayaDestructive,
    onError = KayaLightSurface,
    scrim = KayaLightForeground
)

private val DarkColorScheme = darkColorScheme(
    primary = KayaDarkForeground,
    onPrimary = KayaDarkBackground,
    primaryContainer = KayaDarkSecondary,
    onPrimaryContainer = KayaDarkForeground,
    secondary = KayaDarkForeground,
    onSecondary = KayaDarkBackground,
    secondaryContainer = KayaDarkMuted,
    onSecondaryContainer = KayaDarkForeground,
    tertiary = KayaAccentDark,
    onTertiary = KayaDarkBackground,
    background = KayaDarkBackground,
    onBackground = KayaDarkForeground,
    surface = KayaDarkSurface,
    onSurface = KayaDarkForeground,
    surfaceVariant = KayaDarkMuted,
    onSurfaceVariant = KayaDarkMutedForeground,
    surfaceContainer = KayaDarkSurface,
    surfaceContainerHigh = KayaDarkSecondary,
    outline = KayaDarkHairline,
    outlineVariant = KayaDarkHairline,
    error = KayaDestructiveDark,
    onError = KayaDarkForeground,
    scrim = KayaDarkBackground
)

/**
 * @param darkTheme defaults to **false** — v1 is a light-first product (`:root` is the
 *   light scheme, `.dark` is the opt-in). The user's stored preference still wins;
 *   `MainActivity` passes it in.
 */
@Composable
fun SiteMindTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false, // Kaya has a fixed brand identity; never follow the wallpaper.
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val kayaColors = if (darkTheme) DarkKayaColors else LightKayaColors

    CompositionLocalProvider(LocalKayaColors provides kayaColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = KayaShapes,
            content = content
        )
    }
}
