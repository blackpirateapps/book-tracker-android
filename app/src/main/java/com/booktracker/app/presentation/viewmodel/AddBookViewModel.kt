package com.booktracker.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.booktracker.app.domain.model.Book
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.domain.usecase.AddBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddBookViewModel(
    private val addBookUseCase: AddBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState: StateFlow<AddBookUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddBookEvent) {
        when (event) {
            is AddBookEvent.OnTitleChanged -> {
                _uiState.update { it.copy(title = event.title, titleError = null) }
            }
            is AddBookEvent.OnAuthorChanged -> {
                _uiState.update { it.copy(author = event.author, authorError = null) }
            }
            is AddBookEvent.OnShelfChanged -> {
                _uiState.update {
                    it.copy(
                        shelf = event.shelf,
                        progress = if (event.shelf != ShelfType.READING) 0 else it.progress
                    )
                }
            }
            is AddBookEvent.OnProgressChanged -> {
                _uiState.update { it.copy(progress = event.progress.coerceIn(0, 100)) }
            }
            is AddBookEvent.OnAddBook -> addBook()
            is AddBookEvent.OnCancel -> {
                _uiState.update { AddBookUiState() }
            }
        }
    }

    private fun addBook() {
        val state = _uiState.value
        var hasError = false

        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title cannot be empty") }
            hasError = true
        }
        if (state.author.isBlank()) {
            _uiState.update { it.copy(authorError = "Author cannot be empty") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            val book = Book(
                title = state.title.trim(),
                author = state.author.trim(),
                shelf = state.shelf,
                progress = if (state.shelf == ShelfType.READING) state.progress else 0,
                coverUrl = "https://covers.openlibrary.org/b/id/12547191-L.jpg"
            )
            addBookUseCase(book)
            _uiState.update { it.copy(isSuccess = true) }
        }
    }

    class Factory(private val addBookUseCase: AddBookUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddBookViewModel(addBookUseCase) as T
        }
    }
}
