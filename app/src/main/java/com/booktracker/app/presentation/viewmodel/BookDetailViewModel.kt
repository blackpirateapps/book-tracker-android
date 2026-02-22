package com.booktracker.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.domain.usecase.GetBookByIdUseCase
import com.booktracker.app.domain.usecase.UpdateBookUseCase
import com.booktracker.app.presentation.refresh.AppRefreshBus
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
        viewModelScope.launch {
            AppRefreshBus.events.collect {
                loadBook()
            }
        }
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
            is BookDetailEvent.OnEditClicked -> {
                val book = _uiState.value.book
                if (book != null) {
                    _uiState.update { it.copy(isEditing = true, editTitle = book.title, editAuthor = book.author) }
                }
            }
            is BookDetailEvent.OnCancelEditClicked -> {
                _uiState.update { it.copy(isEditing = false) }
            }
            is BookDetailEvent.OnSaveClicked -> {
                val state = _uiState.value
                val book = state.book
                if (book != null) {
                    val updatedBook = book.copy(title = state.editTitle, author = state.editAuthor)
                    viewModelScope.launch {
                        updateBookUseCase(updatedBook)
                        _uiState.update { it.copy(book = updatedBook, isEditing = false) }
                    }
                }
            }
            is BookDetailEvent.OnEditTitleChanged -> {
                _uiState.update { it.copy(editTitle = event.title) }
            }
            is BookDetailEvent.OnEditAuthorChanged -> {
                _uiState.update { it.copy(editAuthor = event.author) }
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
        val state = _uiState.value
        val book = state.book ?: return
        
        // If we're editing, we just update the book reference object (but wait, we need to bind it separately if we want to not save immediately? 
        // Oh the slider is bound to the actual progress. So the actual progress will be changed live.
        val updated = book.copy(progress = progress.coerceIn(0, 100))
        viewModelScope.launch {
            if (!state.isEditing) {
                updateBookUseCase(updated)
            }
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
            if (!_uiState.value.isEditing) {
                updateBookUseCase(updated)
            }
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
