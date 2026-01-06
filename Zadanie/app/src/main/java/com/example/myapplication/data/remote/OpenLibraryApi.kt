package com.example.myapplication.data.remote

import com.example.myapplication.data.model.BookFromSearch
import com.example.myapplication.data.model.SearchResponse
import com.example.myapplication.data.model.SubjectResponse
import com.example.myapplication.data.model.WorkApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("subjects/fiction.json")
    suspend fun getFictionBooks(@Query("limit") limit: Int = 20): SubjectResponse

    @GET("works/{work_id}.json")
    suspend fun getBookDetails(@Path("work_id") workId: String): WorkApiResponse

    @GET("works/{work_id}.json")
    suspend fun getBook(@Path("work_id") workId: String): BookFromSearch

    @GET("search.json")
    suspend fun searchBooks(@Query("q") query: String, @Query("limit") limit: Int = 20): SearchResponse
}
