package com.booktracker.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.presentation.components.*
import com.booktracker.app.presentation.theme.IOSTheme
import com.booktracker.app.presentation.viewmodel.AddBookEvent
import com.booktracker.app.presentation.viewmodel.AddBookUiState

@Composable
fun AddBookScreen(
    uiState: AddBookUiState,
    onEvent: (AddBookEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val shapes = IOSTheme.shapes
    val spacing = IOSTheme.spacing

    val shelves = remember { ShelfType.entries.toList() }
    val shelfLabels = remember { shelves.map { it.displayName } }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surface)
    ) {
        // Sheet handle
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(36.dp)
                .height(5.dp)
                .clip(shapes.capsule)
                .background(colors.tertiaryLabel)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Header with Cancel and Add buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IOSTextButton(
                text = "Cancel",
                onClick = {
                    onEvent(AddBookEvent.OnCancel)
                    onDismiss()
                }
            )

            BasicText(
                text = "Add Book",
                style = typography.headline.copy(
                    color = colors.label,
                    fontWeight = FontWeight.SemiBold
                )
            )

            IOSTextButton(
                text = "Add",
                onClick = { onEvent(AddBookEvent.OnAddBook) },
                color = colors.primary,
                enabled = uiState.title.isNotBlank() && uiState.author.isNotBlank()
            )
        }

        // Divider
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(colors.separator)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.md)
                .padding(top = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg)
        ) {
            // Title field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BasicText(
                    text = "TITLE",
                    style = typography.caption2.copy(
                        color = colors.secondaryLabel,
                        fontWeight = FontWeight.Medium
                    )
                )
                IOSTextField(
                    value = uiState.title,
                    onValueChange = { onEvent(AddBookEvent.OnTitleChanged(it)) },
                    placeholder = "Enter book title"
                )
                if (uiState.titleError != null) {
                    BasicText(
                        text = uiState.titleError,
                        style = typography.caption1.copy(color = colors.destructive)
                    )
                }
            }

            // Author field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BasicText(
                    text = "AUTHOR",
                    style = typography.caption2.copy(
                        color = colors.secondaryLabel,
                        fontWeight = FontWeight.Medium
                    )
                )
                IOSTextField(
                    value = uiState.author,
                    onValueChange = { onEvent(AddBookEvent.OnAuthorChanged(it)) },
                    placeholder = "Enter author name"
                )
                if (uiState.authorError != null) {
                    BasicText(
                        text = uiState.authorError,
                        style = typography.caption1.copy(color = colors.destructive)
                    )
                }
            }

            // Shelf selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BasicText(
                    text = "SHELF",
                    style = typography.caption2.copy(
                        color = colors.secondaryLabel,
                        fontWeight = FontWeight.Medium
                    )
                )
                val selectedIndex = remember(uiState.shelf) {
                    shelves.indexOf(uiState.shelf)
                }
                IOSSegmentedControl(
                    items = shelfLabels,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        onEvent(AddBookEvent.OnShelfChanged(shelves[index]))
                    }
                )
            }

            // Progress (only when shelf is Reading)
            if (uiState.shelf == ShelfType.READING) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BasicText(
                            text = "PROGRESS",
                            style = typography.caption2.copy(
                                color = colors.secondaryLabel,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        BasicText(
                            text = "${uiState.progress}%",
                            style = typography.caption2.copy(
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    IOSSlider(
                        value = uiState.progress / 100f,
                        onValueChange = { newValue ->
                            onEvent(AddBookEvent.OnProgressChanged((newValue * 100).toInt()))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.xxl))
        }
    }
}
