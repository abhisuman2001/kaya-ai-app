package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeXSmall

/** A small battery glyph (body + nub) filled proportionally to [percent], coloured by level. */
@Composable
fun BatteryMeter(
    percent: Int,
    isCharging: Boolean = false,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val kayaColors = LocalKayaColors.current
    val fillColor = when {
        isCharging -> kayaColors.glass.charging
        percent <= 20 -> kayaColors.status.error
        percent <= 50 -> kayaColors.status.warning
        else -> kayaColors.status.success
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(11.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = ShapeXSmall
                    )
                    .padding(1.5.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((20 * (percent.coerceIn(0, 100) / 100f)).dp)
                        .background(color = fillColor, shape = ShapeXSmall)
                )
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(5.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        if (showLabel) {
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
