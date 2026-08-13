package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Type scale ported from v1. Two things matter more than the sizes:
 *
 * 1. **Negative tracking.** v1 sets `letter-spacing: -0.011em` on body and tightens
 *    further for titles (-0.02em) and display (-0.03em). Compose defaults to positive
 *    tracking, which is what made the previous build look loose and generic.
 * 2. **Weight 600, not 700.** v1 uses semibold throughout; bold reads as shouty next
 *    to the airy layout.
 *
 * The eyebrow style is the exception — wide positive tracking, uppercase, muted.
 * It has no M3 slot, so it lives in [KayaTextStyles].
 */
private val Sans = FontFamily.SansSerif

val Typography =
  Typography(
    // 10px — micro labels under nav icons, stat captions
    labelSmall =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
      ),
    // 11px — v1's most common secondary size
    labelMedium =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.sp,
      ),
    // 12px — supporting copy
    labelLarge =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.1).sp,
      ),
    bodySmall =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = (-0.1).sp,
      ),
    // 13px — v1's body default
    bodyMedium =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = (-0.15).sp,
      ),
    // 15px — primary reading size
    bodyLarge =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.17).sp,
      ),
    // 13px semibold — row titles
    titleSmall =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.15).sp,
      ),
    // 15px semibold — card titles
    titleMedium =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.24).sp,
      ),
    // 17px semibold — project names, prominent rows
    titleLarge =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.34).sp,
      ),
    // 22px — v1's `.text-title`, the section heading
    headlineSmall =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.44).sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.6).sp,
      ),
    headlineLarge =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.75).sp,
      ),
    // v1's `.text-display` — clamp(2rem, 8vw, 2.75rem), line-height 1.05, -0.03em
    displaySmall =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.96).sp,
      ),
    displayMedium =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 38.sp,
        lineHeight = 40.sp,
        letterSpacing = (-1.14).sp,
      ),
    displayLarge =
      TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 44.sp,
        lineHeight = 46.sp,
        letterSpacing = (-1.32).sp,
      ),
  )

/** Styles v1 has that Material 3 has no slot for. */
object KayaTextStyles {
    /** v1's `.text-eyebrow`: 11.5px, 0.18em tracking, uppercase, semibold, muted.
     *  Always pair with `.uppercase()` on the string and a muted colour. */
    val Eyebrow = TextStyle(
        fontFamily = Sans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 2.sp,
    )
}
