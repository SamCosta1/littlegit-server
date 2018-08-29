package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.CreateGitServerModel
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.GitServerId
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.repo.Repo
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

    fun invalidateCache(id: GitServerId) = cache.delete(GitServerCacheKeys.SERVER_CACHE_BY_ID.inject(id))
}