package com.littlegit.server.repo.repo

import com.littlegit.server.application.settings.*
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.auth.TokenType
import com.littlegit.server.model.user.UserId
import com.littlegit.server.repo.AuthRepository
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.upon
import com.littlegit.server.util.TokenGenerator
import com.littlegit.server.util.inject
import org.junit.Test
import org.mockito.Mockito.mock
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepoTests {

    @Test
    fun testCreateAuthToken_IsSuccess() {
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
    fun testGetAuthToken_WhenExpired_ReturnsNull() {
        val testEmail = "get.auth@token.test"
        val tokenDuration = 5
        val cleaner = {
            CleanupHelper.cleanupUser(testEmail)
        }

        cleaner()

        val tokensConfig = TokensConfig(tokenDuration, tokenDuration)
        val settingsProvider = mock(SettingsProvider::class.java)
        upon(settingsProvider.settings).thenReturn(LittleGitSettings(mock(DbConfig::class.java), mock(RedisConfig::class.java), tokensConfig, true))
        val authRepo = AuthRepository(RepositoryHelper.dbConnector, RepositoryHelper.cache, TokenGenerator(settingsProvider))

        try {
            val userId = RepositoryHelper.userRepository.createUser(UserHelper.createSignupModel(testEmail))
            assertNotNull(userId)

            val token = authRepo.createAndSaveAccessToken(userId!!)
            val retrievedToken = RepositoryHelper.authRepository.getFullToken(token.token)

            println("Retrieved Token: ${retrievedToken.toString()}")
            // Should exist now
            assertToken(userId, token, retrievedToken)

            Thread.sleep((tokenDuration.toLong() + 1) * 1000)

            // Should no longer exist
            val reRetrievedToken = RepositoryHelper.authRepository.getFullToken(token.token)
            assertNull(reRetrievedToken)

        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetAuthToken_IsSuccess() {
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
    fun testCreateRefreshToken_IsSuccess() {
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
    fun testGetRefreshToken_IsSuccess() {
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

            // Ensure value was cached
            val cacheKey = AuthRepository.FULL_TOKEN.inject(token.token)
            val cached = RepositoryHelper.cache.get(cacheKey, Token::class.java)

            assertToken(userId, token, retrieved)
            assertToken(userId, token, cached)

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