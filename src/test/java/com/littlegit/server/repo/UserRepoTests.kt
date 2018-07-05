package com.littlegit.server.repo

import com.littlegit.server.model.AuthRole
import com.littlegit.server.model.SignupModel
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.util.HashingUtils
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserRepoTests {

    @Test
    fun testCreateValidUser() {
        val email = "test.email@example.com"

        val cleaner = {
            CleanupHelper.cleanupUser(email)
        }

        cleaner()
        val signupModel = SignupModel(email,
                            "password",
                            "TestCreateValidUser_FirstName",
                             "TestCreateValidUser_Surname",
                                      "en-GB")

        try {
            val id = RepositoryHelper.userRepository.createUser(signupModel)

            assertNotNull(id)
            val createdUser = RepositoryHelper.userRepository.getFullUsers(id!!)

            // Check all the values are as expected
            assertEquals(email, createdUser?.email)
            assertEquals(signupModel.firstName, createdUser?.firstName)
            assertEquals(signupModel.surname, createdUser?.surname)
            assertEquals(signupModel.languageCode, createdUser?.languageCode)
            assertEquals(AuthRole.BasicUser, createdUser?.role)
            assertNotNull(createdUser?.passwordHash)
            assertNotNull(createdUser?.passwordSalt)

            // Check the hash was correct
            assertEquals(HashingUtils.hash(signupModel.password, createdUser!!.passwordSalt), createdUser.passwordHash)
        } finally {
            cleaner()
        }
    }

}