package com.example.liftnepal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Vehicle
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.repository.AuthRepository
import com.example.liftnepal.data.utils.Result
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // ─── Auth States ─────────────────────────────────────────────

    private val _loginState = MutableStateFlow<Result<FirebaseUser>?>(null)
    val loginState: StateFlow<Result<FirebaseUser>?> = _loginState

    private val _signupState = MutableStateFlow<Result<FirebaseUser>?>(null)
    val signupState: StateFlow<Result<FirebaseUser>?> = _signupState

    private val _resetState = MutableStateFlow<Result<Boolean>?>(null)
    val resetState: StateFlow<Result<Boolean>?> = _resetState

    // ─── Users Table States ───────────────────────────────────────

    private val _usersList = MutableStateFlow<Result<List<User>>?>(null)
    val usersList: StateFlow<Result<List<User>>?> = _usersList

    private val _deleteUserState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteUserState: StateFlow<Result<Boolean>?> = _deleteUserState

    private val _currentUserData = MutableStateFlow<Result<User>?>(null)
    val currentUserData: StateFlow<Result<User>?> = _currentUserData

    // Profile photo update state
    private val _profilePhotoState = MutableStateFlow<Result<Boolean>?>(null)
    val profilePhotoState: StateFlow<Result<Boolean>?> = _profilePhotoState

    // Vehicle update state
    private val _vehicleUpdateState = MutableStateFlow<Result<Boolean>?>(null)
    val vehicleUpdateState: StateFlow<Result<Boolean>?> = _vehicleUpdateState

    // ─── Verifications Table States ───────────────────────────────

    private val _myVerification = MutableStateFlow<Result<Verification?>?>(null)
    val myVerification: StateFlow<Result<Verification?>?> = _myVerification

    private val _verificationSubmitState = MutableStateFlow<Result<Boolean>?>(null)
    val verificationSubmitState: StateFlow<Result<Boolean>?> = _verificationSubmitState

    private val _allVerifications = MutableStateFlow<Result<List<Pair<User, Verification>>>?>(null)
    val allVerifications: StateFlow<Result<List<Pair<User, Verification>>>?> = _allVerifications

    private val _verificationUpdateState = MutableStateFlow<Result<Boolean>?>(null)
    val verificationUpdateState: StateFlow<Result<Boolean>?> = _verificationUpdateState

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    // ─── Auth Functions ───────────────────────────────────────────

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

    fun logout() {
        repository.logout()
        clearLoginState()
        clearSignupState()
        clearResetState()
        _myVerification.value = null
        _currentUserData.value = null
        _profilePhotoState.value = null
        _vehicleUpdateState.value = null
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetState.value = Result.Loading
            _resetState.value = repository.sendPasswordReset(email)
        }
    }

    // ─── Users Table Functions ────────────────────────────────────

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
            if (result is Result.Success) fetchAllUsers()
        }
    }

    fun fetchCurrentUserData() {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            _currentUserData.value = Result.Loading
            _currentUserData.value = repository.getCurrentUserData(uid)
        }
    }

    // Save profile photo URL to users/{uid}/profilePhotoUrl
    fun updateProfilePhoto(photoUrl: String) {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            _profilePhotoState.value = Result.Loading
            _profilePhotoState.value = repository.updateProfilePhoto(uid, photoUrl)
            // Refresh user data so photo appears everywhere immediately
            fetchCurrentUserData()
        }
    }

    fun clearProfilePhotoState() { _profilePhotoState.value = null }

    // Save vehicle info to users/{uid}/vehicle
    fun updateVehicleInfo(vehicle: Vehicle) {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            _vehicleUpdateState.value = Result.Loading
            _vehicleUpdateState.value = repository.updateVehicleInfo(uid, vehicle)
            // Refresh user data so vehicle appears everywhere immediately
            fetchCurrentUserData()
        }
    }

    fun clearVehicleUpdateState() { _vehicleUpdateState.value = null }

    // ─── Verifications Table Functions ───────────────────────────

    fun fetchMyVerification() {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            _myVerification.value = Result.Loading
            _myVerification.value = repository.getVerification(uid)
        }
    }

    fun submitVerification(
        licenseNumber: String,
        licenseExpiryDate: String,
        licensePhotoUrl: String
    ) {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            _verificationSubmitState.value = Result.Loading
            _verificationSubmitState.value = repository.submitVerification(
                uid = uid,
                licenseNumber = licenseNumber,
                licenseExpiryDate = licenseExpiryDate,
                licensePhotoUrl = licensePhotoUrl
            )
            fetchMyVerification()
        }
    }

    fun fetchAllVerifications() {
        viewModelScope.launch {
            _allVerifications.value = Result.Loading
            _allVerifications.value = repository.getAllVerifications()
        }
    }

    fun approveVerification(uid: String) {
        viewModelScope.launch {
            _verificationUpdateState.value = Result.Loading
            _verificationUpdateState.value = repository.updateVerificationStatus(uid, "approved")
            fetchAllVerifications()
        }
    }

    fun rejectVerification(uid: String, remarks: String) {
        viewModelScope.launch {
            _verificationUpdateState.value = Result.Loading
            _verificationUpdateState.value = repository.updateVerificationStatus(uid, "rejected", remarks)
            fetchAllVerifications()
        }
    }

    // ─── Clear States ─────────────────────────────────────────────

    fun clearLoginState() { _loginState.value = null }
    fun clearSignupState() { _signupState.value = null }
    fun clearResetState() { _resetState.value = null }
    fun clearDeleteUserState() { _deleteUserState.value = null }
    fun clearVerificationSubmitState() { _verificationSubmitState.value = null }
}