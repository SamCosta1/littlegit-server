package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.user.UserId

object CleanupHelper {

    fun cleanupUser(email: String) {

        val ids = RepositoryHelper.dbConnector.executeScalar("""
            SELECT id FROM Users WHERE email=:email
        """, Integer::class.java, params = mapOf("email" to email))

        RepositoryHelper.dbConnector.executeDelete("""

            DELETE FROM Users WHERE email=:email

        """, mapOf("email" to email))

        ids?.forEach {
            cleanupAuthTokensForUserId(it.toInt())
            RepositoryHelper.userRepository.invalidateCache(it.toInt())
        }
        RepositoryHelper.userRepository.invalidateCache(email)
    }

    fun cleanupAuthTokensForUserId(userId: UserId) {
        RepositoryHelper.dbConnector.executeDelete("""

            DELETE FROM UserTokens WHERE userId=:userId

        """, mapOf("userId" to userId))

        RepositoryHelper.authRepository.invlaidateCache(userId)
    }
}