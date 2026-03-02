package com.example.doctors

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DoctorsAppInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSuccessfulLogin_navigatesToDashboard() {
        // Fill login form and submit
        composeRule.onNodeWithTag("emailField").performTextInput("test@doctor.com")
        composeRule.onNodeWithTag("passwordField").performTextInput("password123")
        composeRule.onNodeWithTag("loginButton").performClick()

        // Verify navigation to dashboard
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Welcome Doctor").assertExists()
    }

    @Test
    fun testRegisterLink_navigatesToRegisterScreen() {
        // Click register link
        composeRule.onNodeWithTag("registerLink").performClick()

        // Verify navigation to registration
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Create Account").assertExists()
    }
}