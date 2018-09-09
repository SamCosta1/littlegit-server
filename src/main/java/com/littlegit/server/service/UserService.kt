package com.littlegit.server.service

import com.littlegit.server.application.exception.EmailInUseException
import com.littlegit.server.application.exception.NotFoundException
import com.littlegit.server.application.exception.UserForbiddenException
import com.littlegit.server.application.exception.UsernameInUseException
import com.littlegit.server.application.remoterunner.RemoteCommandRunner
import com.littlegit.server.model.user.*
import com.littlegit.server.repo.GitServerRepository
import com.littlegit.server.repo.SshKeyRepository
import com.littlegit.server.repo.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor (private val userRepository: UserRepository,
                                       private val gitServerRepository: GitServerRepository,
                                       private val remoteCommandRunner: RemoteCommandRunner,
                                       private val sshKeyRepository: SshKeyRepository) {

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

        val existingUserByEmail = userRepository.getUser(signupModel.email)

        if (existingUserByEmail != null ) {
            throw EmailInUseException(signupModel.email)
        }

        val existingUserByUsername = userRepository.getUserByUsername(username = signupModel.username)

        if (existingUserByUsername != null) {
            throw UsernameInUseException(signupModel.username)
        }

        userRepository.createUser(signupModel)
    }

    fun addSshKeyToUser(currentUser: User, createSshKeyModel: CreateSshKeyModel) {
        if (createSshKeyModel.userId < 0) {
            throw IllegalArgumentException(createSshKeyModel.userId.toString())
        }

        if (currentUser.id == createSshKeyModel.userId || currentUser.hasAnyRoleOf(AuthRole.Admin)) {
            val id = sshKeyRepository.createSshKey(createSshKeyModel)

            if (id == null || id < 0) {
                throw UnknownError()
            }

            // All the servers that contain repos the user has access to
            val servers = gitServerRepository.getUserServers(createSshKeyModel.userId)

            servers?.forEach { server ->
                remoteCommandRunner.addSshKey(createSshKeyModel, server)
            }
        } else {
            throw UserForbiddenException()
        }
    }

}