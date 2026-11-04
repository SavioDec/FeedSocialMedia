package com.example.feedsocialmedia

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.feedsocialmedia.ui.theme.FeedSocialMediaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.time.Duration.Companion.seconds

class PostDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val postId = intent.getStringExtra("post_id") ?: ""
        setContent {
            FeedSocialMediaTheme {
                PostDetailPage(postId)
            }
        }
    }
}

@Composable
fun PostDetailPage(postId: String) {


    var state by remember { mutableStateOf<SuspendState<PostDetail>>(LoadingState) }
    LaunchedEffect(postId) {
        state = try {
            SuccessState(data = fetchPostDetail(postId))
        } catch (_: Exception) {
            ErrorState("Não foi possivel carregar o post #$postId")
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val stateSnap = state
            when (stateSnap) {
                is ErrorState -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stateSnap.errorMessage)
                }

                is LoadingState -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is SuccessState -> {

                    val postDetail = stateSnap.data


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
                            PostCommentListTitle(comment)
                        }
                    }


                }
            }
        }
    }

}

@Composable
fun PostCommentListTitle(comment: PostComment) {
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
                Badge(Modifier.weight(1f)) {
                    Text(comment.commenterEmail, maxLines = 1)
                }
            }
            Text(comment.text)
        }

    }
}

private suspend fun fetchPostDetail(postId: String) = withContext(Dispatchers.IO) {
    val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId").readText()
    val jsonObj = JSONObject(jsonText)
    PostDetail(
        id = jsonObj.getInt("id").toString(),
        title = jsonObj.getString("title"),
        body = jsonObj.getString("body"),
        comments = fetchPostComments(postId)
    )
}

private suspend fun fetchPostComments(postId: String): List<PostComment> = withContext(Dispatchers.IO){
    val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId/comments").readText()
    val jsonList = JSONArray(jsonText)
    List(jsonList.length()) { index ->
        val jsonObj = jsonList.getJSONObject(index)
        PostComment(
            title = jsonObj.getString("name"),
            commenterEmail = jsonObj.getString("email"),
            text = jsonObj.getString("body"),
        )
    }
}

data class PostDetail(
    val id: String,
    val title: String,
    val body: String,
    val comments: List<PostComment>,
)

data class PostComment(
    val title: String,
    val commenterEmail: String,
    val text: String,
)
