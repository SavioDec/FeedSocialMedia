package com.example.feedsocialmedia.jsonplaceholder

import com.example.feedsocialmedia.SuspendState
import com.example.feedsocialmedia.ui.composables.PostComment as appPostComment
import com.example.feedsocialmedia.ui.composables.PostDetail as appPostDetail
import com.example.feedsocialmedia.ui.composables.PostListItem as appPostListItem
import kotlinx.serialization.Serializable

object JSONPlaceholder {
    @Serializable
    class PostDetail(
        val userId: Int,
        val id: Int,
        val title: String,
        val body: String,
    ) {
        fun toPostDetail(comments: List<appPostComment>): appPostDetail{
            return appPostDetail(
                id = id.toString(),
                title = title,
                body = body,
                comments = comments
            )
        }
    }

    @Serializable
    class PostComment(
        val id: Int,
        val postId: Int,
        val name: String,
        val email: String,
        val body: String,
    ) {
        val comment: appPostComment
            get() = appPostComment(
                title = name,
                commenterEmail = email,
                text = body
            )
    }

    @Serializable
    data class PostListItem(
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String,
    ) {
        fun toPostListItem(): appPostListItem {
            return appPostListItem(
                id = id.toString(),
                userId = userId.toString(),
                title = title,
                subtitle = body
            )
        }
    }
}