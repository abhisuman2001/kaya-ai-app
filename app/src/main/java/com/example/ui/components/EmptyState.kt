package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Domain-literate empty state: most screens currently show nothing when a list is empty
 * because failures are swallowed silently upstream. Use a state that reads as good news
 * where the domain supports it (e.g. a green check for "no hazards"), not a generic icon.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingValues(vertical = 48.dp, horizontal = 24.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TintIcon(icon = icon, tint = iconTint, size = 56.dp, iconSize = 28.dp)
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
