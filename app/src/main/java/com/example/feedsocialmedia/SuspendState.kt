package com.example.feedsocialmedia

sealed class SuspendState

//class IdleState: SuspendState ()

class LoadingState : SuspendState()

class ErrorState(val errorMessage: String) : SuspendState()

class SuccessState<DataType>(val data: DataType ) : SuspendState()