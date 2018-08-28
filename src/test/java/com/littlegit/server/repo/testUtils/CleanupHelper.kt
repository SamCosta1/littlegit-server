package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.RepoId
import com.littlegit.server.model.user.User
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
        val tokens = RepositoryHelper.dbConnector.executeScalar("""
            SELECT token FROM UserTokens WHERE userId=:userId
        """, String::class.java, params = mapOf("userId" to userId))

        RepositoryHelper.dbConnector.executeDelete("""

            DELETE FROM UserTokens WHERE userId=:userId

        """, mapOf("userId" to userId))

        tokens?.forEach {
            RepositoryHelper.authRepository.invalidateCache(it)
        }
    }

    fun cleanupRepo(repoName: String) {
        val ids = RepositoryHelper.dbConnector.executeScalar("""
            SELECT id FROM Repos WHERE repoName=:name
        """, Integer::class.java, params = mapOf("name" to repoName))

        RepositoryHelper.dbConnector.executeDelete("""
            DELETE FROM Repos WHERE repoName=:repoName
        """, mapOf("repoName" to repoName))

        ids?.forEach {
            RepositoryHelper.repoRepository.invalidateCache(it.toInt())
        }
    }

    fun cleanupRepoAccess(userId: UserId, repoName: String) {
        val ids = RepositoryHelper.dbConnector.executeScalar("""
            SELECT id FROM Repos WHERE repoName=:name
        """, Integer::class.java, params = mapOf("name" to repoName))


        ids?.forEach {
            RepositoryHelper.dbConnector.executeDelete("""
                DELETE FROM RepoAccess WHERE userId=:userId
                AND repoId = :repoId
            """, mapOf("userId" to userId, "repoId" to it.toInt()))

            RepositoryHelper.repoAccessRepository.invalidateCache(userId, it.toInt())
        }
    }
}