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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    ) {
        if (showBackButton) {
            // ── Inline navigation bar (detail screens) ──
            // Only a compact back row + trailing. No large title.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IOSTheme.dimensions.navigationBarHeight)
                    .padding(horizontal = spacing.md)
            ) {
                // Back button – left aligned
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBackClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicText(
                        text = "‹",
                        style = typography.title1.copy(
                            color = colors.primary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    BasicText(
                        text = "Back",
                        style = typography.body.copy(color = colors.primary)
                    )
                }

                // Trailing – right aligned
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    trailingContent()
                }
            }
        } else {
            // ── Large title navigation bar (home screen) ──
            Spacer(modifier = Modifier.height(spacing.sm))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md)
                    .padding(top = spacing.sm, bottom = spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = title,
                    style = typography.largeTitle.copy(color = colors.label),
                    modifier = Modifier.weight(1f)
                )
                trailingContent()
            }
        }
    }
}
