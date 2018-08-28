package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.CreateRepoModel
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.Repo
import com.littlegit.server.model.RepoId
import com.littlegit.server.model.user.FullUser
import com.littlegit.server.model.user.User
import com.littlegit.server.util.inject
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

object RepoCacheKeys {
    const val REPO_CACHE_KEY_BY_ID = "Repo(Id:{0})"
}

@Singleton
class RepoRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache) {

    fun createRepo(createModel: CreateRepoModel, user: User, cloneUrlPath: String, serverId: Int): RepoId {

        val validationResult = createModel.validate()
        if (validationResult.isNotValid) {
            throw InvalidModelException(validationResult)
        }

        return dbCon.executeInsert("""
            INSERT INTO Repos (
                repoName,
                createdDate,
                creatorId,
                description,
                serverId,
                cloneUrlPath
            )
            VALUES (:repoName, :createdDate, :creatorId, :description, :serverId, :cloneUrlPath);
        """, params = mapOf(
            "repoName" to createModel.repoName,
            "createdDate" to OffsetDateTime.now(),
            "creatorId" to user.id,
            "description" to createModel.description,
            "serverId" to serverId,
            "cloneUrlPath" to cloneUrlPath
        ))
    }

    fun getRepo(id: RepoId): Repo? {
        return cache.retrieve(RepoCacheKeys.REPO_CACHE_KEY_BY_ID.inject(id), Repo::class.java) {
            val sql = """
                SELECT * FROM Repos
                WHERE id = :id
            """
            val params = mapOf("id" to id)

            val repos = dbCon.executeSelect(sql, Repo::class.java, params = params)

            if (repos != null && repos.size > 1) {
                throw IllegalStateException("Multiple repos have the same id, something's very broken")
            }

            repos?.firstOrNull()
        }
    }

    fun getRepoSummary(id: RepoId) = getRepo(id)?.toRepoSummary()

    fun invalidateCache(id: RepoId) = cache.delete(RepoCacheKeys.REPO_CACHE_KEY_BY_ID.inject(id))
}