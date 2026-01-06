package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Book
import com.example.myapplication.data.model.WorkApiResponse
import com.example.myapplication.data.model.toBook
import com.example.myapplication.data.remote.OpenLibraryApi

class BookRepository(private val api: OpenLibraryApi) {

    suspend fun getFictionBooks(): List<Book> {
        return api.getFictionBooks().works
    }

    suspend fun getBookDetails(workId: String): WorkApiResponse {
        return api.getBookDetails(workId)
    }

    suspend fun getBook(workId: String): Book {
        return api.getBook(workId).toBook()
    }

    suspend fun searchBooks(query: String): List<Book> {
        return api.searchBooks(query).docs.map { it.toBook() }
    }
}
