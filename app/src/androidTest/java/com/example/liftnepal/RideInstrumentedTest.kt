package com.example.liftnepal

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RideInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun waitForApp() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Rides").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForLogin() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ─── TEST 1: App launches without crashing ──────────────────────────
    @Test
    fun app_launchesSuccessfully() {
        waitForApp()
        // Reaching here without exception = success
    }

    // ─── TEST 2: Splash screen auto-transitions (does not get stuck) ────
    @Test
    fun splashScreen_transitionsAutomatically() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Welcome Back").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Rides").fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ─── TEST 3: Login screen shows "Welcome Back" title ───────────────
    @Test
    fun loginScreen_showsWelcomeBackTitle() {
        waitForLogin()
        composeTestRule.onNodeWithText("Welcome Back").assertIsDisplayed()
    }

    // ─── TEST 4: Login screen shows "Login to continue" subtitle ───────
    @Test
    fun loginScreen_showsLoginToContinueSubtitle() {
        waitForLogin()
        composeTestRule.onNodeWithText("Login to continue").assertIsDisplayed()
    }

    // ─── TEST 5: Admin email + wrong password shows error message ──────
    @Test
    fun loginScreen_adminEmailWrongPassword_showsError() {
        waitForLogin()
        composeTestRule.onNodeWithText("Email Address").performTextInput("admin@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("wrongpassword")
        composeTestRule.onNodeWithText("Login").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Invalid admin credentials.")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Invalid admin credentials.").assertIsDisplayed()
    }

    // ─── TEST 6: Signup → back to login works ───────────────────────────
    @Test
    fun signupScreen_backToLogin_works() {
        waitForLogin()
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        // Wait for signup screen's unique subtitle
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Sign up to get started")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Alredy have an account? login")
            .performScrollTo()
            .performClick()
        // Wait for signup to disappear first
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Sign up to get started")
                .fetchSemanticsNodes().isEmpty()
        }
        // Then wait for login (splash has 2500ms delay before navigating to login)
        composeTestRule.waitUntil(timeoutMillis = 20000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithText("Login")[0].assertIsDisplayed()
    }

    // ─── TEST 7: Signup with mismatched passwords shows error ───────────
    @Test
    fun signupScreen_passwordMismatch_showsError() {
        waitForLogin()
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Username").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithText("Username")[0].performTextInput("TestUser")
        composeTestRule.onAllNodesWithText("Email Address")[0].performTextInput("test@gmail.com")
        composeTestRule.onAllNodesWithText("Phone Number")[0].performTextInput("9800000000")
        composeTestRule.onAllNodesWithText("Password")[0].performTextInput("Password@123")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("Different@123")
        // Click the button (index 1 = button, index 0 = title text)
        composeTestRule.onAllNodesWithText("Create Account")[1].performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Passwords do not match")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Passwords do not match").assertIsDisplayed()
    }

    // ─── TEST 8: Signup with empty fields shows "Please fill all fields" ─
    @Test
    fun signupScreen_emptyFields_showsError() {
        waitForLogin()
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Create Account").fetchSemanticsNodes().size >= 2
        }
        // Click the button (index 1 = button, index 0 = title text)
        composeTestRule.onAllNodesWithText("Create Account")[1].performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Please fill all fields")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Please fill all fields").assertIsDisplayed()
    }
}