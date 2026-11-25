package dev.atalariq.gobooks.network

import dev.atalariq.gobooks.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Retrofit mewajibkan baseUrl berakhir dengan "/".
    private val BASE_URL = BuildConfig.BASE_URL.let {
        if (it.endsWith("/")) it else "$it/"
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp = OkHttpClient.Builder()
        .apply {
            // URL request memuat API key, jadi log hanya dipasang pada build debug.
            if (BuildConfig.DEBUG) addInterceptor(logging)
        }
        .build()

    val api: GoogleBooksApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleBooksApi::class.java)
    }
}
