package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** A single stat tile: a large value over a small label, optionally tinted (e.g. hazard count). */
@Composable
fun MetricTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    HairlineCard(modifier = modifier, contentPadding = PaddingValues(12.dp)) {
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = valueColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
