package com.booktracker.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.domain.usecase.GetBookByIdUseCase
import com.booktracker.app.domain.usecase.UpdateBookUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookId: String,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState(isLoading = true))
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        loadBook()
    }

    fun onEvent(event: BookDetailEvent) {
        when (event) {
            is BookDetailEvent.OnProgressChanged -> updateProgress(event.progress)
            is BookDetailEvent.OnMoveShelf -> moveToShelf(event.shelf)
            is BookDetailEvent.OnMarkAsRead -> moveToShelf(ShelfType.READ)
            is BookDetailEvent.OnMarkAsAbandoned -> moveToShelf(ShelfType.ABANDONED)
            is BookDetailEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
        }
    }

    private fun loadBook() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val book = getBookByIdUseCase(bookId)
                _uiState.update { it.copy(book = book, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateProgress(progress: Int) {
        val book = _uiState.value.book ?: return
        val updated = book.copy(progress = progress.coerceIn(0, 100))
        viewModelScope.launch {
            updateBookUseCase(updated)
            _uiState.update { it.copy(book = updated) }
        }
    }

    private fun moveToShelf(shelf: ShelfType) {
        val book = _uiState.value.book ?: return
        val progress = when (shelf) {
            ShelfType.READ -> 100
            ShelfType.READING_LIST -> 0
            else -> book.progress
        }
        val updated = book.copy(shelf = shelf, progress = progress)
        viewModelScope.launch {
            updateBookUseCase(updated)
            _uiState.update { it.copy(book = updated) }
        }
    }

    class Factory(
        private val bookId: String,
        private val getBookByIdUseCase: GetBookByIdUseCase,
        private val updateBookUseCase: UpdateBookUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BookDetailViewModel(bookId, getBookByIdUseCase, updateBookUseCase) as T
        }
    }
}
