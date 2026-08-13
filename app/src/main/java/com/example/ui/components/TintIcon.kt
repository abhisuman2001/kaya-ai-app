package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ShapeCircle

/**
 * The app's existing tint recipe: an icon sitting inside a circular chip filled with the
 * accent colour at 15% alpha. Used everywhere an icon needs a soft accent background
 * instead of a hard fill.
 */
@Composable
fun TintIcon(
    icon: ImageVector,
    tint: Color = MaterialTheme.colorScheme.primary,
    contentDescription: String? = null,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(color = tint.copy(alpha = 0.15f), shape = ShapeCircle),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
