package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ShapeCircle

/**
 * One consolidated filter-pill row, replacing the four near-identical implementations that
 * previously lived in HazardCategoryFilterChips / ReportCategoryFilterCard /
 * NotificationCategoryFilterCard / MaterialCategoryFilterCard. Each screen supplies its own
 * item list, label, colour and optional icon; the pill visuals and selection behaviour are
 * shared. Screens keep whatever header/card chrome they already have around it.
 */
@Composable
fun <T> FilterPillRow(
    items: List<T>,
    selected: T?,
    onSelect: (T?) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
    allLabel: String? = "ALL",
    allColor: Color = MaterialTheme.colorScheme.primary,
    pillColor: @Composable (T) -> Color = { MaterialTheme.colorScheme.primary },
    icon: ((T) -> ImageVector)? = null,
    testTagPrefix: String = "filter_pill"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (allLabel != null) {
            FilterPillItem(
                label = allLabel,
                isSelected = selected == null,
                color = allColor,
                onClick = { onSelect(null) },
                testTag = "${testTagPrefix}_all"
            )
        }
        items.forEach { item ->
            val isSelected = item == selected
            FilterPillItem(
                label = label(item),
                isSelected = isSelected,
                color = pillColor(item),
                icon = icon?.invoke(item),
                onClick = { onSelect(item) },
                testTag = "${testTagPrefix}_${label(item).lowercase()}"
            )
        }
    }
}

@Composable
private fun FilterPillItem(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    testTag: String,
    icon: ImageVector? = null
) {
    // The selected fill is usually colorScheme.primary, which is *ink* — near-black in light
    // but near-WHITE in dark. Hardcoding white content made selected pills unreadable in dark
    // mode. Derive the content colour from the fill's luminance instead.
    val contentColor = if (isSelected) {
        if (color.luminance() > 0.5f) Color.Black else Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = ShapeCircle,
        color = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
