package dev.atalariq.gobooks.network

import dev.atalariq.gobooks.data.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("volumes")
    suspend fun searchVolumes(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String
    ): BooksResponse
}
