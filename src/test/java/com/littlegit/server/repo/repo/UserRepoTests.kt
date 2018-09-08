package com.littlegit.server.repo.repo

import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.model.user.FullUser
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.user.SignupModel
import com.littlegit.server.repo.UserRepository
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.UserHelper.assertSignupModel
import com.littlegit.server.util.HashingUtils
import com.littlegit.server.util.inject
import org.junit.Test
import java.text.MessageFormat
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserRepoTests {

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidUser_ThrowsException() {
        val signupModel: SignupModel = UserHelper.createSignupModel("invalidEmail")

        RepositoryHelper.userRepository.createUser(signupModel)
    }

    @Test
    fun testCreateValidUser_IsSuccessful() {
        val email = "test.email@example.com"

        val cleaner = {
            CleanupHelper.cleanupUser(email)
        }

        cleaner()
        val signupModel = SignupModel(email,
                "password",
                "TestCreateValidUser_FirstName",
                "TestCreateValidUser_Surname",
                "en-GB",
                "TestCVU_Username")

        try {
            val id = RepositoryHelper.userRepository.createUser(signupModel)

            assertNotNull(id)

            val cacheKey = UserRepository.USER_CACHE_KEY_BY_ID.inject(id!!)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdUser = RepositoryHelper.userRepository.getFullUser(id)

            // Check all the values are as expected
            assertSignupModel(signupModel, createdUser)

            // Check exists in cache
            val userFromCache = RepositoryHelper.cache.get(cacheKey, FullUser::class.java)
            assertSignupModel(signupModel, userFromCache)

            // Check the hash was correct
            assertEquals(HashingUtils.hash(signupModel.password, createdUser!!.passwordSalt), createdUser.passwordHash)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetUserById_IsSuccessful() {
        val email = "test.email@example.com"

        val cleaner = {
            CleanupHelper.cleanupUser(email)
        }

        cleaner()
        val signupModel = SignupModel(email,
                "password",
                "TestCreateValidUser_FirstName",
                "TestCreateValidUser_Surname",
                "en-GB",
                "TestCVU_Username")

        try {
            val id = RepositoryHelper.userRepository.createUser(signupModel)!!

            val cacheKey = MessageFormat.format(UserRepository.USER_CACHE_KEY_BY_ID, id)

            // Shouldn't be anything in cache
            val cached = RepositoryHelper.cache.get(cacheKey)
            assertNull(cached)

            // Get from db
            val user = RepositoryHelper.userRepository.getFullUser(id)
            assertSignupModel(signupModel, user)

            // Check the get also populated the cache
            val newCached = RepositoryHelper.cache.get(cacheKey, FullUser::class.java)
            assertSignupModel(signupModel, newCached)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetUserByEmail_IsSuccessful() {
        val email = "test.email@example.com"

        val cleaner = {
            CleanupHelper.cleanupUser(email)
        }

        cleaner()
        val signupModel = SignupModel(email,
                "password",
                "TestCreateValidUser_FirstName",
                "TestCreateValidUser_Surname",
                "en-GB",
                "TestCVU_Username")

        try {
            val id = RepositoryHelper.userRepository.createUser(signupModel)!!

            val cacheKey = MessageFormat.format(UserRepository.USER_CACHE_BY_EMAIL, email)

            // Shouldn't be anything in cache
            val cached = RepositoryHelper.cache.get(cacheKey)
            assertNull(cached)

            // Get from db
            val user = RepositoryHelper.userRepository.getFullUser(email)
            assertSignupModel(signupModel, user)

            // Check the get also populated the cache
            val newCached = RepositoryHelper.cache.get(cacheKey, FullUser::class.java)
            assertSignupModel(signupModel, newCached)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetUserByUsername_IsSuccessful() {
        val username = "username_test_1"
        val email = "get_user_by_username@gmail.com"
        val cleaner = {
            CleanupHelper.cleanupUser(email)
        }

        cleaner()
        val signupModel = UserHelper.createSignupModel(username = username, email = email)

        try {
            val id = RepositoryHelper.userRepository.createUser(signupModel)!!

            val cacheKey = MessageFormat.format(UserRepository.USER_CACHE_BY_USERNAME, username)

            // Shouldn't be anything in cache
            val cached = RepositoryHelper.cache.get(cacheKey)
            assertNull(cached)

            // Get from db
            val user = RepositoryHelper.userRepository.getFullUserByUsername(username)
            assertSignupModel(signupModel, user)

            // Check the get also populated the cache
            val newCached = RepositoryHelper.cache.get(cacheKey, FullUser::class.java)
            assertSignupModel(signupModel, newCached)
        } finally {
            cleaner()
        }
    }
}