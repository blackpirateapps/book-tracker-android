package com.booktracker.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.domain.usecase.GetBooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getBooksUseCase: GetBooksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnSearchChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                filterBooks()
            }
            is HomeEvent.OnShelfChanged -> {
                _uiState.update { it.copy(selectedShelf = event.shelf) }
                filterBooks()
            }
            is HomeEvent.OnBookClicked -> { /* Handled by navigation */ }
            is HomeEvent.OnAddBookClicked -> {
                _uiState.update { it.copy(showAddSheet = true) }
            }
            is HomeEvent.OnDismissAddSheet -> {
                _uiState.update { it.copy(showAddSheet = false) }
                loadBooks()
            }
            is HomeEvent.OnRefresh -> loadBooks()
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val books = getBooksUseCase()
                _uiState.update { it.copy(books = books, isLoading = false) }
                filterBooks()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun filterBooks() {
        val state = _uiState.value
        val filtered = state.books
            .filter { it.shelf == state.selectedShelf }
            .filter {
                state.searchQuery.isBlank() ||
                    it.title.contains(state.searchQuery, ignoreCase = true) ||
                    it.author.contains(state.searchQuery, ignoreCase = true)
            }
        _uiState.update { it.copy(filteredBooks = filtered) }
    }

    class Factory(private val getBooksUseCase: GetBooksUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(getBooksUseCase) as T
        }
    }
}
