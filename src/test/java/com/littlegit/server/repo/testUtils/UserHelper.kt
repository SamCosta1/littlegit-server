package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.model.user.FullUser
import com.littlegit.server.model.user.SignupModel
import com.littlegit.server.model.user.User
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

object UserHelper {
    fun createTestUser(id: Int = 1,
                       email: String = "test@test.com",
                       firstName: String = "Froddo",
                       surname: String = "Baggins",
                       authRole: AuthRole = AuthRole.BasicUser,
                       languageCode: String = "en-GB",
                       username: String = "1RingRules"

    ) = User(id, email, firstName, surname, authRole, languageCode, username)

    fun createSignupModel(email: String = "test@test.com",
                          password: String = "password",
                          firstName: String = "Froddo",
                          surname: String = "Baggins",
                          languageCode: String = "en-GB",
                          username: String = "1RingRules"

    ) = SignupModel(email, password, firstName, surname, languageCode, username)

    fun assertSignupModel(expected: SignupModel, actualFullUser: FullUser?) {
        assertSignupModel(expected, actualUser = actualFullUser)
        assertNotNull(actualFullUser?.passwordHash)
        assertNotNull(actualFullUser?.passwordSalt)
    }

    fun assertSignupModel(expected: SignupModel, actualUser: User?) {
        assertEquals(expected.email, actualUser?.email)
        assertEquals(expected.firstName, actualUser?.firstName)
        assertEquals(expected.surname, actualUser?.surname)
        assertEquals(expected.languageCode, actualUser?.languageCode)
        assertEquals(AuthRole.BasicUser, actualUser?.role)
        assertEquals(expected.username, actualUser?.username)
    }
}