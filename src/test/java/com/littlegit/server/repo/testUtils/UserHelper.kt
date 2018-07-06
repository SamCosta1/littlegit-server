package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.AuthRole
import com.littlegit.server.model.SignupModel
import com.littlegit.server.model.User

object UserHelper {
    fun createTestUser(id: Int = 1,
                       email: String = "test@test.com",
                       firstName: String = "Froddo",
                       surname: String = "Baggins",
                       authRole: AuthRole = AuthRole.BasicUser,
                       languageCode: String = "en-GB"

    ) = User(id, email, firstName, surname, authRole, languageCode)

    fun createSignupModel(email: String = "test@test.com",
                          password: String = "password",
                          firstName: String = "Froddo",
                          surname: String = "Baggins",
                          languageCode: String = "en-GB"

    ) = SignupModel(email, password, firstName, surname, languageCode)
}