package org.littlegit.server.service

import org.littlegit.server.repo.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor (private val userRepository: UserRepository) {

}