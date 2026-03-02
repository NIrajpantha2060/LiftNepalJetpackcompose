package com.example.liftnepal.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Vehicle
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.repository.AuthRepository
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.data.utils.SavedAccountManager
import com.example.liftnepal.data.utils.SavedCredentials
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

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

    private val _profilePhotoState = MutableStateFlow<Result<Boolean>?>(null)
    val profilePhotoState: StateFlow<Result<Boolean>?> = _profilePhotoState

    private val _vehicleUpdateState = MutableStateFlow<Result<Boolean>?>(null)
    val vehicleUpdateState: StateFlow<Result<Boolean>?> = _vehicleUpdateState

    // ─── Strike States ────────────────────────────────────────────

    private val _strikeState = MutableStateFlow<Result<Int>?>(null)
    val strikeState: StateFlow<Result<Int>?> = _strikeState

    private val _cancelVerificationState = MutableStateFlow<Result<Boolean>?>(null)
    val cancelVerificationState: StateFlow<Result<Boolean>?> = _cancelVerificationState

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
        _strikeState.value = null
        _cancelVerificationState.value = null
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetState.value = Result.Loading
            _resetState.value = repository.sendPasswordReset(email)
        }
    }

    // ─── Saved Credentials Functions ─────────────────────────────

    fun saveCredentials(context: Context, email: String, password: String) {
        SavedAccountManager.save(context, email, password)
    }

    fun loadAllSavedCredentials(context: Context): List<SavedCredentials> {
        return SavedAccountManager.loadAll(context)
    }

    fun removeSavedCredential(context: Context, email: String) {
        SavedAccountManager.remove(context, email)
    }

    fun clearAllSavedCredentials(context: Context) {
        SavedAccountManager.clearAll(context)
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

    fun updateProfilePhoto(photoUrl: String) {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            _profilePhotoState.value = Result.Loading
            _profilePhotoState.value = repository.updateProfilePhoto(uid, photoUrl)
            fetchCurrentUserData()
        }
    }

    fun clearProfilePhotoState() { _profilePhotoState.value = null }

    fun updateVehicleInfo(vehicle: Vehicle) {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            _vehicleUpdateState.value = Result.Loading
            _vehicleUpdateState.value = repository.updateVehicleInfo(uid, vehicle)
            fetchCurrentUserData()
        }
    }

    fun clearVehicleUpdateState() { _vehicleUpdateState.value = null }

    // ─── Strike Functions ─────────────────────────────────────────

    fun addStrike(uid: String) {
        viewModelScope.launch {
            _strikeState.value = Result.Loading
            val result = repository.addStrike(uid)
            _strikeState.value = result
            if (result is Result.Success) fetchAllUsers()
        }
    }

    fun removeStrike(uid: String) {
        viewModelScope.launch {
            _strikeState.value = Result.Loading
            val result = repository.removeStrike(uid)
            _strikeState.value = result
            if (result is Result.Success) fetchAllUsers()
        }
    }

    fun cancelVerification(uid: String, reason: String) {
        viewModelScope.launch {
            _cancelVerificationState.value = Result.Loading
            val result = repository.cancelVerification(uid, reason)
            _cancelVerificationState.value = result
            if (result is Result.Success) {
                fetchAllUsers()
                fetchAllVerifications()
            }
        }
    }

    fun clearStrikeState() { _strikeState.value = null }
    fun clearCancelVerificationState() { _cancelVerificationState.value = null }

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