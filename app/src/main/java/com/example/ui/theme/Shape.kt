package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Radii ported from v1 (`--radius-sm` … `--radius-3xl`). Markedly rounder than the
 * 6/8/12/16 scale this app used before — the softness is a large part of why v1 reads
 * as a finished consumer product rather than a dashboard.
 *
 * Rule of thumb from v1's markup: interactive rows and small cards 20dp, content cards
 * 24–28dp, hero/sheet surfaces 28–36dp, buttons and chips fully round.
 */
val ShapeXSmall = RoundedCornerShape(10.dp)   // --radius-sm
val ShapeSmall = RoundedCornerShape(14.dp)    // --radius-md
val ShapeMedium = RoundedCornerShape(20.dp)   // --radius-lg
val ShapeLarge = RoundedCornerShape(24.dp)    // between lg and xl; v1's card default
val ShapeXLarge = RoundedCornerShape(28.dp)   // --radius-xl — hero cards
val ShapeXXLarge = RoundedCornerShape(36.dp)  // --radius-2xl — sheets
val ShapeXXXLarge = RoundedCornerShape(44.dp) // --radius-3xl
val ShapeCircle = CircleShape                 // pills, avatars, the floating nav

val KayaShapes = Shapes(
    extraSmall = ShapeXSmall,
    small = ShapeSmall,
    medium = ShapeMedium,
    large = ShapeLarge,
    extraLarge = ShapeXLarge
)
