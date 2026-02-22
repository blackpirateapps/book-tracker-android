package com.booktracker.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.booktracker.app.domain.model.ShelfType
import com.booktracker.app.domain.usecase.AddBookByOlidUseCase
import com.booktracker.app.domain.usecase.GetBooksUseCase
import com.booktracker.app.domain.usecase.SearchBooksUseCase
import com.booktracker.app.presentation.refresh.AppRefreshBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

import androidx.lifecycle.SavedStateHandle

class HomeViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getBooksUseCase: GetBooksUseCase,
    private val searchBooksUseCase: SearchBooksUseCase,
    private val addBookByOlidUseCase: AddBookByOlidUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var searchJob: Job? = null

    init {
        loadBooks()
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>("shelf", null).collect { shelfStr ->
                if (shelfStr != null) {
                    try {
                        val newShelf = ShelfType.valueOf(shelfStr)
                        _uiState.update { it.copy(selectedShelf = newShelf) }
                        filterBooks()
                    } catch (e: Exception) {
                        // Ignored
                    }
                }
            }
        }
        viewModelScope.launch {
            AppRefreshBus.events.collect {
                loadBooks()
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnSearchChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                if (event.query.isBlank()) {
                    searchJob?.cancel()
                    _uiState.update {
                        it.copy(
                            searchResults = emptyList(),
                            isSearching = false,
                            searchError = null
                        )
                    }
                    filterBooks()
                } else {
                    performSearch(event.query)
                }
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
            is HomeEvent.OnAddFromSearch -> addFromSearch(event.olid)
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
        val filtered = state.books.filter {
            state.searchQuery.isBlank() ||
                it.title.contains(state.searchQuery, ignoreCase = true) ||
                it.author.contains(state.searchQuery, ignoreCase = true)
        }
        _uiState.update { it.copy(filteredBooks = filtered) }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchError = null) }
            delay(300)
            val result = searchBooksUseCase(query)
            if (result.isSuccess) {
                _uiState.update { it.copy(searchResults = result.getOrNull().orEmpty(), isSearching = false) }
            } else {
                _uiState.update { it.copy(searchResults = emptyList(), isSearching = false, searchError = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun addFromSearch(olid: String) {
        viewModelScope.launch {
            addBookByOlidUseCase(olid, ShelfType.READING_LIST.apiValue)
            loadBooks()
        }
    }

    class Factory(
        private val savedStateHandle: SavedStateHandle,
        private val getBooksUseCase: GetBooksUseCase,
        private val searchBooksUseCase: SearchBooksUseCase,
        private val addBookByOlidUseCase: AddBookByOlidUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(savedStateHandle, getBooksUseCase, searchBooksUseCase, addBookByOlidUseCase) as T
        }
    }
}
