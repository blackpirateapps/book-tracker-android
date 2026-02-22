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
import androidx.compose.ui.unit.dp
import com.booktracker.app.presentation.theme.IOSTheme

@Composable
fun IOSNavigationBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    trailingContent: @Composable () -> Unit = {}
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val spacing = IOSTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(horizontal = spacing.md)
    ) {
        // Top bar with back button and trailing content
        if (showBackButton) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IOSTheme.dimensions.navigationBarHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBackClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        text = "‹",
                        style = typography.largeTitle.copy(
                            color = colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicText(
                        text = "Back",
                        style = typography.body.copy(color = colors.primary)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                trailingContent()
            }
        }

        // Large title section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = title,
                style = typography.largeTitle.copy(color = colors.label),
                modifier = Modifier.weight(1f)
            )
            if (!showBackButton) {
                trailingContent()
            }
        }
    }
}
