package com.example.myapplication.ui.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.myapplication.data.model.Book
import com.example.myapplication.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeScreenUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String? = null
)

class HomeViewModel(private val bookRepository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState

    init {
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            _uiState.value = HomeScreenUiState(isLoading = true)
            try {
                val books = bookRepository.getFictionBooks()
                val filteredBooks = books.filter { !it.key.isNullOrEmpty() && !it.title.isNullOrEmpty() }
                _uiState.value = HomeScreenUiState(books = filteredBooks)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching books", e)
                _uiState.value = HomeScreenUiState(error = "Failed to fetch books")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onBookClick: (String) -> Unit, onFavoritesClick: () -> Unit, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Explorer") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.error!!)
                        Button(onClick = { viewModel.getBooks() }) {
                            Text("Try Again")
                        }
                    }
                }
                else -> {
                    LazyColumn {
                        items(uiState.books) { book ->
                            BookItem(book = book, onBookClick = onBookClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book, onBookClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick(book.key.removePrefix("/works/")) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = "https://covers.openlibrary.org/b/id/${book.cover_id}-M.jpg",
            contentDescription = "Book Cover",
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = book.title ?: "No title")
            Text(text = book.authors?.joinToString { it.name } ?: "Unknown Author")
        }
    }
}
