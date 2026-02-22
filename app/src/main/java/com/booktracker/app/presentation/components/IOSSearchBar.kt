package com.booktracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.booktracker.app.presentation.theme.IOSTheme

@Composable
fun IOSSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search"
) {
    val colors = IOSTheme.colors
    val shapes = IOSTheme.shapes

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(shapes.searchBar)
            .background(colors.searchBarBackground),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Magnifying glass icon
            BasicText(
                text = "🔍",
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(end = 6.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    BasicText(
                        text = placeholder,
                        style = TextStyle(
                            color = colors.secondaryLabel,
                            fontSize = 16.sp
                        )
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(
                        color = colors.label,
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(colors.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotEmpty()) {
                IOSTextButton(
                    text = "✕",
                    onClick = { onQueryChange("") },
                    color = colors.secondaryLabel
                )
            }
        }
    }
}
