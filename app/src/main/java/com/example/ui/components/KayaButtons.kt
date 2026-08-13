package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalKayaColors
import com.example.ui.theme.ShapeCircle

/**
 * Button set ported from v1.
 *
 * v1's primary button is a **fully-round ink pill** — `bg-primary` is near-black in
 * light mode and near-white in dark, never blue. Secondary is a hairline-outlined pill
 * with no fill. Height is 48–52dp with generous horizontal padding.
 *
 * Loading replaces the label in place, so the button never changes size or jumps.
 */

@Composable
fun KayaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled && !isLoading,
        shape = ShapeCircle,
        contentPadding = ButtonDefaults.ContentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
    ) {
        ButtonContent(
            text = text,
            isLoading = isLoading,
            spinnerColor = MaterialTheme.colorScheme.onPrimary,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon
        )
    }
}

/** Hairline-outlined pill, no fill — v1's "Connect glasses" / "Retry" treatment. */
@Composable
fun KayaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled && !isLoading,
        shape = ShapeCircle,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        ButtonContent(
            text = text,
            isLoading = isLoading,
            spinnerColor = MaterialTheme.colorScheme.onSurface,
            leadingIcon = leadingIcon
        )
    }
}

/** Solid red pill for irreversible actions. */
@Composable
fun KayaDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val error = LocalKayaColors.current.status.error
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled && !isLoading,
        shape = ShapeCircle,
        colors = ButtonDefaults.buttonColors(
            containerColor = error,
            contentColor = Color.White
        )
    ) {
        ButtonContent(text = text, isLoading = isLoading, spinnerColor = Color.White)
    }
}

/** Text-only, tinted with the blue accent — v1's inline links. */
@Composable
fun KayaGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val accent = LocalKayaColors.current.accent
    TextButton(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        enabled = enabled && !isLoading,
        shape = ShapeCircle,
        colors = ButtonDefaults.textButtonColors(contentColor = accent)
    ) {
        ButtonContent(text = text, isLoading = isLoading, spinnerColor = accent)
    }
}

@Composable
private fun ButtonContent(
    text: String,
    isLoading: Boolean,
    spinnerColor: Color,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Box(contentAlignment = Alignment.Center) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = spinnerColor
                )
            } else {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = text, style = MaterialTheme.typography.titleMedium)
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    trailingIcon()
                }
            }
        }
    }
}
