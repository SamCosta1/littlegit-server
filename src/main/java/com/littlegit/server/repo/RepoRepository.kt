package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoId
import com.littlegit.server.model.repo.RepoSummary
import com.littlegit.server.model.user.User
import com.littlegit.server.model.user.UserId
import com.littlegit.server.util.inject
import com.littlegit.server.util.stripWhiteSpace
import littlegitcore.RepoCreationResult
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

object RepoCacheKeys {
    const val REPO_BY_ID = "Repo(Id:{0})"
    const val REPOS_BY_USER = "Repos(UserId:{0})"
}

@Singleton
class RepoRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache) {

    fun createRepo(createModel: CreateRepoModel, user: User, initResponse: RepoCreationResult, serverId: Int): RepoId {

        val createValidationResult = createModel.validate()
        if (createValidationResult.isNotValid) {
            throw InvalidModelException(createValidationResult)
        }

        val initResponseValidationResult = initResponse.validate()
        if (initResponseValidationResult.isNotValid) {
            throw InvalidModelException(initResponseValidationResult)
        }

        if (initResponseValidationResult.isNotValid) {
            throw InvalidModelException(createValidationResult)
        }

        return dbCon.executeInsert("""
            INSERT INTO Repos (
                repoName,
                createdDate,
                creatorId,
                description,
                serverId,
                cloneUrlPath,
                filePath
            )
            VALUES (:repoName, :createdDate, :creatorId, :description, :serverId, :cloneUrlPath, :filePath);
        """, params = mapOf(
            "repoName" to createModel.repoName.stripWhiteSpace(),
            "createdDate" to OffsetDateTime.now(),
            "creatorId" to user.id,
            "description" to createModel.description,
            "serverId" to serverId,
            "cloneUrlPath" to initResponse.cloneUrl,
            "filePath" to initResponse.filePath
        ))
    }

    fun getRepo(id: RepoId): Repo? {
        return cache.retrieve(RepoCacheKeys.REPO_BY_ID.inject(id), Repo::class.java) {
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

    fun invalidateCache(id: RepoId) = cache.delete(RepoCacheKeys.REPO_BY_ID.inject(id))
    fun invalidateCache(user: User) = cache.delete(RepoCacheKeys.REPOS_BY_USER.inject(user.id))

    fun getRepoByNameAndCreator(user: User, repoName: String): Repo? {
        return dbCon.executeSelect("""
            SELECT * FROM Repos
            WHERE creatorId=:creatorId
            AND   repoName=:repoName
        """, Repo::class.java, params = mapOf("repoName" to repoName.stripWhiteSpace(), "creatorId" to user.id))?.firstOrNull()
    }

    fun getAllReposForUser(user: User): List<RepoSummary>? {
        return cache.retrieveList(RepoCacheKeys.REPOS_BY_USER.inject(user.id), RepoSummary::class.java) {
            val sql = """
                SELECT 			Repos.id, Repos.repoName, Repos.createdDate, Repos.description, Repos.cloneUrlPath
                FROM            Repos
                INNER JOIN	    RepoAccess ON RepoAccess.repoId = Repos.id
                WHERE 			RepoAccess.active = true
                AND				RepoAccess.userId = :userId;
            """

             dbCon.executeSelect(sql, RepoSummary::class.java, params = mapOf("userId" to user.id))
        }
    }
}