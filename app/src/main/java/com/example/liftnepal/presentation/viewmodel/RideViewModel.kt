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

    private val _riderRidesState = MutableStateFlow<Result<List<Ride>>?>(null)
    val riderRidesState: StateFlow<Result<List<Ride>>?> = _riderRidesState

    private val _myBookingsState = MutableStateFlow<Result<List<Ride>>?>(null)
    val myBookingsState: StateFlow<Result<List<Ride>>?> = _myBookingsState

    private val _updateRideState = MutableStateFlow<Result<Boolean>?>(null)
    val updateRideState: StateFlow<Result<Boolean>?> = _updateRideState

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

    fun updateRideStatus(rideId: String, status: String) {
        viewModelScope.launch {
            _updateRideState.value = Result.Loading
            _updateRideState.value = repository.updateRideStatus(rideId, status)
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

    fun cancelBooking(rideId: String, userId: String) {
        viewModelScope.launch {
            _updateRideState.value = Result.Loading
            val result = repository.cancelBooking(rideId)
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