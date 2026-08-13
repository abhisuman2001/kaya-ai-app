package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.LocalKayaColors

/** Which artwork the brand hero shows. */
enum class KayaHeroArt {
    /** Site helmet — the primary Kaya AI brand image. Construction first. */
    Helmet,
    /** Abstract AI core — for AI/analysis moments. */
    AiCore,
    /** Blueprint sheet — for plan/CAD moments. */
    Blueprint
}

enum class KayaHeroSize(val artSize: Dp, val glowSize: Dp) {
    Small(92.dp, 150.dp),
    Medium(128.dp, 210.dp),
    Large(164.dp, 270.dp)
}

/**
 * The Kaya AI brand hero: construction artwork floating over a soft accent glow.
 *
 * Deliberately **not** the Ray-Ban product shot. The glasses are one supported device, not
 * the product — leading with them made the app read as a Ray-Ban accessory rather than a
 * construction-intelligence tool. The glasses now appear only where the device itself is
 * the subject (pairing, device status).
 */
@Composable
fun KayaBrandHero(
    modifier: Modifier = Modifier,
    art: KayaHeroArt = KayaHeroArt.Helmet,
    size: KayaHeroSize = KayaHeroSize.Medium,
    animated: Boolean = true
) {
    val accent = LocalKayaColors.current.accent
    val transition = rememberInfiniteTransition(label = "brand_hero")

    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(6000), RepeatMode.Reverse),
        label = "float"
    )
    val glow by transition.animateFloat(
        initialValue = 0.30f,
        targetValue = if (animated) 0.65f else 0.30f,
        animationSpec = infiniteRepeatable(tween(4000), RepeatMode.Reverse),
        label = "glow"
    )

    val drawable = when (art) {
        KayaHeroArt.Helmet -> R.drawable.ws_helmet
        KayaHeroArt.AiCore -> R.drawable.ws_ai_core
        KayaHeroArt.Blueprint -> R.drawable.ws_blueprint
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(size.artSize + 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size.glowSize)
                .alpha(glow)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.28f),
                            accent.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Image(
            painter = painterResource(id = drawable),
            contentDescription = when (art) {
                KayaHeroArt.Helmet -> "Kaya AI"
                KayaHeroArt.AiCore -> "Kaya AI analysis"
                KayaHeroArt.Blueprint -> "Site plans"
            },
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size.artSize)
                .graphicsLayer { translationY = float * 7f }
        )
    }
}
