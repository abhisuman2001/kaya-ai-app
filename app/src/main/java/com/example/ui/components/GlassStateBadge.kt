package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.GlassAiState
import com.example.ui.theme.KayaColors
import com.example.ui.theme.KayaMotion
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeXLarge

private fun GlassAiState.toKayaColor(colors: KayaColors): Color = when (this) {
    GlassAiState.IDLE -> colors.glass.idle
    GlassAiState.CONNECTED -> colors.glass.connected
    GlassAiState.LISTENING -> colors.glass.listening
    GlassAiState.THINKING -> colors.glass.thinking
    GlassAiState.ANALYZING -> colors.glass.analyzing
    GlassAiState.SPEAKING -> colors.glass.speaking
    GlassAiState.CHARGING -> colors.glass.charging
    GlassAiState.OFFLINE -> colors.glass.offline
}

private fun GlassAiState.transitionDurationMs(): Int = when (this) {
    GlassAiState.LISTENING, GlassAiState.THINKING, GlassAiState.ANALYZING, GlassAiState.SPEAKING ->
        KayaMotion.DURATION_LIVE_MS
    GlassAiState.CONNECTED, GlassAiState.CHARGING -> KayaMotion.DURATION_IDLE_ALIVE_MS
    GlassAiState.IDLE, GlassAiState.OFFLINE -> KayaMotion.DURATION_AMBIENT_SCAN_MS
}

/**
 * One semantic colour per Glass machine state, driving glow (background tint) + border +
 * badge + dot together via a single animateColorAsState — they never fall out of sync
 * because they all animate from the same value.
 */
@Composable
fun GlassStateBadge(
    state: GlassAiState,
    modifier: Modifier = Modifier
) {
    val kayaColors = LocalKayaColors.current
    val animatedColor by animateColorAsState(
        targetValue = state.toKayaColor(kayaColors),
        animationSpec = tween(durationMillis = state.transitionDurationMs()),
        label = "glass_state_color"
    )

    Row(
        modifier = modifier
            .background(color = animatedColor.copy(alpha = 0.15f), shape = ShapeXLarge)
            .border(width = 1.dp, color = animatedColor, shape = ShapeXLarge)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        StatusDot(color = animatedColor)
        Text(
            text = state.label,
            style = MaterialTheme.typography.labelLarge,
            color = animatedColor
        )
    }
}
