package com.example.feedsocialmedia

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
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feedsocialmedia.ui.theme.FeedSocialMediaTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class PostDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeedSocialMediaTheme {
                PostDetailPage()
            }
        }
    }
}

@Composable
@Preview
fun PostDetailPage() {

    var state by remember { mutableStateOf<SuspendState>(LoadingState()) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val stateSnap = state
            when (stateSnap) {
                is ErrorState ->  Box(
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

                is SuccessState<*> -> {

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
                                Text("Post#1")
                            }
                            Text("dslajkdksajdksjkda", style = MaterialTheme.typography.titleLarge)
                            Text("dsajdhsakdhsajdsa")
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

                    LazyColumn {
                        items(10) { index ->
                            PostCommentListTitle()
                        }
                    }


                }
            }
        }
    }

}

@Composable
fun PostCommentListTitle() {
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
                Text("id labore ex et quam laborum")
                Badge(Modifier.weight(1f)) {
                    Text("jayne_kuhic@sydney.com", maxLines = 1)
                }
            }
        }

    }
}

suspend fun fetchPostDetail(postId: String) {
    delay(4.seconds)
    PostDetail(
        id = "1",
        title = "dhsakhdgsahijhdkjashdkjsahdkjsa",
        body = "djakshdkashdkjasnckzxncy7wquew98u1927321731",
        comments = emptyList()
    )
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
    val comment: String
)
