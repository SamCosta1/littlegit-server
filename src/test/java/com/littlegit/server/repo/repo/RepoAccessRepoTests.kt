package com.littlegit.server.repo.repo

import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoAccess
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.user.User
import com.littlegit.server.repo.RepoAccessCacheKeys
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepoHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.util.inject
import org.junit.Test
import kotlin.test.assertEquals

class RepoAccessRepoTests {

    @Test
    fun testGrantRepoAccess_WhenDoesNotExist_IsSuccessful() {
        val user = UserHelper.createTestUser()
        val repo = RepoHelper.insertTestRepo(user = user)

        val cleaner = {
            CleanupHelper.cleanupRepoAccess(user.id, repo.repoName)
            CleanupHelper.cleanupRepo(repo.repoName)
        }

        cleaner()

        try {
            val repoAccessLevel = RepoAccessLevel.Contributor
            val cacheKey = RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(user.id, repo.id)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo.id, repoAccessLevel)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, repoAccessLevel, true, createdRepoAccess)

            // Check exists in cache
            val repoAccessFromCache = RepositoryHelper.cache.get(cacheKey, RepoAccess::class.java)
            assertRepo(repo, user, repoAccessLevel, true, repoAccessFromCache)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGrantRepoAccess_WhenDoesExist_IsSuccessful() {
        val user = UserHelper.createTestUser()
        val repo = RepoHelper.insertTestRepo(user = user)

        val cleaner = {
            CleanupHelper.cleanupRepoAccess(user.id, repo.repoName)
            CleanupHelper.cleanupRepo(repo.repoName)
        }

        cleaner()

        try {
            val repoAccessLevel = RepoAccessLevel.Contributor
            val cacheKey = RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(user.id, repo.id)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo.id, repoAccessLevel)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, repoAccessLevel, true, createdRepoAccess)

            // Now update it
            val updatedRepoAccessLevel = RepoAccessLevel.Owner
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo.id, updatedRepoAccessLevel)

            // Check updated - not clearing the cache to check it was invalidated
            RepositoryHelper.cache.delete(cacheKey)
            val updatedRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, updatedRepoAccessLevel, true, updatedRepoAccess)

            // Check exists in cache
            val repoAccessFromCache = RepositoryHelper.cache.get(cacheKey, RepoAccess::class.java)
            assertRepo(repo, user, updatedRepoAccessLevel, true, repoAccessFromCache)

        } finally {
            cleaner()
        }
    }

    @Test
    fun testRevokeRepoAccess_WhenDoesExist_IsSuccessful() {
        val user = UserHelper.createTestUser()
        val repo = RepoHelper.insertTestRepo(user = user)

        val cleaner = {
            CleanupHelper.cleanupRepoAccess(user.id, repo.repoName)
            CleanupHelper.cleanupRepo(repo.repoName)
        }

        cleaner()

        try {
            val repoAccessLevel = RepoAccessLevel.Contributor
            val cacheKey = RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(user.id, repo.id)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo.id, repoAccessLevel)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, repoAccessLevel, true, createdRepoAccess)

            // Now update it
            val updatedRepoAccessLevel = RepoAccessLevel.Owner
            RepositoryHelper.repoAccessRepository.revokeRepoAccess(user, repo.id, updatedRepoAccessLevel)

            // Check updated - not clearing the cache to check it was invalidated
            RepositoryHelper.cache.delete(cacheKey)
            val updatedRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, updatedRepoAccessLevel, false, updatedRepoAccess)

            // Check exists in cache
            val repoAccessFromCache = RepositoryHelper.cache.get(cacheKey, RepoAccess::class.java)
            assertRepo(repo, user, updatedRepoAccessLevel, false, repoAccessFromCache)

        } finally {
            cleaner()
        }
    }

    private fun assertRepo(repo: Repo, user: User, repoAccessLevel: RepoAccessLevel, active: Boolean, repoAccess: RepoAccess?) {
        assertEquals(repo.id, repoAccess?.repoId)
        assertEquals(user.id, repoAccess?.userId)
        assertEquals(repoAccessLevel, repoAccess?.level)
        assertEquals(active, repoAccess?.active)
    }

}