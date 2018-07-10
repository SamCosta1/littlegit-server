package com.littlegit.server.repo.repo

import com.littlegit.server.model.auth.TokenType
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import org.junit.Test
import java.time.OffsetDateTime
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

        cleaner()
        try {

            val userId = RepositoryHelper.userRepository.createUser(UserHelper.createSignupModel(testEmail))
            assertNotNull(userId)

            val token = RepositoryHelper.authRepository.createAndSaveAccessToken(userId!!)

            assertNotNull(token)
            assertTrue(token.token.isNotBlank())
            assertEquals(userId, token.userId)

            assertEquals(token.tokenType, TokenType.AccessToken)
            assertTrue(token.expiry.isAfter(OffsetDateTime.now()))

        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetAuthToken() {
        val testEmail = "get.auth@token.test"

        val cleaner = {
            CleanupHelper.cleanupUser(testEmail)
        }

        cleaner()

        try {

            val userId = RepositoryHelper.userRepository.createUser(UserHelper.createSignupModel(testEmail))
            assertNotNull(userId)

            val token = RepositoryHelper.authRepository.createAndSaveAccessToken(userId!!)

            val retrieved = RepositoryHelper.authRepository.getFullToken(token.token)
            assertNotNull(retrieved)

            assertEquals(userId, retrieved?.userId)
            assertEquals(token.token, retrieved?.token)
            assertEquals(token.tokenType, retrieved?.tokenType)
            assertTrue(token.expiry.isEqual(retrieved?.expiry))
        } finally {
            cleaner()
        }
    }


    @Test
    fun testCreateRefreshToken() {
        val testEmail = "create.auth@token.test"

        val cleaner = {
            CleanupHelper.cleanupUser(testEmail)
        }

        cleaner()
        try {

            val userId = RepositoryHelper.userRepository.createUser(UserHelper.createSignupModel(testEmail))
            assertNotNull(userId)

            val token = RepositoryHelper.authRepository.createAndSaveRefreshToken(userId!!)

            assertNotNull(token)
            assertTrue(token.token.isNotBlank())
            assertEquals(userId, token.userId)

            assertEquals(token.tokenType, TokenType.RefreshToken)
            assertTrue(token.expiry.isAfter(OffsetDateTime.now()))

        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetRefreshToken() {
        val testEmail = "get.auth@token.test"

        val cleaner = {
            CleanupHelper.cleanupUser(testEmail)
        }

        cleaner()

        try {

            val userId = RepositoryHelper.userRepository.createUser(UserHelper.createSignupModel(testEmail))
            assertNotNull(userId)

            val token = RepositoryHelper.authRepository.createAndSaveRefreshToken(userId!!)

            val retrieved = RepositoryHelper.authRepository.getFullToken(token.token)
            assertNotNull(retrieved)

            assertEquals(userId, retrieved?.userId)
            assertEquals(token.token, retrieved?.token)
            assertEquals(token.tokenType, retrieved?.tokenType)
            assertTrue(token.expiry.isEqual(retrieved?.expiry))

        } finally {
            cleaner()
        }
    }
}