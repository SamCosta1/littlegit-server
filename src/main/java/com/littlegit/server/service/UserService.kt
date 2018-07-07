package com.littlegit.server.service

import com.littlegit.server.application.exception.EmailInUseException
import com.littlegit.server.model.user.SignupModel
import com.littlegit.server.model.user.User
import com.littlegit.server.repo.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor (private val userRepository: UserRepository) {

    fun getUser(userId: Int): User? {
        if (userId < 0) {
            throw IllegalArgumentException(userId.toString())
        }

        return userRepository.getFullUser(userId)
    }

    fun createUser(signupModel: SignupModel) {

        val existingUser = userRepository.getUser(signupModel.email)

        if (existingUser != null ) {
            throw EmailInUseException(signupModel.email)
        }

        userRepository.createUser(signupModel)
    }

}