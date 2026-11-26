package com.example.feedsocialmedia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedsocialmedia.SuspendState
import com.example.feedsocialmedia.jsonplaceholder.JSONPlaceholder
import com.example.feedsocialmedia.ui.composables.PostListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import java.net.URL
import java.util.concurrent.TimeUnit

class PostListPageViewModel: ViewModel() {
    var state by mutableStateOf<SuspendState<List<PostListItem>>>(SuspendState.Loading)
        private set

    fun downloadPosts() {
        viewModelScope.launch {
            state = SuspendState.Loading
            state = try {
                SuspendState.Success(
                    RetrofitClent.api.listPost().map { it.toPostListItem() }
                )
            } catch (_: Exception) {
                SuspendState.Error("Algo deu errado!")
            }
        }
    }

    private suspend fun download(): List<PostListItem> = withContext(Dispatchers.IO) {
        val jsonText = URL("https://jsonplaceholder.typicode.com/posts").readText()
        Json.decodeFromString<List<JSONPlaceholder.PostListItem>>(jsonText).map { it.toPostListItem() }
    }

}


interface JSONPlaceHolderApi {
    @GET("posts")
    suspend fun listPost(): List<JSONPlaceholder.PostListItem>
}

object RetrofitClent {
    private val json = Json { ignoreUnknownKeys = true}
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()

    val api : JSONPlaceHolderApi = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://jsonplaceholder.typicode.com")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(JSONPlaceHolderApi::class.java)
}