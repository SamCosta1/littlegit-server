package com.littlegit.server.service

import com.littlegit.server.model.User
import com.littlegit.server.repo.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor (private val userRepository: UserRepository) {
    fun getUser(userId: Int): User? {
        return userRepository.getUser(userId)
    }

}