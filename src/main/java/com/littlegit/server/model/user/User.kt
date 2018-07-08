package com.littlegit.server.model.user

typealias UserId = Int
class FullUser(id: UserId,
               email: String,
               firstName: String,
               surname: String,
               val passwordHash: String,
               val passwordSalt: String,
               role: AuthRole,
               languageCode: String): User(id, email, firstName, surname, role, languageCode) {

    fun toUser(): User {
        return super.clone()
    }
}

open class User(val id: UserId,
                val email: String,
                val firstName: String,
                val surname: String,
                val role: AuthRole,
                val languageCode: String) {

    fun clone(): User {
        return User(id, email, firstName, surname, role, languageCode)
    }
}

class CreateUserModel(val email: String,
                      val firstName: String,
                      val surname: String,
                      val passwordHash: String,
                      val passwordSalt: String,
                      val role: AuthRole,
                      val languageCode: String) {

    companion object {
        fun fromSignupModel(signupModel: SignupModel, salt: String, passwordHash: String): CreateUserModel {
            return CreateUserModel(
                    signupModel.email,
                    signupModel.firstName,
                    signupModel.surname,
                    passwordHash,
                    salt,
                    AuthRole.BasicUser,
                    signupModel.languageCode
            )
        }
    }
}