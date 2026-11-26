package com.example.feedsocialmedia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedsocialmedia.SuspendState
import com.example.feedsocialmedia.jsonplaceholder.JSONPlaceholder
import com.example.feedsocialmedia.ui.composables.PostComment
import com.example.feedsocialmedia.ui.composables.PostDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

class PostDetailPageViewModel : ViewModel() {

    var state by  mutableStateOf<SuspendState<PostDetail>>(SuspendState.Loading)
        private set


    fun fetchPost(postId: String) {
        viewModelScope.launch {
            state = SuspendState.Loading
            state = try {
                SuspendState.Success(fetchPostDetail(postId))
            } catch (_: Exception) {
                SuspendState.Error("Algo deu errado!")
            }
        }
    }

    private suspend fun fetchPostDetail(postId: String): PostDetail = withContext(Dispatchers.IO) {
        val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId").readText()
        Json.decodeFromString<JSONPlaceholder.PostDetail>(jsonText).toPostDetail(fetchPostComments(postId))

    }

    private suspend fun fetchPostComments(postId: String): List<PostComment> =
        withContext(Dispatchers.IO) {
            val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId/comments").readText()
            Json.decodeFromString<List<JSONPlaceholder.PostComment>>(jsonText).map { it.comment }
        }


}