package com.example.feedsocialmedia.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feedsocialmedia.viewmodel.PostListPageViewModel
import com.example.feedsocialmedia.SuspendState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListPage(onPostClick: (PostListItem) -> Unit) {
    val viewModel: PostListPageViewModel = viewModel()
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.downloadPosts()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Posts") },
                actions = {
                    TextButton(
                        enabled = state !is SuspendState.Loading,
                        onClick = viewModel::downloadPosts
                    ) {
                        Text("Retry")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = state) {
            is SuspendState.Loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is SuspendState.Error -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(state.errorMessage)
            }

            is SuspendState.Success -> LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                items(state.data, { it.id }) { post ->
                    PostListTile(post) { onPostClick(post) }
                }
            }
        }
    }
}

@Composable
private fun PostListTile(post: PostListItem, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(true) {
                onClick()
            }
    ) {
        Text(
            "User ID: ${post.userId} • Post #${post.id}",
            style = MaterialTheme.typography.titleSmall
        )
        Text(post.title, style = MaterialTheme.typography.titleLarge)
        Text(
            post.subtitle,
            maxLines = 3
        )
        HorizontalDivider()
    }
}



data class PostListItem(
    val id: String,
    val userId: String,
    val title: String,
    val subtitle: String,
)

