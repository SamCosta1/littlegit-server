package com.littlegit.server.repo.service

import com.littlegit.server.application.exception.EmailInUseException
import com.littlegit.server.application.exception.UserForbiddenException
import com.littlegit.server.application.exception.UsernameInUseException
import com.littlegit.server.application.remoterunner.RemoteCommandRunner
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.repo.GitServerRepository
import com.littlegit.server.repo.SshKeyRepository
import com.littlegit.server.repo.UserRepository
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.upon
import com.littlegit.server.service.UserService
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class UserServiceTests {

    private lateinit var userService: UserService
    private lateinit var userRepoMock: UserRepository
    private lateinit var gitServerRepoMock: GitServerRepository
    private lateinit var sshKeyRepoMock: SshKeyRepository
    private lateinit var remoteCommandRunner: RemoteCommandRunner

    @Before
    fun setup() {
        userRepoMock = mock(UserRepository::class.java)
        gitServerRepoMock = mock(GitServerRepository::class.java)
        sshKeyRepoMock = mock(SshKeyRepository::class.java)
        remoteCommandRunner = mock(RemoteCommandRunner::class.java)

        userService = UserService(userRepoMock, gitServerRepoMock, remoteCommandRunner, sshKeyRepoMock)
    }

    @Test(expected = EmailInUseException::class)
    fun testCreateUser_UserEmailInUse_ThrowsException() {
        val testEmail = "test@example.com"

        upon(userRepoMock.getUser(testEmail)).thenReturn(UserHelper.createTestUser(email = testEmail))

        userService.createUser(UserHelper.createSignupModel(email = testEmail))
    }

    @Test(expected = UsernameInUseException::class)
    fun testCreateUser_UsernameInUse_ThrowsException() {
        val testEmail = "test@example.com"
        val testUsername = "test-username"

        upon(userRepoMock.getUser(testEmail)).thenReturn(null)
        upon(userRepoMock.getUserByUsername(testUsername)).thenReturn(UserHelper.createTestUser(email = testEmail, username = testUsername))

        userService.createUser(UserHelper.createSignupModel(email = testEmail, username = testUsername))
    }

    @Test
    fun testCreateUser_UserEmailNotInUse_IsSuccessful() {
        val testEmail = "test@example.com"
        upon(userRepoMock.getUser(testEmail)).thenReturn(null)

        val signupModel = UserHelper.createSignupModel(email = testEmail)
        userService.createUser(signupModel)

        verify(userRepoMock).createUser(signupModel)
    }

    @Test(expected = InvalidModelException::class)
    fun testCreateUser_RepoThrowsException_ThrowsException() {
        val signupModel = UserHelper.createSignupModel()
        upon(userRepoMock.getUser(Mockito.anyString())).thenReturn(null)
        upon(userRepoMock.createUser(signupModel)).thenThrow(InvalidModelException::class.java)

        userService.createUser(signupModel)
    }

    @Test
    fun testGetUser_GetCurrentUser_IsSuccessful() {
        val id = 1
        val user = UserHelper.createTestUser(id = id)

        val returnedUser = userService.getUser(user, id)

        assertEquals(user, returnedUser)
        verify(userRepoMock, never()).getUser(ArgumentMatchers.anyInt())
    }

    @Test
    fun testGetUser_AsAdmin_IsSuccessful() {
        val thisUserId = 1
        val gettingUserId = 2
        val user = UserHelper.createTestUser(id = thisUserId, authRole = AuthRole.Admin)
        val userToReturn = UserHelper.createTestUser(id = gettingUserId)

        upon(userRepoMock.getUser(gettingUserId)).thenReturn(userToReturn)

        val returnedUser = userService.getUser(user, gettingUserId)

        assertEquals(userToReturn, returnedUser)
        verify(userRepoMock, times(1)).getUser(gettingUserId)
    }

    @Test(expected = UserForbiddenException::class)
    fun testGetUser_OtherUser_AsNonAdmin_ThrowsException() {
        val thisUserId = 1
        val gettingUserId = 2
        val user = UserHelper.createTestUser(id = thisUserId)
        val userToReturn = UserHelper.createTestUser(id = gettingUserId)

        upon(userRepoMock.getUser(gettingUserId)).thenReturn(userToReturn)

        userService.getUser(user, gettingUserId)
    }
}