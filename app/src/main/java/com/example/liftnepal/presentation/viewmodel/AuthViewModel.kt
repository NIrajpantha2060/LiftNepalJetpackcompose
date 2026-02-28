package com.example.liftnepal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.model.User
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

    // Admin: Users list state
    private val _usersList = MutableStateFlow<Result<List<User>>?>(null)
    val usersList: StateFlow<Result<List<User>>?> = _usersList

    // Admin: Delete user state
    private val _deleteUserState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteUserState: StateFlow<Result<Boolean>?> = _deleteUserState

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            _loginState.value = repository.login(email, password)
        }
    }

    fun signup(username: String, email: String, password: String, phoneNumber: String) {
        viewModelScope.launch {
            _signupState.value = Result.Loading
            _signupState.value = repository.signup(username, email, password, phoneNumber)
        }
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _usersList.value = Result.Loading
            _usersList.value = repository.getAllUsers()
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _deleteUserState.value = Result.Loading
            val result = repository.deleteUser(userId)
            _deleteUserState.value = result
            if (result is Result.Success) {
                fetchAllUsers() // Refresh list after deletion
            }
        }
    }

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
    fun clearDeleteUserState() { _deleteUserState.value = null }
}
