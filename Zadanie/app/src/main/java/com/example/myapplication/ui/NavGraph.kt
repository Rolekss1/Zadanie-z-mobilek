package com.example.myapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.local.FavoritesManager
import com.example.myapplication.data.remote.RetrofitInstance
import com.example.myapplication.data.repository.BookRepository
import com.example.myapplication.ui.screens.detail.BookDetailScreen
import com.example.myapplication.ui.screens.detail.BookDetailViewModel
import com.example.myapplication.ui.screens.favorites.FavoritesScreen
import com.example.myapplication.ui.screens.favorites.FavoritesViewModel
import com.example.myapplication.ui.screens.home.HomeScreen
import com.example.myapplication.ui.screens.home.HomeViewModel

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val bookRepository = BookRepository(RetrofitInstance.api)
    val favoritesManager = FavoritesManager(context)

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return HomeViewModel(bookRepository) as T
                    }
                }
            )
            HomeScreen(
                onBookClick = { workId -> navController.navigate("bookDetail/$workId") },
                onFavoritesClick = { navController.navigate("favorites") },
                viewModel = homeViewModel
            )
        }
        composable("bookDetail/{workId}", arguments = listOf(navArgument("workId") { type = NavType.StringType })) {
            val workId = it.arguments?.getString("workId")!!
            val bookDetailViewModel: BookDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return BookDetailViewModel(bookRepository, favoritesManager, workId) as T
                    }
                }
            )
            BookDetailScreen(viewModel = bookDetailViewModel, onNavigateBack = { navController.popBackStack() })
        }
        composable("favorites") {
            val favoritesViewModel: FavoritesViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return FavoritesViewModel(bookRepository, favoritesManager) as T
                    }
                }
            )
            FavoritesScreen(onBookClick = { workId -> navController.navigate("bookDetail/$workId") }, onNavigateBack = { navController.popBackStack() }, viewModel = favoritesViewModel)
        }
    }
}
