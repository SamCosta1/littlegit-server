package com.littlegit.server.service

import com.littlegit.server.application.exception.EmailInUseException
import com.littlegit.server.application.exception.NotFoundException
import com.littlegit.server.application.exception.UserForbiddenException
import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.model.user.SignupModel
import com.littlegit.server.model.user.User
import com.littlegit.server.repo.UserRepository
import java.security.Principal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor (private val userRepository: UserRepository) {

    fun getUser(currentUser: User, userId: Int): User {
        if (userId < 0) {
            throw IllegalArgumentException(userId.toString())
        }

        if (currentUser.id == userId) {
            return currentUser
        }

        if (currentUser.role == AuthRole.Admin) {
            return userRepository.getUser(userId) ?: throw NotFoundException(User::class)
        } else {
            throw UserForbiddenException()
        }
    }

    fun createUser(signupModel: SignupModel) {

        val existingUser = userRepository.getUser(signupModel.email)

        if (existingUser != null ) {
            throw EmailInUseException(signupModel.email)
        }

        userRepository.createUser(signupModel)
    }

}