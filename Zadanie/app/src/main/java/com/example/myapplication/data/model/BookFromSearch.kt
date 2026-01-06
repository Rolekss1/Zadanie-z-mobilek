package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val docs: List<BookFromSearch>
)

data class BookFromSearch(
    val key: String,
    val title: String?,
    @SerializedName("author_name")
    val authorName: List<String>?,
    @SerializedName("cover_i")
    val coverId: Int?
)

fun BookFromSearch.toBook(): Book {
    return Book(
        key = this.key,
        title = this.title,
        authors = this.authorName?.map { Author(name = it) },
        cover_id = this.coverId
    )
}
