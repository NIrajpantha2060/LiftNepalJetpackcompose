package com.example.liftnepal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.repository.RideRepository
import com.example.liftnepal.data.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RideViewModel : ViewModel() {

    private val repository = RideRepository()

    // ─── Add Ride States ──────────────────────────────────────────

    private val _addRideState = MutableStateFlow<Result<Boolean>?>(null)
    val addRideState: StateFlow<Result<Boolean>?> = _addRideState

    // ─── All Rides States (for users) ─────────────────────────────

    private val _allRidesState = MutableStateFlow<Result<List<Ride>>?>(null)
    val allRidesState: StateFlow<Result<List<Ride>>?> = _allRidesState

    // ─── Rider's Rides States (for history) ────────────────────────

    private val _riderRidesState = MutableStateFlow<Result<List<Ride>>?>(null)
    val riderRidesState: StateFlow<Result<List<Ride>>?> = _riderRidesState

    // ─── Single Ride State (for details view) ──────────────────────

    private val _rideDetailsState = MutableStateFlow<Result<Ride?>?>(null)
    val rideDetailsState: StateFlow<Result<Ride?>?> = _rideDetailsState

    // ─── Update/Delete States ──────────────────────────────────────

    private val _updateRideState = MutableStateFlow<Result<Boolean>?>(null)
    val updateRideState: StateFlow<Result<Boolean>?> = _updateRideState

    private val _deleteRideState = MutableStateFlow<Result<Boolean>?>(null)
    val deleteRideState: StateFlow<Result<Boolean>?> = _deleteRideState

    // ─── Functions ─────────────────────────────────────────────────

    /**
     * Add a new ride
     */
    fun addRide(ride: Ride) {
        viewModelScope.launch {
            _addRideState.value = Result.Loading
            _addRideState.value = repository.addRide(ride)
        }
    }

    /**
     * Fetch all active rides (for user dashboard)
     */
    fun fetchAllActiveRides() {
        viewModelScope.launch {
            _allRidesState.value = Result.Loading
            _allRidesState.value = repository.getAllActiveRides()
        }
    }

    /**
     * Fetch rides by specific rider (for rider's history)
     */
    fun fetchRidesByRider(riderId: String) {
        viewModelScope.launch {
            _riderRidesState.value = Result.Loading
            _riderRidesState.value = repository.getRidesByRider(riderId)
        }
    }

    /**
     * Get single ride details
     */
    fun fetchRideDetails(rideId: String) {
        viewModelScope.launch {
            _rideDetailsState.value = Result.Loading
            _rideDetailsState.value = repository.getRideById(rideId)
        }
    }

    /**
     * Update ride status (complete/cancel)
     */
    fun updateRideStatus(rideId: String, status: String) {
        viewModelScope.launch {
            _updateRideState.value = Result.Loading
            _updateRideState.value = repository.updateRideStatus(rideId, status)
            // Refresh the lists after update
            fetchAllActiveRides()
        }
    }

    /**
     * Delete a ride
     */
    fun deleteRide(rideId: String) {
        viewModelScope.launch {
            _deleteRideState.value = Result.Loading
            _deleteRideState.value = repository.deleteRide(rideId)
            // Refresh after deletion
            fetchAllActiveRides()
        }
    }

    /**
     * Book a ride (for future)
     */
    fun bookRide(rideId: String, userId: String) {
        viewModelScope.launch {
            _updateRideState.value = Result.Loading
            _updateRideState.value = repository.bookRide(rideId, userId)
            fetchAllActiveRides()
        }
    }

    // ─── Clear States ──────────────────────────────────────────────

    fun clearAddRideState() { _addRideState.value = null }
    fun clearUpdateRideState() { _updateRideState.value = null }
    fun clearDeleteRideState() { _deleteRideState.value = null }
}