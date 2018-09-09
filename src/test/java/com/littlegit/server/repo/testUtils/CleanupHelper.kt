package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.user.FullUser
import com.littlegit.server.model.user.UserId
import java.net.InetAddress

object CleanupHelper {

    fun cleanupUser(email: String) {

        val users = RepositoryHelper.dbConnector.executeSelect("""
            SELECT * FROM Users
            WHERE email=:email
        """.trimIndent(), FullUser::class.java, params = mapOf("email" to email))

        users?.forEach {
            cleanupAuthTokensForUserId(it.id)
            RepositoryHelper.userRepository.invalidateCache(it)
        }

        RepositoryHelper.userRepository.invalidateCache(email)
        RepositoryHelper.dbConnector.executeDelete("""
            DELETE FROM Users WHERE email=:email
        """, mapOf("email" to email))
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

    fun cleanupRepos(vararg repos: String) = repos.forEach { cleanupRepo(it) }
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
                DELETE FROM RepoAccess WHERE repoId = :repoId
            """, mapOf("repoId" to it.toInt()))

            RepositoryHelper.repoAccessRepository.invalidateCache(userId, it.toInt())
        }
    }

    fun cleanupRepoAccess(email: String, repoName: String) {
        val ids = getIds(mapOf("name" to repoName), "Repos", "repoName=:name")
        val user = RepositoryHelper.userRepository.getUser(email)

        ids.forEach {
            RepositoryHelper.dbConnector.executeDelete("""
                DELETE FROM RepoAccess WHERE AND repoId = :repoId
            """, mapOf("repoId" to it.toInt()))

            RepositoryHelper.repoAccessRepository.invalidateCache(user?.id ?: 0, it.toInt())
        }
    }

    fun cleanupServers(vararg ips: InetAddress) = ips.forEach { cleanupServer(it) }
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

    fun cleanupSshKey(publicKey: String) {
        RepositoryHelper.dbConnector.executeDelete("""
            DELETE FROM SshKeys WHERE publicKey=:key
        """, mapOf("key" to publicKey))
    }
}