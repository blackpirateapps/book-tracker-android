package com.booktracker.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.presentation.viewmodel.BookDetailEvent
import com.booktracker.app.presentation.viewmodel.BookDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    uiState: BookDetailUiState,
    onEvent: (BookDetailEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val shelves = remember { ShelfType.entries.toList() }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(BookDetailEvent.OnBackClicked) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = { onEvent(BookDetailEvent.OnCancelEditClicked) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                        }
                        IconButton(onClick = { onEvent(BookDetailEvent.OnSaveClicked) }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                        }
                    } else {
                        IconButton(onClick = { onEvent(BookDetailEvent.OnEditClicked) }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val book = uiState.book ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(84.dp)
                            .height(124.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (uiState.isEditing) {
                            OutlinedTextField(
                                value = uiState.editTitle,
                                onValueChange = { onEvent(BookDetailEvent.OnEditTitleChanged(it)) },
                                label = { Text("Title") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = uiState.editAuthor,
                                onValueChange = { onEvent(BookDetailEvent.OnEditAuthorChanged(it)) },
                                label = { Text("Author") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = book.author,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            ShelfChip(label = book.shelf.displayName)
                        }
                    }
                }
            }

            SectionHeader("DETAILS")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetadataCard(
                    icon = Icons.Default.Description,
                    label = "Pages",
                    value = book.pageCount?.toString() ?: "---",
                    modifier = Modifier.weight(1f)
                )
                MetadataCard(
                    icon = Icons.Default.LibraryBooks,
                    label = "Medium",
                    value = book.readingMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetadataCard(
                    icon = Icons.Default.CalendarToday,
                    label = "Started",
                    value = book.startedOn ?: "---",
                    modifier = Modifier.weight(1f)
                )
                MetadataCard(
                    icon = Icons.Default.EventAvailable,
                    label = "Finished",
                    value = book.finishedOn ?: "---",
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.isEditing) {
                SectionHeader("EDIT FIELDS")
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.editReadingMedium,
                            onValueChange = { onEvent(BookDetailEvent.OnEditReadingMediumChanged(it)) },
                            label = { Text("Reading Medium") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.editPageCount,
                            onValueChange = { onEvent(BookDetailEvent.OnEditPageCountChanged(it)) },
                            label = { Text("Page Count") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.editStartedOn,
                            onValueChange = { onEvent(BookDetailEvent.OnEditStartedOnChanged(it)) },
                            label = { Text("Started On (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.editFinishedOn,
                            onValueChange = { onEvent(BookDetailEvent.OnEditFinishedOnChanged(it)) },
                            label = { Text("Finished On (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.editDescription,
                            onValueChange = { onEvent(BookDetailEvent.OnEditDescriptionChanged(it)) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            }

            SectionHeader("SHELF")
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isEditing) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = book.shelf.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Shelf") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            shelves.forEach { shelf ->
                                DropdownMenuItem(
                                    text = { Text(shelf.displayName) },
                                    onClick = {
                                        onEvent(BookDetailEvent.OnMoveShelf(shelf))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    TabRow(
                        selectedTabIndex = shelves.indexOf(book.shelf),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        divider = {},
                        indicator = { tabPositions ->
                            val index = shelves.indexOf(book.shelf)
                            if (index != -1 && index < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    ) {
                        shelves.forEach { shelf ->
                            Tab(
                                selected = book.shelf == shelf,
                                onClick = { onEvent(BookDetailEvent.OnMoveShelf(shelf)) },
                                text = {
                                    Text(
                                        text = shelf.displayName,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }
            }

            if (book.shelf == ShelfType.READING) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${book.progress}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { book.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(MaterialTheme.shapes.small),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )

                        Slider(
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
                }
            }

            if (!book.description.isNullOrBlank() && !uiState.isEditing) {
                SectionHeader("ABOUT")
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (book.shelf != ShelfType.READ) {
                    Button(
                        onClick = { onEvent(BookDetailEvent.OnMarkAsRead) },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark as Read")
                    }
                }

                if (book.shelf != ShelfType.ABANDONED) {
                    OutlinedButton(
                        onClick = { onEvent(BookDetailEvent.OnMarkAsAbandoned) },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark as Abandoned")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ShelfChip(label: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MetadataCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
