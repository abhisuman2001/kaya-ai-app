package com.example.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.theme.ShapeLarge

/** The app's one dialog shape/colour treatment, so every confirmation reads consistently. */
@Composable
fun KayaDialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    text: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        shape = ShapeLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}
