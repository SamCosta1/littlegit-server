package com.littlegit.server.repo.repo

import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthRepoTests {

    @Test
    fun testCreateAuthToken() {
        val testEmail = "create.auth@token.test"

        val cleaner = {
            CleanupHelper.cleanupUser(testEmail)
        }

        try {

            val userId = RepositoryHelper.userRepository.createUser(UserHelper.createSignupModel(testEmail))
            assertNotNull(userId)

            val token = RepositoryHelper.authRepository.createAndSaveAccessToken(userId!!)

            assertTrue(token.isNotBlank())

        } finally {
            cleaner()
        }
    }
}