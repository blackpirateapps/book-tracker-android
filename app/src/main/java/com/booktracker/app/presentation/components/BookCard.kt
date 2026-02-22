package com.booktracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.presentation.theme.IOSTheme

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val shapes = IOSTheme.shapes
    val spacing = IOSTheme.spacing
    val dimensions = IOSTheme.dimensions

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = shapes.card,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(shapes.card)
            .background(colors.surface)
            .border(
                width = 0.5.dp,
                color = colors.separator.copy(alpha = 0.3f),
                shape = shapes.card
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cover image with subtle shadow
            Box(
                modifier = Modifier
                    .shadow(4.dp, shapes.cover)
                    .clip(shapes.cover)
            ) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = "${book.title} cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(dimensions.coverWidth)
                        .height(dimensions.coverHeight)
                        .background(colors.fill)
                )
            }

            // Book info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.coverHeight),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    BasicText(
                        text = book.title,
                        style = typography.headline.copy(color = colors.label),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    BasicText(
                        text = book.author,
                        style = typography.subheadline.copy(
                            color = colors.secondaryLabel
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Shelf tag
                    ShelfTag(shelf = book.shelf)

                    // Progress bar (only for Reading)
                    if (book.shelf == ShelfType.READING) {
                        IOSProgressBar(
                            progress = book.progress / 100f,
                            modifier = Modifier.fillMaxWidth(),
                            height = 5.dp
                        )
                        BasicText(
                            text = "${book.progress}% completed",
                            style = typography.caption1.copy(
                                color = colors.secondaryLabel,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShelfTag(
    shelf: ShelfType,
    modifier: Modifier = Modifier
) {
    val colors = IOSTheme.colors
    val typography = IOSTheme.typography
    val shapes = IOSTheme.shapes

    val tagColor = when (shelf) {
        ShelfType.READING -> colors.shelfReading
        ShelfType.READ -> colors.shelfRead
        ShelfType.ABANDONED -> colors.shelfAbandoned
        ShelfType.READING_LIST -> colors.shelfReadingList
    }

    Box(
        modifier = modifier
            .clip(shapes.tag)
            .background(tagColor.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        BasicText(
            text = shelf.displayName,
            style = typography.caption2.copy(
                color = tagColor,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
