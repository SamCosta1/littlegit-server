package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.CreateGitServerModel
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.GitServerId
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.user.User
import com.littlegit.server.model.user.UserId
import com.littlegit.server.util.inject
import javax.inject.Inject

object GitServerCacheKeys {
    const val SERVER_CACHE_BY_ID = "GitServer(Id: {0})"
}

class GitServerRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                               private val cache: Cache) {

    fun createGitServer(createModel: CreateGitServerModel): GitServerId {

        val validationResult = createModel.validate()
        if (validationResult.isNotValid) {
            throw InvalidModelException(validationResult)
        }

        return dbCon.executeInsert("""
            INSERT INTO GitServers (
                ip,
                region,
                capacity
            )
            VALUES (:ip, :region, :capacity);
        """, model = createModel)
    }

    fun getGitServer(id: GitServerId): GitServer? {
        return cache.retrieve(GitServerCacheKeys.SERVER_CACHE_BY_ID.inject(id), GitServer::class.java) {
            val sql = """
                SELECT * FROM GitServers
                WHERE id = :id
            """
            val params = mapOf("id" to id)

            val gitServers = dbCon.executeSelect(sql, GitServer::class.java, params = params)

            if (gitServers != null && gitServers.size > 1) {
                throw IllegalStateException("Multiple repos have the same id, something's very broken")
            }

            gitServers?.firstOrNull()
        }
    }


    /**
     * Returns a list of all the servers with repos that the user has access to
     */
    fun getUserServers(userId: UserId): List<GitServer>? {
        return dbCon.executeSelect("""
            SELECT DISTINCT GitServers.* FROM GitServers
            INNER JOIN Repos ON Repos.serverId = GitServers.id
            INNER JOIN RepoAccess On RepoAccess.repoId = Repos.id
            WHERE RepoAccess.userId = :userId
            AND RepoAccess.active = true
        """, GitServer::class.java, params = mapOf("userId" to userId))
    }

    // TODO: Make this take into account the number of repos on each server, the user's region etc etc etc
    // For now since there's only one server, just return it
    fun getBestGitServerForUser(user: User): GitServer? {
        return getGitServer(1)
    }

    fun invalidateCache(id: GitServerId) = cache.delete(GitServerCacheKeys.SERVER_CACHE_BY_ID.inject(id))
}