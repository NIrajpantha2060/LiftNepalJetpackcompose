package com.example.liftnepal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.repository.RideRepository
import com.example.liftnepal.data.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RideViewModel : ViewModel() {

    private val repository = RideRepository()

    private val _addRideState = MutableStateFlow<Result<Boolean>?>(null)
    val addRideState: StateFlow<Result<Boolean>?> = _addRideState

    private val _allRidesState = MutableStateFlow<Result<List<Ride>>?>(null)
    val allRidesState: StateFlow<Result<List<Ride>>?> = _allRidesState

    // ✅ NEW: All rides for admin (no status filter)
    private val _adminAllRidesState = MutableStateFlow<Result<List<Ride>>?>(null)
    val adminAllRidesState: StateFlow<Result<List<Ride>>?> = _adminAllRidesState

    private val _riderRidesState = MutableStateFlow<Result<List<Ride>>?>(null)
    val riderRidesState: StateFlow<Result<List<Ride>>?> = _riderRidesState

    private val _myBookingsState = MutableStateFlow<Result<List<Ride>>?>(null)
    val myBookingsState: StateFlow<Result<List<Ride>>?> = _myBookingsState

    private val _updateRideState = MutableStateFlow<Result<Boolean>?>(null)
    val updateRideState: StateFlow<Result<Boolean>?> = _updateRideState

    // ✅ NEW: Delete ride state for admin
    private val _deleteRideState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteRideState: StateFlow<Result<Boolean>?> = _deleteRideState

    fun addRide(ride: Ride) {
        viewModelScope.launch {
            _addRideState.value = Result.Loading
            _addRideState.value = repository.addRide(ride)
        }
    }

    fun fetchAllActiveRides() {
        viewModelScope.launch {
            _allRidesState.value = Result.Loading
            _allRidesState.value = repository.getAllActiveRides()
        }
    }

    // ✅ NEW: For admin — fetches ALL rides regardless of status
    fun fetchAllRidesForAdmin() {
        viewModelScope.launch {
            _adminAllRidesState.value = Result.Loading
            _adminAllRidesState.value = repository.getAllRides()
        }
    }

    // ✅ NEW: For admin — deletes a ride and refreshes the admin list
    fun deleteRide(rideId: String) {
        viewModelScope.launch {
            _deleteRideState.value = Result.Loading
            val result = repository.deleteRide(rideId)
            _deleteRideState.value = result
            if (result is Result.Success) {
                fetchAllRidesForAdmin()
            }
        }
    }

    fun clearDeleteRideState() { _deleteRideState.value = null }

    fun fetchRidesByRider(riderId: String) {
        viewModelScope.launch {
            _riderRidesState.value = Result.Loading
            _riderRidesState.value = repository.getRidesByRider(riderId)
        }
    }

    fun fetchMyBookings(userId: String) {
        viewModelScope.launch {
            _myBookingsState.value = Result.Loading
            _myBookingsState.value = repository.getRidesByPassenger(userId)
        }
    }

    fun updateRideStatus(rideId: String, status: String, cancelledBy: String = "") {
        viewModelScope.launch {
            _updateRideState.value = Result.Loading
            val result = repository.updateRideStatus(rideId, status, cancelledBy)
            _updateRideState.value = result
            if (result is Result.Success) {
                fetchAllActiveRides()
                fetchAllRidesForAdmin() // Refresh admin view too
            }
        }
    }

    fun bookRide(rideId: String, userData: User) {
        viewModelScope.launch {
            _updateRideState.value = Result.Loading
            val result = repository.bookRide(
                rideId = rideId,
                userId = userData.uid,
                userName = userData.displayName,
                userPhone = userData.phoneNumber,
                userPhotoUrl = userData.profilePhotoUrl
            )
            _updateRideState.value = result
            if (result is Result.Success) {
                fetchAllActiveRides()
                fetchMyBookings(userData.uid)
            }
        }
    }

    fun cancelBooking(rideId: String, userId: String, cancelledBy: String, newStatus: String = "cancelled") {
        viewModelScope.launch {
            _updateRideState.value = Result.Loading
            val result = repository.cancelBooking(rideId, newStatus, cancelledBy)
            _updateRideState.value = result
            if (result is Result.Success) {
                fetchAllActiveRides()
                fetchMyBookings(userId)
            }
        }
    }

    fun clearAddRideState() { _addRideState.value = null }
    fun clearUpdateRideState() { _updateRideState.value = null }
}