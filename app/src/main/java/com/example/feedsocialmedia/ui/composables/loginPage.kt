package com.example.feedsocialmedia.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


@Preview
@Composable
fun LoginPage(viewModel: LoginPAgeViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
            .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            OutlinedTextField(
                value = state.email,
//                onValueChange = {
//                    viewModel.onEmailChange(it)
//                }
                onValueChange = viewModel::onEmailChange,
                supportingText = {
                    state.emailErrorMessage?.let{
                        Text(it, color = Color.Red)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email ),
                isError = state.emailErrorMessage != null
            )
            Spacer( modifier = Modifier.size(16.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                visualTransformation = PasswordVisualTransformation('*')
            )
            Spacer( modifier = Modifier.size(8.dp))
            FilledTonalButton(onClick = viewModel::login) {
                Text("Login")
            }
        }
    }
}

class LoginPAgeViewModel: ViewModel() {
    data class UiState(
        val email: String = "",
        val password: String = "",
        val emailErrorMessage: String? = null,
    )

    val uiState: StateFlow<UiState>
        field = MutableStateFlow(UiState()) // explicit backing field

    fun onEmailChange(email: String) {
        uiState.update {
            it.copy(email = email)
        }
    }
    fun onPasswordChange(password: String) {
        uiState.update {
            it.copy(password = password)
        }
    }

    fun login() {
        if ( !uiState.value.email.contains('@')){
            uiState.update {
                it.copy(emailErrorMessage = "Email inválido")
            }
            return
        }

        uiState.update {
            it.copy(emailErrorMessage = null)
        }
    }

}