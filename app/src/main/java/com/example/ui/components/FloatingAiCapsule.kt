package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeCircle

/** Mirrors v1's `AIState` — the capsule's four resting/active states. */
enum class AiCapsuleState { Ready, Listening, Thinking, Responding }

/**
 * v1's floating AI capsule, ported to Compose.
 *
 * A round accent button that hovers above the content. **Tap** opens Live AI; **long-press**
 * starts a voice session — the same two-gesture contract v1 uses. While listening it shows a
 * four-bar waveform, and while thinking a slow breathing pulse.
 *
 * Sized 64dp (v1 uses 72px) so it clears the floating nav without crowding it.
 */
@Composable
fun FloatingAiCapsule(
    state: AiCapsuleState,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalKayaColors.current.accent
    var pressed by remember { mutableStateOf(false) }

    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(140),
        label = "capsule_press"
    )

    val transition = rememberInfiniteTransition(label = "capsule")
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == AiCapsuleState.Thinking) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "capsule_breathe"
    )

    Box(
        modifier = modifier
            .size(64.dp)
            .scale(pressScale * breathe)
            .background(accent, ShapeCircle)
            .pointerInput(state) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { onTap() },
                    onLongPress = { onLongPress() }
                )
            }
            .testTag("floating_ai_capsule"),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            AiCapsuleState.Listening -> Waveform(color = Color.White)
            AiCapsuleState.Thinking -> Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Kaya AI is thinking",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            else -> Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Ask Kaya AI",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/** Four bars rising and falling out of phase — v1's `animate-wave` with staggered delays. */
@Composable
private fun Waveform(color: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Distinct durations rather than delays: co-prime periods keep the bars from
        // ever re-syncing into a single pulse.
        listOf(760, 900, 680, 840).forEach { period ->
            val scale by transition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(period),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "wave_bar_$period"
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((16 * scale).dp)
                    .background(color, ShapeCircle)
            )
        }
    }
}

/** Label shown beside the capsule when it is doing something. */
@Composable
fun AiCapsuleStatusLabel(state: AiCapsuleState, modifier: Modifier = Modifier) {
    val text = when (state) {
        AiCapsuleState.Ready -> return
        AiCapsuleState.Listening -> "Listening…"
        AiCapsuleState.Thinking -> "Thinking…"
        AiCapsuleState.Responding -> "Responding"
    }
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, ShapeCircle)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
