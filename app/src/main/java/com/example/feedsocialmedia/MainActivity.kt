package com.example.feedsocialmedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.feedsocialmedia.ui.composables.LoginPage
import com.example.feedsocialmedia.ui.composables.PostDetailPage
import com.example.feedsocialmedia.ui.composables.PostListPage
import com.example.feedsocialmedia.ui.theme.FeedSocialMediaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            FeedSocialMediaTheme {
                val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Login) }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { entry ->
                        when(entry) {
                           is AppRoute.Home -> NavEntry(entry) {
                               PostListPage { post ->
                                    backStack.add(AppRoute.PostDetail(post.id))
                               }
                           }
                           is AppRoute.PostDetail -> NavEntry(entry) {
                               PostDetailPage(postId = entry.postId)
                           }
                            is AppRoute.Login -> NavEntry(entry){
                                LoginPage()
                            }
                        }
                    }
                )
            }
        }
    }
}

sealed class AppRoute {
    data object Home : AppRoute()
    data class PostDetail(val postId: String): AppRoute()

    data object Login : AppRoute()
}