package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared screen header: an optional small-caps eyebrow over a 20sp title, matching the
 * pattern already used across the app. Screens opt into this instead of hand-rolling their
 * own header so spacing and type stay consistent; it does not add its own Scaffold or
 * inset handling — that stays single-sourced in AppNavigation per the app's one-Scaffold rule.
 */
@Composable
fun ScreenScaffold(
    title: String,
    eyebrow: String? = null,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                if (eyebrow != null) {
                    Text(
                        text = eyebrow.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
        content()
    }
}
