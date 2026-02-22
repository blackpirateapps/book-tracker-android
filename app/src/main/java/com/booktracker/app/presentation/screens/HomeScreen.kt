package com.booktracker.app.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.presentation.components.*
import com.booktracker.app.presentation.theme.IOSTheme
import com.booktracker.app.presentation.viewmodel.HomeEvent
import com.booktracker.app.presentation.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onBookClick: (String) -> Unit
) {
    val colors = IOSTheme.colors
    val spacing = IOSTheme.spacing
    val shelves = remember { ShelfType.entries.toList() }
    val shelfLabels = remember { shelves.map { it.displayName } }
    val selectedIndex = remember(uiState.selectedShelf) {
        shelves.indexOf(uiState.selectedShelf)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        // Navigation bar with large title and add button
        IOSNavigationBar(
            title = "My Books",
            trailingContent = {
                Box(
                    modifier = Modifier
                        .size(IOSTheme.dimensions.addButtonSize)
                        .clip(CircleShape)
                        .background(colors.primary)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onEvent(HomeEvent.OnAddBookClicked) },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "+",
                        style = IOSTheme.typography.title3.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Light,
                            fontSize = 20.sp,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        )

        // Search bar
        IOSSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { onEvent(HomeEvent.OnSearchChanged(it)) },
            modifier = Modifier.padding(horizontal = spacing.md)
        )

        Spacer(modifier = Modifier.height(spacing.md))

        // Segmented control
        IOSSegmentedControl(
            items = shelfLabels,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                onEvent(HomeEvent.OnShelfChanged(shelves[index]))
            },
            modifier = Modifier.padding(horizontal = spacing.md)
        )

        Spacer(modifier = Modifier.height(spacing.md))

        // Book list
        Crossfade(
            targetState = uiState.selectedShelf,
            animationSpec = tween(300),
            label = "shelfCrossfade"
        ) { _ ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = "Loading...",
                        style = IOSTheme.typography.body.copy(color = colors.secondaryLabel)
                    )
                }
            } else if (uiState.filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicText(
                            text = "📚",
                            style = IOSTheme.typography.largeTitle
                        )
                        Spacer(modifier = Modifier.height(spacing.sm))
                        BasicText(
                            text = "No books yet",
                            style = IOSTheme.typography.headline.copy(color = colors.secondaryLabel)
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        BasicText(
                            text = "Tap + to add your first book",
                            style = IOSTheme.typography.subheadline.copy(color = colors.tertiaryLabel)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = spacing.md,
                        vertical = spacing.sm
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    itemsIndexed(
                        items = uiState.filteredBooks,
                        key = { _, book -> book.id }
                    ) { index, book ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(book.id) {
                            visible = true
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(400, delayMillis = index * 50)) +
                                    slideInVertically(
                                        tween(400, delayMillis = index * 50),
                                        initialOffsetY = { it / 4 }
                                    )
                        ) {
                            BookCard(
                                book = book,
                                onClick = { onBookClick(book.id) }
                            )
                        }
                    }
                    // Bottom padding for navigation bar
                    item { Spacer(modifier = Modifier.height(spacing.xl)) }
                }
            }
        }
    }
}
