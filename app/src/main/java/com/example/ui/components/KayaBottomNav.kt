package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ShapeCircle

data class KayaNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * v1's bottom navigation: a **floating rounded pill** that hovers over the content,
 * not a full-width Material bar docked to the edge. Hairline border, surface fill,
 * soft shadow, clamped to 408dp so it stays a pill on tablets instead of stretching.
 *
 * Active items go full-opacity ink + semibold with the icon scaled to 1.08; inactive
 * sit at 70% opacity in the muted colour. That restraint — no pill indicator, no colour
 * flood — is what makes v1's bar read as calm.
 */
@Composable
fun KayaBottomNav(
    items: List<KayaNavItem>,
    currentRoute: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 408.dp)
                .background(MaterialTheme.colorScheme.surface, ShapeCircle)
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    ShapeCircle
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                KayaNavButton(
                    item = item,
                    selected = currentRoute == item.route,
                    onClick = { onSelect(item.route) }
                )
            }
        }
    }
}

@Composable
private fun KayaNavButton(
    item: KayaNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = tween(200),
        label = "nav_icon_scale"
    )
    val contentColor =
        if (selected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null
            )
            // 48dp keeps the touch target accessible even though the visual is smaller.
            .heightIn(min = 48.dp)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("nav_${item.route}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier
                .size(20.dp)
                .scale(iconScale)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}
