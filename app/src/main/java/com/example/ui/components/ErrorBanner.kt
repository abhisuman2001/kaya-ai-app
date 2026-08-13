package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeMedium

/** A tinted inline error message, replacing the identical hand-rolled box previously
 * duplicated across the auth screens (login/register/forgot-password/OTP). */
@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    val errorColor = LocalKayaColors.current.status.error
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = errorColor.copy(alpha = 0.15f), shape = ShapeMedium)
            .border(width = 1.dp, color = errorColor.copy(alpha = 0.4f), shape = ShapeMedium)
            .padding(14.dp)
    ) {
        Text(
            text = message,
            color = errorColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}
