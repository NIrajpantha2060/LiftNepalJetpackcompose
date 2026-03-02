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
class AuthInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ─── TEST 1: Login screen shows email, password, and login button ───
    @Test
    fun loginScreen_showsEmailPasswordAndLoginButton() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Email Address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    // ─── TEST 2: Login screen shows "Forgot Password?" link ────────────
    @Test
    fun loginScreen_showsForgotPasswordLink() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Forgot Password?").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
    }

    // ─── TEST 3: Login screen shows "Don't have an account?" link ──────
    @Test
    fun loginScreen_showsSignUpLink() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Don't have an account? Sign up").assertIsDisplayed()
    }

    // ─── TEST 4: User can type in the email field ───────────────────────
    @Test
    fun loginScreen_canTypeInEmailField() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Email Address").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Email Address").performTextInput("test@gmail.com")
        composeTestRule.onNodeWithText("test@gmail.com").assertIsDisplayed()
    }

    // ─── TEST 5: Tapping "Sign up" navigates to signup screen ──────────
    @Test
    fun loginScreen_clickSignUp_navigatesToSignupScreen() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Don't have an account? Sign up")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Create Account").fetchSemanticsNodes().size >= 2
        }
        // Two nodes exist: the title Text and the Button — assert on the first one
        composeTestRule.onAllNodesWithText("Create Account")[0].assertIsDisplayed()
    }

    // ─── TEST 6: Tapping "Forgot Password?" navigates to reset screen ──
    @Test
    fun loginScreen_clickForgotPassword_navigatesToResetScreen() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Forgot Password?").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Forgot Password?").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Reset Password")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Forgot Password")
                        .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // ─── TEST 7: Pressing Login with empty fields does NOT crash ───────
    @Test
    fun loginScreen_emptyLogin_doesNotCrash() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Login").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Login").performClick()
        // Still on login screen = no crash
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    // ─── TEST 8: Signup screen shows all required input fields ─────────
    @Test
    fun signupScreen_showsAllRequiredFields() {
        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Don't have an account? Sign up")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Don't have an account? Sign up").performClick()
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Username").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onAllNodesWithText("Username")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Email Address")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Phone Number")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Password")[0].assertIsDisplayed()
    }
}