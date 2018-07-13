package com.littlegit.server.repo.service

import com.littlegit.server.application.exception.InvalidTokenException
import com.littlegit.server.application.exception.NotFoundException
import com.littlegit.server.application.settings.*
import com.littlegit.server.authfilter.AuthConstants
import com.littlegit.server.model.auth.RefreshRequest
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.auth.TokenType
import com.littlegit.server.repo.AuthRepository
import com.littlegit.server.repo.UserRepository
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.upon
import com.littlegit.server.service.AuthService
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.expect

class AuthServiceTests {

    private lateinit var authService: AuthService
    private lateinit var userRepoMock: UserRepository
    private lateinit var authRepoMock: AuthRepository
    private lateinit var settingsProviderMock: SettingsProvider

    @Before
    fun setup() {
        userRepoMock = mock(UserRepository::class.java)
        authRepoMock = mock(AuthRepository::class.java)
        settingsProviderMock = mock(SettingsProvider::class.java)
        authService = AuthService(authRepoMock, userRepoMock, settingsProviderMock)
    }

    @Test(expected = InvalidTokenException::class)
    fun testGetUserForToken_WhenTokenNotFound_ThrowsException() {
        val token = "token"
        upon(authRepoMock.getFullToken(token)).thenReturn(null)
        authService.getUserForToken(token)
    }

    @Test(expected = NotFoundException::class)
    fun testGetUserForToken_WhenNoUserFound_ThrowsException() {
        val token = "token"
        val userId = 1
        upon(authRepoMock.getFullToken(token)).thenReturn(Token(userId, token, TokenType.AccessToken, OffsetDateTime.now()))
        upon(userRepoMock.getUser(userId)).thenReturn(null)
        authService.getUserForToken(token)
    }
    
    @Test
    fun testGetUserForToken_WhenOk_ThrowsException() {
        val token = "token"
        val userId = 1
        val user = UserHelper.createTestUser(userId)

        upon(authRepoMock.getFullToken(token)).thenReturn(Token(userId, token, TokenType.AccessToken, OffsetDateTime.now()))
        upon(userRepoMock.getUser(userId)).thenReturn(user)

        val returnedUser = authService.getUserForToken(token)
        assertEquals(user, returnedUser)
    }

    @Test
    fun testRefreshToken_WhenOk_IsSuccessful() {
        val token = "token"
        val newRawToken = "New Token $token"
        val userId = 1
        val duration = 10

        val tokensConfig = TokensConfig(duration, duration)
        upon(settingsProviderMock.settings).thenReturn(LittleGitSettings(mock(DbConfig::class.java), mock(RedisConfig::class.java), tokensConfig, true))
        upon(authRepoMock.getFullToken(token)).thenReturn(Token(userId, token, TokenType.RefreshToken, OffsetDateTime.now()))
        upon(authRepoMock.createAndSaveAccessToken(userId)).thenReturn(Token(userId, newRawToken, TokenType.AccessToken, OffsetDateTime.now()))

        val newToken = authService.refreshToken(RefreshRequest(token, userId))

        verify(authRepoMock).createAndSaveAccessToken(userId)
        assertEquals(newRawToken, newToken.accessToken)
        assertEquals(settingsProviderMock.settings.tokens.accessTokenDuration, newToken.expiry)
        assertEquals(AuthConstants.AuthScheme, newToken.scheme)
        assertEquals(duration, newToken.expiry)
    }

    @Test(expected = InvalidTokenException::class)
    fun testRefreshToken_WithInvalidRefreshToken_ThrowsException() {
        val token = "token"
        val userId = 1

        upon(authRepoMock.getFullToken(token)).thenReturn(null)
        authService.refreshToken(RefreshRequest(token, userId))
    }

    @Test(expected = InvalidTokenException::class)
    fun testRefreshToken_WithNonRefreshToken_ThrowsException() {
        val token = "token"
        val userId = 1

        upon(authRepoMock.getFullToken(token)).thenReturn(Token(userId, token, TokenType.AccessToken, OffsetDateTime.now().plusDays(2)))
        authService.refreshToken(RefreshRequest(token, userId))
    }
}