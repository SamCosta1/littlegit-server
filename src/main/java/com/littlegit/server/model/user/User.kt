package com.littlegit.server.model.user

import java.security.Principal

typealias UserId = Int
class FullUser(id: UserId,
               email: String,
               firstName: String,
               surname: String,
               val passwordHash: String,
               val passwordSalt: String,
               role: AuthRole,
               languageCode: String,
               username: String): User(id, email, firstName, surname, role, languageCode, username) {

    fun toUser(): User {
        return super.clone()
    }
}

open class User(val id: UserId,
                val email: String,
                val firstName: String,
                val surname: String,
                val role: AuthRole,
                val languageCode: String,
                val username: String): Principal {

    override fun getName(): String = "$firstName $surname"

    fun clone(): User {
        return User(id, email, firstName, surname, role, languageCode, username)
    }

    fun hasAnyRoleOf(allowedRoles: List<AuthRole>): Boolean {
        return allowedRoles.contains(role)
    }
}

class CreateUserModel(val email: String,
                      val firstName: String,
                      val surname: String,
                      val passwordHash: String,
                      val passwordSalt: String,
                      val role: AuthRole,
                      val languageCode: String,
                      val username: String) {

    companion object {
        fun fromSignupModel(signupModel: SignupModel, salt: String, passwordHash: String): CreateUserModel {
            return CreateUserModel(
                    signupModel.email,
                    signupModel.firstName,
                    signupModel.surname,
                    passwordHash,
                    salt,
                    AuthRole.BasicUser,
                    signupModel.languageCode,
                    signupModel.username
            )
        }
    }
}