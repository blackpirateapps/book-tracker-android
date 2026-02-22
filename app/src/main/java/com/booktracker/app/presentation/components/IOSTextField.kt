package com.booktracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.booktracker.app.presentation.theme.IOSTheme

@Composable
fun IOSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val shapes = IOSTheme.shapes

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.searchBar)
            .background(colors.fill)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            BasicText(
                text = placeholder,
                style = typography.body.copy(color = colors.secondaryLabel)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = typography.body.copy(color = colors.label),
            singleLine = singleLine,
            cursorBrush = SolidColor(colors.primary),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
