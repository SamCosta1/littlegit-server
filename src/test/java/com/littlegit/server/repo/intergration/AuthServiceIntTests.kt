package com.littlegit.server.repo.intergration

import com.littlegit.server.model.user.LoginModel
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.service.AuthService
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthServiceIntTests{

    private val authService: AuthService = AuthService(RepositoryHelper.authRepository, RepositoryHelper.userRepository)

    @Test
    fun testValidLogin() {
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
            UserHelper.assertSignupModel(testUserSignupModel, loginResult.user)

        } finally {
            cleaner()
        }
    }
}