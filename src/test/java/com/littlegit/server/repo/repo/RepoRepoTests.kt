package com.littlegit.server.repo.repo

import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.user.User
import com.littlegit.server.repo.RepoCacheKeys
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.util.inject
import littlegitcore.RepoCreationResult
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RepoRepoTests {

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidRepo_ThrowsException() {
        val createRepoModel = CreateRepoModel("Test_InvalidRepo_Name_Which_Is_Much_Too_Long", "")
        val user = UserHelper.createTestUser()
        RepositoryHelper.repoRepository.createRepo(createRepoModel, user, RepoCreationResult(), 1)
    }

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidRepo_InvalidChars_ThrowsException() {
        val createRepoModel = CreateRepoModel("repo&invalid")
        val user = UserHelper.createTestUser()

        RepositoryHelper.repoRepository.createRepo(createRepoModel, user, RepoCreationResult(), 1)
    }

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidRepo_NameStartingWithDash_ThrowsException() {
        val createRepoModel = CreateRepoModel("-repo-invalid")
        val user = UserHelper.createTestUser()

        RepositoryHelper.repoRepository.createRepo(createRepoModel, user, RepoCreationResult(), 1)
    }

    @Test
    fun testCreateValidRepo_AndGetIt_IsSuccessful() {
        val repoName = "test_create-valid"

        val cleaner = {
            CleanupHelper.cleanupRepo(repoName)
        }

        cleaner()
        val testUser = UserHelper.createTestUser()
        val cloneUrl = "clone_url"
        val serverId = 1
        val createRepoModel = CreateRepoModel(repoName, "description")

        try {
            val id = RepositoryHelper.repoRepository.createRepo(createRepoModel, testUser, RepoCreationResult(cloneUrl = cloneUrl), serverId)

            assertNotNull(id)

            val cacheKey = RepoCacheKeys.REPO_CACHE_KEY_BY_ID.inject(id)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdRepo = RepositoryHelper.repoRepository.getRepo(id)

            // Check all the values are as expected
            assertRepo(createRepoModel, testUser, cloneUrl, serverId, createdRepo)

            // Check exists in cache
            val repoFromCache = RepositoryHelper.cache.get(cacheKey, Repo::class.java)
            assertRepo(createRepoModel, testUser, cloneUrl, serverId, repoFromCache)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetRepo_ByUserAndName_IsSuccessful() {
        val repoName = "test_create_valid"

        val cleaner = {
            CleanupHelper.cleanupRepo(repoName)
        }

        cleaner()
        val testUser = UserHelper.createTestUser()
        val cloneUrl = "clone_url"
        val serverId = 1
        val createRepoModel = CreateRepoModel(repoName, "description")

        try {
            val id = RepositoryHelper.repoRepository.createRepo(createRepoModel, testUser, RepoCreationResult(cloneUrl = cloneUrl), serverId)
            assertNotNull(id)

            val retrievedRepo = RepositoryHelper.repoRepository.getRepoByNameAndCreator(testUser, repoName)

            // Check all the values are as expected
            assertRepo(createRepoModel, testUser, cloneUrl, serverId, retrievedRepo)

        } finally {
            cleaner()
        }
    }

    // Helper
    private fun assertRepo(expected: CreateRepoModel, expectedUser: User, expectedCloneUrl: String, expectedServerId: Int, actual: Repo?) {
        assertEquals(expected.repoName, actual?.repoName)
        assertEquals(expected.description, actual?.description)
        assertEquals(expectedUser.id, actual?.creatorId)
        assertEquals(expectedCloneUrl, actual?.cloneUrlPath)
        assertEquals(expectedServerId, actual?.serverId)
    }
}