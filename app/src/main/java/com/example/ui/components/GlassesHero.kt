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
import androidx.compose.foundation.layout.padding
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

/** Presentation size for [GlassesHero]. Mirrors v1's `size` prop. */
enum class GlassesHeroSize(val imageHeight: Dp, val glowSize: Dp) {
    Small(96.dp, 180.dp),
    Medium(132.dp, 240.dp),
    Large(168.dp, 300.dp)
}

/**
 * The Ray-Ban Meta product shot from v1, floating over a soft accent glow.
 *
 * This replaces a hand-drawn Canvas approximation of the glasses. Real product
 * photography is the single biggest credibility win on the home screen — a vector
 * sketch of a consumer device always reads as a placeholder.
 *
 * The float and glow-breathe are slow (6s / 4s) so the hero feels alive without
 * competing with the content around it.
 */
@Composable
fun GlassesHero(
    modifier: Modifier = Modifier,
    size: GlassesHeroSize = GlassesHeroSize.Medium,
    isConnected: Boolean = true,
    animated: Boolean = true
) {
    val accent = LocalKayaColors.current.accent
    val transition = rememberInfiniteTransition(label = "glasses_hero")

    val floatOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val glowAlpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = if (animated && isConnected) 0.75f else 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(size.imageHeight + 48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Radial accent glow behind the product — fades to transparent so it reads as
        // light rather than a coloured disc.
        Box(
            modifier = Modifier
                .size(size.glowSize)
                .alpha(glowAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.30f),
                            accent.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )

        Image(
            painter = painterResource(id = R.drawable.rayban_meta),
            contentDescription = "Ray-Ban Meta smart glasses",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(size.imageHeight)
                .padding(horizontal = 16.dp)
                // A few px of drift; enough to feel buoyant, small enough not to distract.
                .graphicsLayer { translationY = floatOffset * 8f }
        )
    }
}
