package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalKayaColors

/** A circular 0-100 score arc (quality/safety scores), coloured by band. */
@Composable
fun ScoreGauge(
    score: Int,
    modifier: Modifier = Modifier,
    diameter: Dp = 64.dp,
    strokeWidth: Dp = 6.dp,
    label: String? = null
) {
    val kayaColors = LocalKayaColors.current
    val arcColor = when {
        score >= 80 -> kayaColors.status.success
        score >= 50 -> kayaColors.status.warning
        else -> kayaColors.status.error
    }
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = Stroke(width = strokeWidth.toPx())
            val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
            val topLeft = Offset(stroke.width / 2, stroke.width / 2)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = 360f * (score.coerceIn(0, 100) / 100f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
        }
        Text(
            text = label ?: score.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
