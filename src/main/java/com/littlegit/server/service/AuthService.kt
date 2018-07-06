package com.littlegit.server.service

import com.littlegit.server.model.SignupModel
import com.littlegit.server.repo.AuthRepository
import com.littlegit.server.repo.UserRepository
import javax.inject.Inject

class AuthService @Inject constructor (private val authRepository: AuthRepository,
                                       private val userRepository: UserRepository) {


}
