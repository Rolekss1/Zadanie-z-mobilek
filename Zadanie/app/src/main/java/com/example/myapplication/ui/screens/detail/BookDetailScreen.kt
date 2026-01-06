package com.example.myapplication.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.myapplication.data.local.FavoritesManager
import com.example.myapplication.data.model.WorkApiResponse
import com.example.myapplication.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BookDetailScreenUiState(
    val isLoading: Boolean = false,
    val bookDetails: WorkApiResponse? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)

class BookDetailViewModel(private val bookRepository: BookRepository, private val favoritesManager: FavoritesManager, private val workId: String) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailScreenUiState())
    val uiState: StateFlow<BookDetailScreenUiState> = _uiState

    init {
        getBookDetails()
        checkIfFavorite()
    }

    private fun getBookDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val details = bookRepository.getBookDetails(workId)
                _uiState.value = _uiState.value.copy(isLoading = false, bookDetails = details)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to fetch book details")
            }
        }
    }

    private fun checkIfFavorite() {
        _uiState.value = _uiState.value.copy(isFavorite = favoritesManager.isFavorite(workId))
    }

    fun toggleFavorite() {
        if (_uiState.value.isFavorite) {
            favoritesManager.removeFavorite(workId)
        } else {
            favoritesManager.addFavorite(workId)
        }
        checkIfFavorite()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(viewModel: BookDetailViewModel, onNavigateBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                }
                uiState.error != null -> {
                    Text(text = uiState.error!!, modifier = Modifier.fillMaxSize())
                }
                uiState.bookDetails != null -> {
                    val details = uiState.bookDetails!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = "https://covers.openlibrary.org/b/id/${details.firstPublishDate}-L.jpg",
                            contentDescription = "Book Cover",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Published: ${details.firstPublishDate ?: "N/A"}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.toggleFavorite() }) {
                            Text(if (uiState.isFavorite) "Remove from Favorites" else "Add to Favorites")
                        }
                    }
                }
            }
        }
    }
}
