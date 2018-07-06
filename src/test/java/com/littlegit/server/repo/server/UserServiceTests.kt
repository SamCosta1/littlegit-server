package com.littlegit.server.repo.server

import com.littlegit.server.application.exception.EmailInUseException
import com.littlegit.server.repo.UserRepository
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.upon
import com.littlegit.server.service.UserService
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class UserServiceTests {

    private lateinit var userService: UserService
    private lateinit var userRepoMock: UserRepository

    @Before
    fun setup() {
        userRepoMock = mock(UserRepository::class.java)
        userService = UserService(userRepoMock)
    }

    @Test(expected = EmailInUseException::class)
    fun testUserEmailInUse_ThrowsException() {
        val testEmail = "test@example.com"

        upon(userRepoMock.getUser(testEmail)).thenReturn(UserHelper.createTestUser(email = testEmail))

        userService.createUser(UserHelper.createSignupModel(email = testEmail))
    }
}