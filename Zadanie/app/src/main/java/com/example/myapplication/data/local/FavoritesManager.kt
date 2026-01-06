package com.example.myapplication.data.local

import android.content.Context

class FavoritesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun addFavorite(id: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(id)
        sharedPreferences.edit().putStringSet("favorite_keys", favorites).apply()
    }

    fun removeFavorite(id: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(id)
        sharedPreferences.edit().putStringSet("favorite_keys", favorites).apply()
    }

    fun isFavorite(id: String): Boolean {
        return getFavorites().contains(id)
    }

    fun getFavorites(): Set<String> {
        return sharedPreferences.getStringSet("favorite_keys", emptySet()) ?: emptySet()
    }
}
