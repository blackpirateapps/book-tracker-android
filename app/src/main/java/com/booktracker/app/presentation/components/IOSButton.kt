package com.booktracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.booktracker.app.presentation.theme.IOSTheme

@Composable
fun IOSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    isDestructive: Boolean = false
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val shapes = IOSTheme.shapes

    val backgroundColor = when {
        !enabled -> colors.tertiaryLabel
        isDestructive -> colors.destructive
        isPrimary -> colors.primary
        else -> Color.Transparent
    }

    val textColor = when {
        !enabled && isPrimary -> Color.White.copy(alpha = 0.6f)
        isPrimary || isDestructive -> Color.White
        else -> colors.primary
    }

    Box(
        modifier = modifier
            .clip(shapes.button)
            .background(backgroundColor)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = typography.headline.copy(color = textColor),
        )
    }
}

@Composable
fun IOSTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = IOSTheme.colors.primary,
    enabled: Boolean = true
) {
    val typography = IOSTheme.typography
    val actualColor = if (enabled) color else color.copy(alpha = 0.4f)

    BasicText(
        text = text,
        style = typography.body.copy(color = actualColor),
        modifier = modifier.clickable(
            enabled = enabled,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    )
}
