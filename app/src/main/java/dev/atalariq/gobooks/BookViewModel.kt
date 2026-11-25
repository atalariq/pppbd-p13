package dev.atalariq.gobooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.atalariq.gobooks.data.BookItem
import dev.atalariq.gobooks.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class BooksViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<BookItem>>(emptyList())
    val results: StateFlow<List<BookItem>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    companion object {
        private const val MAX_RESULTS = 20
    }

    fun search(query: String) {
        val q = query.trim()
        if (q.isEmpty()) {
            _error.value = "Kata kunci tidak boleh kosong"
            return
        }
        if (BuildConfig.API_KEY.isBlank()) {
            _error.value = "API key belum diisi di local.properties"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = ApiClient.api.searchVolumes(q, MAX_RESULTS, BuildConfig.API_KEY)
                val books = response.items.orEmpty().distinctBy { it.id }
                _results.value = books
                if (books.isEmpty()) _error.value = "Buku tidak ditemukan"
            } catch (e: IOException) {
                _error.value = "Koneksi error, periksa internet Anda"
                _results.value = emptyList()
            } catch (e: HttpException) {
                _error.value = when (e.code()) {
                    400, 403 -> "API key ditolak Google (HTTP ${e.code()}), periksa key dan pembatasannya"
                    429 -> "Kuota Google Books API habis, coba lagi nanti"
                    else -> "Permintaan gagal (HTTP ${e.code()})"
                }
                _results.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
