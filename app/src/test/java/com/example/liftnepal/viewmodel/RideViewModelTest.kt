package com.example.liftnepal.viewmodel



import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.repository.RideRepository
import com.example.liftnepal.data.utils.Result
import com.example.liftnepal.presentation.viewmodel.RideViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner.Silent::class)
class RideViewModelTest {

    @Mock
    lateinit var mockRepository: RideRepository

    private lateinit var rideViewModel: RideViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        rideViewModel = RideViewModel(repository = mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── TEST 1: addRide() sets Success state ─────────────────────
    // When: rider adds a new ride
    // Then: addRideState should be Success
    @Test
    fun `addRide success updates addRideState to Success`() = runTest {
        // ARRANGE
        val fakeRide = Ride(riderId = "rider1", startLocation = "Kathmandu", destination = "Pokhara")
        whenever(mockRepository.addRide(fakeRide))
            .thenReturn(Result.Success(true))

        // ACT
        rideViewModel.addRide(fakeRide)
        advanceUntilIdle()

        // ASSERT
        val state = rideViewModel.addRideState.value
        assertTrue("Expected Success but got $state", state is Result.Success)
    }

    // ─── TEST 2: addRide() failure sets Error state ───────────────
    // When: Firebase fails to save ride
    // Then: addRideState should be Error
    @Test
    fun `addRide failure updates addRideState to Error`() = runTest {
        // ARRANGE
        val fakeRide = Ride(riderId = "rider1")
        whenever(mockRepository.addRide(fakeRide))
            .thenReturn(Result.Error("Failed to add ride"))

        // ACT
        rideViewModel.addRide(fakeRide)
        advanceUntilIdle()

        // ASSERT
        val state = rideViewModel.addRideState.value
        assertTrue("Expected Error but got $state", state is Result.Error)
        assertEquals("Failed to add ride", (state as Result.Error).message)
    }

    // ─── TEST 3: fetchAllActiveRides() sets Success with rides ────
    // When: passenger opens rides page
    // Then: allRidesState should have list of rides
    @Test
    fun `fetchAllActiveRides success updates allRidesState`() = runTest {
        // ARRANGE
        val fakeRides = listOf(
            Ride(rideId = "r1", destination = "Pokhara"),
            Ride(rideId = "r2", destination = "Chitwan")
        )
        whenever(mockRepository.getAllActiveRides())
            .thenReturn(Result.Success(fakeRides))

        // ACT
        rideViewModel.fetchAllActiveRides()
        advanceUntilIdle()

        // ASSERT
        val state = rideViewModel.allRidesState.value
        assertTrue(state is Result.Success)
        assertEquals(2, (state as Result.Success).data.size)
    }

    // ─── TEST 4: fetchAllActiveRides() Firebase error ─────────────
    @Test
    fun `fetchAllActiveRides failure updates allRidesState to Error`() = runTest {
        // ARRANGE
        whenever(mockRepository.getAllActiveRides())
            .thenReturn(Result.Error("Network error"))

        // ACT
        rideViewModel.fetchAllActiveRides()
        advanceUntilIdle()

        // ASSERT
        val state = rideViewModel.allRidesState.value
        assertTrue(state is Result.Error)
    }

    // ─── TEST 5: deleteRide() calls repository and refreshes ──────
    // When: admin deletes a ride
    // Then: deleteRideState is Success AND fetchAllRidesForAdmin is called
    @Test
    fun `deleteRide success updates deleteRideState and refreshes list`() = runTest {
        // ARRANGE
        whenever(mockRepository.deleteRide("ride_123"))
            .thenReturn(Result.Success(true))
        whenever(mockRepository.getAllRides())
            .thenReturn(Result.Success(emptyList()))

        // ACT
        rideViewModel.deleteRide("ride_123")
        advanceUntilIdle()

        // ASSERT
        val state = rideViewModel.deleteRideState.value
        assertTrue(state is Result.Success)
        // Verify repository.deleteRide was actually called
        verify(mockRepository).deleteRide("ride_123")
    }

    // ─── TEST 6: fetchRidesByRider() returns rider's rides ────────
    // When: rider opens their ride history
    // Then: riderRidesState should have their rides
    @Test
    fun `fetchRidesByRider success updates riderRidesState`() = runTest {
        // ARRANGE
        val fakeRides = listOf(Ride(rideId = "r1", riderId = "rider_001"))
        whenever(mockRepository.getRidesByRider("rider_001"))
            .thenReturn(Result.Success(fakeRides))

        // ACT
        rideViewModel.fetchRidesByRider("rider_001")
        advanceUntilIdle()

        // ASSERT
        val state = rideViewModel.riderRidesState.value
        assertTrue(state is Result.Success)
        assertEquals(1, (state as Result.Success).data.size)
    }

    // ─── TEST 7: clearAddRideState() sets state to null ───────────
    @Test
    fun `clearAddRideState sets addRideState to null`() = runTest {
        // ARRANGE — first set a state
        val fakeRide = Ride(riderId = "rider1")
        whenever(mockRepository.addRide(fakeRide)).thenReturn(Result.Success(true))
        rideViewModel.addRide(fakeRide)
        advanceUntilIdle()

        // ACT
        rideViewModel.clearAddRideState()

        // ASSERT
        assertNull(rideViewModel.addRideState.value)
    }

    // ─── TEST 8: fetchMyBookings() returns passenger bookings ─────
    // When: passenger opens their bookings page
    // Then: myBookingsState should have their booked rides
    @Test
    fun `fetchMyBookings success updates myBookingsState`() = runTest {
        // ARRANGE
        val fakeBookings = listOf(
            Ride(rideId = "r1", bookedBy = "user_001"),
            Ride(rideId = "r2", bookedBy = "user_001")
        )
        whenever(mockRepository.getRidesByPassenger("user_001"))
            .thenReturn(Result.Success(fakeBookings))

        // ACT
        rideViewModel.fetchMyBookings("user_001")
        advanceUntilIdle()

        // ASSERT
        val state = rideViewModel.myBookingsState.value
        assertTrue(state is Result.Success)
        assertEquals(2, (state as Result.Success).data.size)
    }
}
