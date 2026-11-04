package com.example.feedsocialmedia

sealed class SuspendState<out SuccessDataType>

//class IdleState: SuspendState ()

data object LoadingState : SuspendState<Nothing>()

class ErrorState(val errorMessage: String) : SuspendState<Nothing>()

class SuccessState<out T>(val data: T ) : SuspendState<T>()