package com.example.feedsocialmedia.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feedsocialmedia.viewmodel.PostDetailPageViewModel
import com.example.feedsocialmedia.SuspendState


@Composable

fun PostDetailPage(viewModel: PostDetailPageViewModel = viewModel(), postId: String) {

    val state = viewModel.state

    LaunchedEffect(postId) {
        viewModel.fetchPost(postId)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = state) {
                is SuspendState.Error -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.errorMessage)
                }

                is SuspendState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is SuspendState.Success -> {
                    val postDetail = state.data

                    LazyColumn {
                        item {
                            OutlinedCard(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Badge {
                                        Text("Post #${postDetail.id}")
                                    }
                                    Text(
                                        postDetail.title,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(postDetail.body)
                                }
                            }
                            HorizontalDivider()
                            Text(
                                "Comments",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 22.dp)
                            )
                        }

                        items(postDetail.comments) { comment ->
                            PostCommentListTile(comment)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCommentListTile(comment: PostComment) {
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(comment.title, style = MaterialTheme.typography.titleSmall)
                Badge {
                    Text(comment.commenterEmail, maxLines = 1)
                }
            }
            Text(comment.text)
        }
    }
}


data class PostDetail(
    val id: String,
    val title: String,
    val body: String,
    val comments: List<PostComment>
)

data class PostComment(
    val title: String,
    val commenterEmail: String,
    val text: String
)
