package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.user.UserId
import java.net.InetAddress

object CleanupHelper {

    fun cleanupUser(email: String) {

        val ids = getIds(mapOf("email" to email), "Users", "email=:email")

        RepositoryHelper.dbConnector.executeDelete("""
            DELETE FROM Users WHERE email=:email
        """, mapOf("email" to email))

        ids.forEach {
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
        val ids = getIds(mapOf("name" to repoName), "Repos", "repoName=:name")

        RepositoryHelper.dbConnector.executeDelete("""
            DELETE FROM Repos WHERE repoName=:repoName
        """, mapOf("repoName" to repoName))

        ids.forEach {
            RepositoryHelper.repoRepository.invalidateCache(it.toInt())
        }
    }

    fun cleanupRepoAccess(userId: UserId, repoName: String) {
        val ids = getIds(mapOf("name" to repoName), "Repos", "repoName=:name")

        ids.forEach {
            RepositoryHelper.dbConnector.executeDelete("""
                DELETE FROM RepoAccess WHERE userId=:userId
                AND repoId = :repoId
            """, mapOf("userId" to userId, "repoId" to it.toInt()))

            RepositoryHelper.repoAccessRepository.invalidateCache(userId, it.toInt())
        }
    }

    fun cleanupServer(ip: InetAddress) {
        val ids = getIds(mapOf("ip" to ip), "GitServers", "ip=:ip")

        RepositoryHelper.dbConnector.executeDelete("""
            DELETE FROM GitServers WHERE ip=:ip
        """, mapOf("ip" to ip))

        ids.forEach {
            RepositoryHelper.gitServerRepository.invalidateCache(it.toInt())
        }
    }


    // Helpers

    private fun getIds(params: Map<String, Any>, table: String, whereClause: String): List<Integer> {
        return RepositoryHelper.dbConnector.executeScalar("""
            SELECT id FROM $table WHERE $whereClause
        """, Integer::class.java, params = params) ?: emptyList()
    }
}