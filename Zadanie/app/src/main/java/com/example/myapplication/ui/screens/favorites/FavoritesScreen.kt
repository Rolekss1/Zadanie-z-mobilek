package com.example.myapplication.ui.screens.favorites

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.FavoritesManager
import com.example.myapplication.data.model.Book
import com.example.myapplication.data.repository.BookRepository
import com.example.myapplication.ui.screens.home.BookItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FavoritesScreenUiState(
    val isLoading: Boolean = false,
    val favoriteBooks: List<Book> = emptyList(),
    val error: String? = null
)

class FavoritesViewModel(private val bookRepository: BookRepository, private val favoritesManager: FavoritesManager) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesScreenUiState())
    val uiState: StateFlow<FavoritesScreenUiState> = _uiState

    fun getFavoriteBooks() {
        viewModelScope.launch {
            _uiState.value = FavoritesScreenUiState(isLoading = true)
            try {
                val favoriteIds = favoritesManager.getFavorites()
                val favoriteBooks = favoriteIds.map { bookRepository.getBook(it) }
                _uiState.value = FavoritesScreenUiState(favoriteBooks = favoriteBooks)
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error fetching favorite books", e)
                _uiState.value = FavoritesScreenUiState(error = "Failed to fetch favorite books")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onBookClick: (String) -> Unit, onNavigateBack: () -> Unit, viewModel: FavoritesViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFavoriteBooks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Books") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.favoriteBooks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorite books.")
                }
            } else {
                LazyColumn {
                    items(uiState.favoriteBooks) { book ->
                        BookItem(book = book, onBookClick = onBookClick)
                    }
                }
            }
        }
    }
}
