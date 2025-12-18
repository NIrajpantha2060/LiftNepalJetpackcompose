package com.example.liftnepal.presentation.viewmodel





import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.repository.AuthRepository
import com.example.liftnepal.data.utils.Result
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Login state
    private val _loginState = MutableStateFlow<Result<FirebaseUser>?>(null)
    val loginState: StateFlow<Result<FirebaseUser>?> = _loginState

    // Signup state
    private val _signupState = MutableStateFlow<Result<FirebaseUser>?>(null)
    val signupState: StateFlow<Result<FirebaseUser>?> = _signupState

    // Reset password state
    private val _resetState = MutableStateFlow<Result<Boolean>?>(null)
    val resetState: StateFlow<Result<Boolean>?> = _resetState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            _loginState.value = repository.login(email, password)
        }
    }

    fun signup(username: String, email: String, password: String) {
        viewModelScope.launch {
            _signupState.value = Result.Loading
            _signupState.value = repository.signup(username, email, password)
        }
    }

    // Add this to your AuthViewModel.kt
    fun logout() {
        repository.logout()
        clearLoginState()
        clearSignupState()
        clearResetState()
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetState.value = Result.Loading
            _resetState.value = repository.sendPasswordReset(email)
        }
    }

    fun clearLoginState() { _loginState.value = null }
    fun clearSignupState() { _signupState.value = null }
    fun clearResetState() { _resetState.value = null }
}