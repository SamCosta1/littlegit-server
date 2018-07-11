package com.littlegit.server.repo.repo

import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.auth.TokenType
import com.littlegit.server.model.user.UserId
import com.littlegit.server.repo.AuthRepository
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.util.inject
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

            // Ensure value was cached
            val cacheKey = AuthRepository.FULL_TOKEN.inject(token.token)
            val cached = RepositoryHelper.cache.get(cacheKey, Token::class.java)

            assertToken(userId, token, retrieved)
            assertToken(userId, token, cached)
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

            assertToken(userId, token, retrieved)

        } finally {
            cleaner()
        }
    }

    private fun assertToken(userId: UserId?, expected: Token, actual: Token?) {
        assertEquals(userId, actual?.userId)
        assertEquals(expected.token, actual?.token)
        assertEquals(expected.tokenType, actual?.tokenType)
        assertTrue(expected.expiry.isEqual(actual?.expiry))
    }


}