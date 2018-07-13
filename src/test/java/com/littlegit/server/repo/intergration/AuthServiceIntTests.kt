package com.littlegit.server.repo.intergration

import com.littlegit.server.authfilter.AuthConstants
import com.littlegit.server.model.auth.RefreshRequest
import com.littlegit.server.model.user.LoginModel
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.service.AuthService
import org.junit.Test
import kotlin.math.log
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthServiceIntTests{

    private val authService: AuthService = AuthService( RepositoryHelper.authRepository,
                                                        RepositoryHelper.userRepository,
                                                        RepositoryHelper.settingsProvider)

    @Test
    fun testValidLogin_IsSuccessful() {
        val testEmail = "gimply@lonely.mountain.com"
        val testPassword = "password"

        val cleaner = {
            CleanupHelper.cleanupUser(testEmail)
        }

        cleaner()

        val testUserSignupModel = UserHelper.createSignupModel(testEmail, testPassword)

        try {
            // Create a test user
            RepositoryHelper.userRepository.createUser(testUserSignupModel)

            // Login as that user
            val loginResult = authService.login(LoginModel(testEmail, testPassword))

            assertNotNull(loginResult)
            assertTrue(loginResult.accessToken.isNotBlank())
            assertTrue(loginResult.refreshToken.isNotBlank())
            assertEquals(AuthConstants.AuthScheme, loginResult.scheme)
            UserHelper.assertSignupModel(testUserSignupModel, loginResult.user)

        } finally {
            cleaner()
        }
    }

    @Test
    fun testRefreshToken_IsSuccessful() {
        val testEmail = "gimply@lonely.mountain.com"
        val testPassword = "password"

        val cleaner = {
            CleanupHelper.cleanupUser(testEmail)
        }

        cleaner()
        val testUserSignupModel = UserHelper.createSignupModel(testEmail, testPassword)

        try {
            // Create a test user
            RepositoryHelper.userRepository.createUser(testUserSignupModel)

            // Login as that user
            val loginResult = authService.login(LoginModel(testEmail, testPassword))

            // Refresh the token
            val newTokens = authService.refreshToken(RefreshRequest(loginResult.refreshToken, loginResult.user.id))
            assertNotNull(newTokens)
            assertTrue(newTokens.accessToken.isNotBlank())
            assertEquals(AuthConstants.AuthScheme, newTokens.scheme)
            assertEquals(RepositoryHelper.settingsProvider.settings.tokens.accessTokenDuration, newTokens.expiry)
        } finally {
            cleaner()
        }
    }

}