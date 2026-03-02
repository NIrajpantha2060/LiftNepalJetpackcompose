package com.example.liftnepal.viewmodel



import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.repository.AuthRepository
import com.example.liftnepal.data.utils.Result
import com.google.firebase.auth.FirebaseUser
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
import com.example.liftnepal.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner.Silent::class)
class AuthViewModelTest {

    // ─── Fake repository — we control what it returns ─────────────
    @Mock
    lateinit var mockRepository: AuthRepository

    @Mock
    lateinit var mockFirebaseUser: FirebaseUser

    // ─── The real ViewModel we are testing ────────────────────────
    private lateinit var authViewModel: AuthViewModel

    // Coroutine test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Use test dispatcher so coroutines run in controlled way
        Dispatchers.setMain(testDispatcher)
        // Inject mock repository into ViewModel
        authViewModel = AuthViewModel(repository = mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── TEST 1: login() sets Loading then Success state ──────────
    // When: user calls login with correct credentials
    // Then: loginState should be Success with the user
    @Test
    fun `login success updates loginState to Success`() = runTest {
        // ARRANGE — mock repository returns success
        whenever(mockRepository.login("test@gmail.com", "pass123"))
            .thenReturn(Result.Success(mockFirebaseUser))

        // ACT — call login on ViewModel
        authViewModel.login("test@gmail.com", "pass123")
        advanceUntilIdle() // wait for coroutine to finish

        // ASSERT — loginState should now be Success
        val state = authViewModel.loginState.value
        assertTrue("Expected Success but got $state", state is Result.Success)
        assertEquals(mockFirebaseUser, (state as Result.Success).data)
    }

    // ─── TEST 2: login() with wrong password sets Error state ─────
    // When: user types wrong password
    // Then: loginState should be Error
    @Test
    fun `login failure updates loginState to Error`() = runTest {
        // ARRANGE
        whenever(mockRepository.login("test@gmail.com", "wrongpass"))
            .thenReturn(Result.Error("Invalid password"))

        // ACT
        authViewModel.login("test@gmail.com", "wrongpass")
        advanceUntilIdle()

        // ASSERT
        val state = authViewModel.loginState.value
        assertTrue("Expected Error but got $state", state is Result.Error)
        assertEquals("Invalid password", (state as Result.Error).message)
    }

    // ─── TEST 3: signup() sets Success state ──────────────────────
    // When: new user fills signup form correctly
    // Then: signupState should be Success
    @Test
    fun `signup success updates signupState to Success`() = runTest {
        // ARRANGE
        whenever(mockRepository.signup("Ram", "ram@gmail.com", "pass123", "9800000000"))
            .thenReturn(Result.Success(mockFirebaseUser))

        // ACT
        authViewModel.signup("Ram", "ram@gmail.com", "pass123", "9800000000")
        advanceUntilIdle()

        // ASSERT
        val state = authViewModel.signupState.value
        assertTrue("Expected Success but got $state", state is Result.Success)
    }

    // ─── TEST 4: signup() with existing email sets Error state ────
    // When: email already exists in Firebase
    // Then: signupState should be Error
    @Test
    fun `signup with existing email updates signupState to Error`() = runTest {
        // ARRANGE
        whenever(mockRepository.signup("Ram", "existing@gmail.com", "pass123", "9800000000"))
            .thenReturn(Result.Error("Email already in use"))

        // ACT
        authViewModel.signup("Ram", "existing@gmail.com", "pass123", "9800000000")
        advanceUntilIdle()

        // ASSERT
        val state = authViewModel.signupState.value
        assertTrue("Expected Error but got $state", state is Result.Error)
        assertEquals("Email already in use", (state as Result.Error).message)
    }

    // ─── TEST 5: logout() calls repository.logout() ───────────────
    // When: user presses logout
    // Then: repository.logout() must be called
    @Test
    fun `logout calls repository logout`() {
        // ACT
        authViewModel.logout()

        // ASSERT — verify logout was actually called on repository
        verify(mockRepository).logout()
    }

    // ─── TEST 6: logout() clears all states ───────────────────────
    // When: user logs out
    // Then: loginState and signupState should be null
    @Test
    fun `logout clears loginState and signupState`() {
        // ACT
        authViewModel.logout()

        // ASSERT
        assertNull(authViewModel.loginState.value)
        assertNull(authViewModel.signupState.value)
    }

    // ─── TEST 7: resetPassword() sets Success state ───────────────
    // When: user requests password reset with valid email
    // Then: resetState should be Success
    @Test
    fun `resetPassword success updates resetState to Success`() = runTest {
        // ARRANGE
        whenever(mockRepository.sendPasswordReset("user@gmail.com"))
            .thenReturn(Result.Success(true))

        // ACT
        authViewModel.resetPassword("user@gmail.com")
        advanceUntilIdle()

        // ASSERT
        val state = authViewModel.resetState.value
        assertTrue("Expected Success but got $state", state is Result.Success)
    }

    // ─── TEST 8: clearLoginState() sets loginState to null ────────
    // When: screen calls clearLoginState() after handling result
    // Then: loginState should be null
    @Test
    fun `clearLoginState sets loginState to null`() = runTest {
        // ARRANGE — first set a state
        whenever(mockRepository.login("test@gmail.com", "pass123"))
            .thenReturn(Result.Success(mockFirebaseUser))
        authViewModel.login("test@gmail.com", "pass123")
        advanceUntilIdle()

        // ACT — clear it
        authViewModel.clearLoginState()

        // ASSERT
        assertNull(authViewModel.loginState.value)
    }
}