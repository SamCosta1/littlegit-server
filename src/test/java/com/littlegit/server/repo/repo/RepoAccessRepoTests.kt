package com.littlegit.server.repo.repo

import com.littlegit.server.model.repo.RepoAccess
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.repo.RepoAccessCacheKeys
import com.littlegit.server.repo.testUtils.*
import com.littlegit.server.util.inject
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class RepoAccessRepoTests {

    @Test
    fun testGrantRepoAccess_WhenDoesNotExist_IsSuccessful() {
        val user = UserHelper.createTestUser()
        val repoName = "RepoAccess_Test"

        val cleaner = {
            CleanupHelper.cleanupRepoAccess(user.id, repoName)
            CleanupHelper.cleanupRepo(repoName)
        }

        cleaner()

        try {
            val repo = RepoHelper.insertTestRepo(repoName = repoName, user = user)
            val repoAccessLevel = RepoAccessLevel.Contributor
            val cacheKey = RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(user.id, repo.id)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo, repoAccessLevel)

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
        val repoName = "RepoAccess_Test"

        val cleaner = {
            CleanupHelper.cleanupRepoAccess(user.id, repoName)
            CleanupHelper.cleanupRepo(repoName)
        }

        cleaner()

        try {
            val repo = RepoHelper.insertTestRepo(repoName = repoName, user = user)
            val repoAccessLevel = RepoAccessLevel.Contributor
            val cacheKey = RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(user.id, repo.id)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo, repoAccessLevel)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, repoAccessLevel, true, createdRepoAccess)

            // Now update it
            val updatedRepoAccessLevel = RepoAccessLevel.Owner
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo, updatedRepoAccessLevel)

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
        val repoName = "RepoAccess_Test"

        val cleaner = {
            CleanupHelper.cleanupRepoAccess(user.id, repoName)
            CleanupHelper.cleanupRepo(repoName)
        }

        cleaner()

        try {
            val repo = RepoHelper.insertTestRepo(repoName = repoName, user = user)
            val repoAccessLevel = RepoAccessLevel.Contributor
            val cacheKey = RepoAccessCacheKeys.REPO_ACCESS_CACHE_KEY.inject(user.id, repo.id)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo, repoAccessLevel)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, repoAccessLevel, true, createdRepoAccess)

            // Now update it
            RepositoryHelper.repoAccessRepository.revokeRepoAccess(user, repo)

            // Check updated - not clearing the cache to check it was invalidated
            RepositoryHelper.cache.delete(cacheKey)
            val updatedRepoAccess = RepositoryHelper.repoAccessRepository.getRepoAccessStatus(user, repo.id)

            // Check all the values are as expected
            assertRepo(repo, user, repoAccessLevel, false, updatedRepoAccess)

            // Check exists in cache
            val repoAccessFromCache = RepositoryHelper.cache.get(cacheKey, RepoAccess::class.java)
            assertRepo(repo, user, repoAccessLevel, false, repoAccessFromCache)

        } finally {
            cleaner()
        }
    }

    @Test
    fun testUserHasRepoOnServer_UserHasNoRepos_IsSuccessful() {
        val userSignup = UserHelper.createSignupModel()
        val createServerModel = GitServerHelper.createGitServerModel()

        val cleaner = {
            CleanupHelper.cleanupUser(userSignup.email)
            CleanupHelper.cleanupServer(createServerModel.ip)
        }
        cleaner()

        try {
            val userId = RepositoryHelper.userRepository.createUser(userSignup)!!
            val serverId = RepositoryHelper.gitServerRepository.createGitServer(createServerModel)

            val server = RepositoryHelper.gitServerRepository.getGitServer(serverId)!!
            val user = RepositoryHelper.userRepository.getUser(userId)!!

            val userHasRepoOnServer = RepositoryHelper.repoAccessRepository.userHasRepoOnServer(server, user)
            assertNotNull(userHasRepoOnServer); userHasRepoOnServer!!
            assertFalse(userHasRepoOnServer)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testUserHasRepoOnServer_UserHasRepo_IsSuccessful() {
        val userSignup = UserHelper.createSignupModel()
        val createServerModel = GitServerHelper.createGitServerModel()
        val repoName = "GandalfsRepo"

        val cleaner = {
            CleanupHelper.cleanupRepo(repoName)
            CleanupHelper.cleanupUser(userSignup.email)
            CleanupHelper.cleanupServer(createServerModel.ip)
        }
        cleaner()

        try {
            val userId = RepositoryHelper.userRepository.createUser(userSignup)!!
            val serverId = RepositoryHelper.gitServerRepository.createGitServer(createServerModel)

            val server = RepositoryHelper.gitServerRepository.getGitServer(serverId)!!
            val user = RepositoryHelper.userRepository.getUser(userId)!!
            val repo = RepoHelper.insertTestRepo(repoName = repoName, user = user, serverId = serverId)

            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo, RepoAccessLevel.Contributor)

            val userHasRepoOnServer = RepositoryHelper.repoAccessRepository.userHasRepoOnServer(server, user)
            assertNotNull(userHasRepoOnServer); userHasRepoOnServer!!
            assertTrue(userHasRepoOnServer)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testUserHasRepoOnServer_UserHasRepo_AccessRevoked_IsSuccessful() {
        val userSignup = UserHelper.createSignupModel()
        val createServerModel = GitServerHelper.createGitServerModel()
        val repoName = "GandalfsRepo"

        val cleaner = {
            CleanupHelper.cleanupRepo(repoName)
            CleanupHelper.cleanupUser(userSignup.email)
            CleanupHelper.cleanupServer(createServerModel.ip)
        }
        cleaner()

        try {
            val userId = RepositoryHelper.userRepository.createUser(userSignup)!!
            val serverId = RepositoryHelper.gitServerRepository.createGitServer(createServerModel)

            val server = RepositoryHelper.gitServerRepository.getGitServer(serverId)!!
            val user = RepositoryHelper.userRepository.getUser(userId)!!
            val repo = RepoHelper.insertTestRepo(repoName = repoName, user = user)

            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo, RepoAccessLevel.Contributor)
            RepositoryHelper.repoAccessRepository.revokeRepoAccess(user, repo)

            val userHasRepoOnServer = RepositoryHelper.repoAccessRepository.userHasRepoOnServer(server, user)
            assertNotNull(userHasRepoOnServer); userHasRepoOnServer!!
            assertFalse(userHasRepoOnServer)
        } finally {
            cleaner()
        }
    }


}