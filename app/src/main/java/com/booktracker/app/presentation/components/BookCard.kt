package com.booktracker.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.ShelfType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cover image
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .width(64.dp)
                    .height(96.dp)
            ) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = "${book.title} cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            // Book info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShelfTag(shelf = book.shelf)

                    // Progress text (only for Reading)
                    if (book.shelf == ShelfType.READING) {
                        Text(
                            text = "${book.progress}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
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
    val tagColor = when (shelf) {
        ShelfType.READING -> MaterialTheme.colorScheme.primary
        ShelfType.READ -> MaterialTheme.colorScheme.secondary
        ShelfType.ABANDONED -> MaterialTheme.colorScheme.error
        ShelfType.READING_LIST -> MaterialTheme.colorScheme.tertiary
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = tagColor.copy(alpha = 0.12f),
        contentColor = tagColor
    ) {
        Text(
            text = shelf.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
