package com.littlegit.server.repo.service

import com.littlegit.server.application.exception.InvalidTokenException
import com.littlegit.server.application.exception.NotFoundException
import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.auth.TokenType
import com.littlegit.server.repo.AuthRepository
import com.littlegit.server.repo.UserRepository
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.upon
import com.littlegit.server.service.AuthService
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.time.OffsetDateTime
import kotlin.test.assertEquals
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
}