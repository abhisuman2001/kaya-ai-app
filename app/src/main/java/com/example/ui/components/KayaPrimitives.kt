package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KayaTextStyles
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeCircle
import com.example.ui.theme.ShapeSmall
import com.example.ui.theme.ShapeXLarge

/**
 * Primitives ported from the v1 design system. Each mirrors a specific pattern from
 * `kaya-ai-v1` rather than being a generic Material widget.
 */

/** v1's `.text-title` section heading — 22sp semibold, tight tracking. */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        trailing?.invoke()
    }
}

/** v1's `.text-eyebrow` — uppercase, wide tracking, muted. Sits above a title. */
@Composable
fun Eyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text.uppercase(),
        style = KayaTextStyles.Eyebrow,
        color = color,
        modifier = modifier
    )
}

/**
 * A stat cell on a muted ground — v1's `rounded-xl bg-muted/60 p-3`: value at 18sp
 * semibold over a 10sp uppercase caption.
 */
@Composable
fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    tone: Color? = null
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, ShapeSmall)
            .padding(horizontal = 12.dp, vertical = 11.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = tone ?: MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

/** v1's quick-action tile: hairline card holding a muted icon chip over a small label. */
@Composable
fun QuickActionTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, ShapeSmall)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), ShapeSmall)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, ShapeSmall),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * The circular progress dial from v1's project card — a muted track with an accent arc,
 * percentage centred inside. Starts at 12 o'clock and sweeps clockwise.
 */
@Composable
fun ProgressRing(
    percent: Int,
    modifier: Modifier = Modifier,
    diameter: Dp = 58.dp,
    strokeWidth: Dp = 4.dp
) {
    val accent = LocalKayaColors.current.accent
    val track = MaterialTheme.colorScheme.surfaceVariant
    val clamped = percent.coerceIn(0, 100)

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val inset = stroke.width / 2
            val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
            drawArc(
                color = track,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = stroke
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * (clamped / 100f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = stroke
            )
        }
        Text(
            text = "$clamped%",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * v1's device status strip: battery, storage and sync as three compact readouts
 * separated by hairlines.
 */
@Composable
fun DeviceStatusRow(
    battery: Int,
    storagePercent: Int,
    isSyncing: Boolean,
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val kaya = LocalKayaColors.current
    val batteryTone = when {
        !isConnected -> MaterialTheme.colorScheme.onSurfaceVariant
        battery <= 20 -> kaya.status.error
        battery <= 50 -> kaya.status.warning
        else -> kaya.status.success
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, ShapeSmall)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusReadout(
            label = "Battery",
            value = if (isConnected) "$battery%" else "—",
            tone = batteryTone
        )
        HairlineDivider()
        StatusReadout(
            label = "Storage",
            value = if (isConnected) "$storagePercent%" else "—"
        )
        HairlineDivider()
        StatusReadout(
            label = "Sync",
            value = when {
                !isConnected -> "Offline"
                isSyncing -> "Syncing"
                else -> "Synced"
            },
            tone = if (isConnected && !isSyncing) kaya.status.success else null
        )
    }
}

@Composable
private fun StatusReadout(label: String, value: String, tone: Color? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = tone ?: MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun HairlineDivider() {
    Box(
        modifier = Modifier
            .height(26.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outline)
    )
}

/** Small tinted status pill — v1 uses these for counts and short state labels. */
@Composable
fun TintPill(
    text: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(tint.copy(alpha = 0.15f), ShapeCircle)
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            maxLines = 1
        )
    }
}

/** Full-bleed hero surface used at the top of detail screens. */
@Composable
fun HeroSurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, ShapeXLarge)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), ShapeXLarge)
            .padding(contentPadding)
    ) {
        content()
    }
}
