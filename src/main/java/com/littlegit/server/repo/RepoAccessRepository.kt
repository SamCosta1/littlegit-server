package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.repo.RepoId
import com.littlegit.server.model.repo.RepoAccess
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.user.User
import com.littlegit.server.model.user.UserId
import com.littlegit.server.util.inject
import javax.inject.Inject

object RepoAccessCacheKeys {
    const val REPO_ACCESS_CACHE_KEY = "RepoAccess(UserId:{0}, RepoId: {1})"
}
class RepoAccessRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                                private val cache: Cache) {

    fun grantRepoAccess(user: User, repoId: RepoId, level: RepoAccessLevel) = upsertRepoAccess(user, repoId, level, hasAccess = true)
    fun revokeRepoAccess(user: User, repoId: RepoId, level: RepoAccessLevel) = upsertRepoAccess(user, repoId, level, hasAccess = false)

    private fun upsertRepoAccess(user: User, repoId: RepoId, level: RepoAccessLevel, hasAccess: Boolean) {

        dbCon.executeInsert("""
            INSERT INTO RepoAccess
                (repoId, userId, active, level)
            VALUES
                (:repoId, :userId, :active, :level)
            ON DUPLICATE KEY UPDATE
                active = :active,
                level = :level
        """, params = mapOf(
           "repoId" to repoId,
           "userId" to user.id,
           "level" to level,
           "active" to hasAccess
        ))

        invalidateCache(user.id, repoId)
    }

    fun getRepoAccessStatus(user: User, repoId: RepoId): RepoAccess? {
        return cache.retrieve(RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(user.id, repoId), RepoAccess::class.java) {
            val sql = """
                SELECT * FROM RepoAccess
                WHERE userId = :userId
                AND   repoId = :repoId
            """
            val params = mapOf(
                    "repoId" to repoId,
                    "userId" to user.id
            )

            val repoAccesses = dbCon.executeSelect(sql, RepoAccess::class.java, params = params)

            if (repoAccesses != null && repoAccesses.size > 1) {
                throw IllegalStateException("Multiple primary keys, something's very broken")
            }

            repoAccesses?.firstOrNull()
        }
    }

    fun invalidateCache(userId: UserId, repoId: RepoId)
            = cache.delete(RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(userId, repoId))

}