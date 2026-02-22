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
            .background(colors.surface)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = spacing.md, vertical = spacing.sm)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cover image with small radius
                Box(
                    modifier = Modifier
                        .clip(shapes.cover)
                ) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = "${book.title} cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(dimensions.coverWidth * 0.8f)
                            .height(dimensions.coverHeight * 0.8f)
                            .background(colors.fill)
                    )
                }

                // Book info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    BasicText(
                        text = book.title,
                        style = typography.body.copy(color = colors.label, fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    BasicText(
                        text = book.author,
                        style = typography.subheadline.copy(
                            color = colors.secondaryLabel
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ShelfTag(shelf = book.shelf)

                        // Progress text (only for Reading)
                        if (book.shelf == ShelfType.READING) {
                            BasicText(
                                text = "${book.progress}%",
                                style = typography.caption1.copy(
                                    color = colors.secondaryLabel,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.sm))
            // Bottom separator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(colors.separator)
            )
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
