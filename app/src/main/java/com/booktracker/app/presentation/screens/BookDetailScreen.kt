package com.booktracker.app.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.presentation.components.*
import com.booktracker.app.presentation.theme.IOSTheme
import com.booktracker.app.presentation.viewmodel.BookDetailEvent
import com.booktracker.app.presentation.viewmodel.BookDetailUiState

@Composable
fun BookDetailScreen(
    uiState: BookDetailUiState,
    onEvent: (BookDetailEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val shapes = IOSTheme.shapes
    val spacing = IOSTheme.spacing
    val dimensions = IOSTheme.dimensions

    val shelves = remember { ShelfType.entries.toList() }
    val shelfLabels = remember { shelves.map { it.displayName } }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        IOSNavigationBar(
            title = uiState.book?.title ?: "",
            showBackButton = true,
            onBackClick = { onEvent(BookDetailEvent.OnBackClicked) }
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "Loading...",
                    style = typography.body.copy(color = colors.secondaryLabel)
                )
            }
        } else {
            val book = uiState.book ?: return

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cover image
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = "${book.title} cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(dimensions.detailCoverWidth)
                        .height(dimensions.detailCoverHeight)
                        .shadow(8.dp, shapes.cover)
                        .clip(shapes.cover)
                        .background(colors.fill)
                )

                Spacer(modifier = Modifier.height(spacing.lg))

                // Title
                BasicText(
                    text = book.title,
                    style = typography.title1.copy(color = colors.label)
                )

                Spacer(modifier = Modifier.height(spacing.xs))

                // Author
                BasicText(
                    text = book.author,
                    style = typography.body.copy(color = colors.secondaryLabel)
                )

                Spacer(modifier = Modifier.height(spacing.lg))

                // Shelf segmented control
                val selectedIndex = remember(book.shelf) {
                    shelves.indexOf(book.shelf)
                }
                IOSSegmentedControl(
                    items = shelfLabels,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        onEvent(BookDetailEvent.OnMoveShelf(shelves[index]))
                    }
                )

                Spacer(modifier = Modifier.height(spacing.lg))

                // Progress section (only when Reading)
                AnimatedContent(
                    targetState = book.shelf == ShelfType.READING,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                    },
                    label = "progressSection"
                ) { showProgress ->
                    if (showProgress) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, shapes.card, ambientColor = colors.cardShadow)
                                .clip(shapes.card)
                                .background(colors.surface)
                                .padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            BasicText(
                                text = "Reading Progress",
                                style = typography.headline.copy(color = colors.label)
                            )

                            IOSProgressBar(
                                progress = book.progress / 100f,
                                height = 6.dp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicText(
                                    text = "${book.progress}%",
                                    style = typography.title2.copy(
                                        color = colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                BasicText(
                                    text = "completed",
                                    style = typography.subheadline.copy(
                                        color = colors.secondaryLabel
                                    )
                                )
                            }

                            IOSSlider(
                                value = book.progress / 100f,
                                onValueChange = { newValue ->
                                    onEvent(
                                        BookDetailEvent.OnProgressChanged(
                                            (newValue * 100).toInt()
                                        )
                                    )
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                }

                Spacer(modifier = Modifier.height(spacing.lg))

                // Action buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, shapes.card, ambientColor = colors.cardShadow)
                        .clip(shapes.card)
                        .background(colors.surface)
                        .padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    BasicText(
                        text = "Actions",
                        style = typography.headline.copy(color = colors.label)
                    )

                    if (book.shelf != ShelfType.READ) {
                        IOSButton(
                            text = "Mark as Read",
                            onClick = { onEvent(BookDetailEvent.OnMarkAsRead) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (book.shelf != ShelfType.ABANDONED) {
                        IOSButton(
                            text = "Mark as Abandoned",
                            onClick = { onEvent(BookDetailEvent.OnMarkAsAbandoned) },
                            isDestructive = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.xxl))
            }
        }
    }
}
