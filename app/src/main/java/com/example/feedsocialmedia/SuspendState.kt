package com.example.feedsocialmedia

sealed class SuspendState<out SuccessDataType> {
    data object Loading : SuspendState<Nothing>()
    data class Error(val errorMessage: String) : SuspendState<Nothing>()
    data class Success<out T>(val data: T) : SuspendState<T>()
}

