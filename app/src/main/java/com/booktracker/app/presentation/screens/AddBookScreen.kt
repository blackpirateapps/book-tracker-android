package com.booktracker.app.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.presentation.viewmodel.AddBookEvent
import com.booktracker.app.presentation.viewmodel.AddBookUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    uiState: AddBookUiState,
    onEvent: (AddBookEvent) -> Unit,
    onDismiss: () -> Unit
) {
    val shelves = remember { ShelfType.entries.toList() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onDismiss()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Book") },
                navigationIcon = {
                    IconButton(onClick = { 
                        onEvent(AddBookEvent.OnCancel)
                        onDismiss() 
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { onEvent(AddBookEvent.OnAddBook) },
                        enabled = uiState.title.isNotBlank() && uiState.author.isNotBlank()
                    ) {
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { onEvent(AddBookEvent.OnTitleChanged(it)) },
                label = { Text("Title") },
                placeholder = { Text("Enter book title") },
                isError = uiState.titleError != null,
                supportingText = {
                    if (uiState.titleError != null) {
                        Text(text = uiState.titleError)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            // Author field
            OutlinedTextField(
                value = uiState.author,
                onValueChange = { onEvent(AddBookEvent.OnAuthorChanged(it)) },
                label = { Text("Author") },
                placeholder = { Text("Enter author name") },
                isError = uiState.authorError != null,
                supportingText = {
                    if (uiState.authorError != null) {
                        Text(text = uiState.authorError)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            // Shelf selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Shelf",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.shelf.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        shape = MaterialTheme.shapes.medium
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        shelves.forEach { shelf ->
                            DropdownMenuItem(
                                text = { Text(shelf.displayName) },
                                onClick = {
                                    onEvent(AddBookEvent.OnShelfChanged(shelf))
                                    expanded = false
                                }
                            )
                        }
                    }
                }

            // Progress (only when shelf is Reading)
            if (uiState.shelf == ShelfType.READING) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${uiState.progress}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = uiState.progress / 100f,
                        onValueChange = { newValue ->
                            onEvent(AddBookEvent.OnProgressChanged((newValue * 100).toInt()))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
