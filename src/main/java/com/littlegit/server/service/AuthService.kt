package com.littlegit.server.service

import com.littlegit.server.application.exception.InvalidTokenException
import com.littlegit.server.application.exception.NotFoundException
import com.littlegit.server.application.exception.UserUnauthorizedException
import com.littlegit.server.authfilter.AuthConstants
import com.littlegit.server.model.user.FullUser
import com.littlegit.server.model.user.LoginModel
import com.littlegit.server.model.user.LoginResponseModel
import com.littlegit.server.model.user.User
import com.littlegit.server.repo.AuthRepository
import com.littlegit.server.repo.UserRepository
import com.littlegit.server.util.HashingUtils
import javax.inject.Inject

class AuthService @Inject constructor (private val authRepository: AuthRepository,
                                       private val userRepository: UserRepository) {

    fun getUserForToken(rawToken: String): User {
        val token = authRepository.getFullToken(rawToken) ?: throw InvalidTokenException()

        return userRepository.getUser(token.userId) ?: throw NotFoundException(User::class)
    }

    fun login(loginDetails: LoginModel): LoginResponseModel {
        val fullUser = userRepository.getFullUser(loginDetails.email) ?: throw NotFoundException(FullUser::class)

        val suppliedPasswordHash = HashingUtils.hash(loginDetails.password, fullUser.passwordSalt)

        if (suppliedPasswordHash != fullUser.passwordHash) {
            throw UserUnauthorizedException()
        }

        // If we got here, details are correct yey!
        val accessToken = authRepository.createAndSaveAccessToken(fullUser.id).token
        val refreshToken = authRepository.createAndSaveRefreshToken(fullUser.id).token

        // This is important, don't want to send back the hash and salt
        val user = fullUser.toUser()

        return LoginResponseModel(accessToken, refreshToken, AuthConstants.AuthScheme, user)
    }
}
