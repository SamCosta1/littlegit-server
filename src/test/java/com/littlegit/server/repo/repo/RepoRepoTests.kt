package com.littlegit.server.repo.repo

import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.user.User
import com.littlegit.server.repo.RepoCacheKeys
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.util.inject
import littlegitcore.RepoCreationResult
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepoRepoTests {

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidRepo_ThrowsException() {
        val createRepoModel = CreateRepoModel("Test_InvalidRepo_Name_Which_Is_Much_Too_Long_Test_InvalidRepo_Name_Which_Is_Much_Too_Long_Test_InvalidRepo_Name_Which_Is_Much_Too_Long", "")
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
    fun testCreateInvalidRepo_CloneUrlTooLong_ThrowsException() {
        val createRepoModel = CreateRepoModel("repo_valid")
        val user = UserHelper.createTestUser()

        RepositoryHelper.repoRepository.createRepo(createRepoModel, user, RepoCreationResult(cloneUrl = "a".repeat(200)), 1)
    }

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidRepo_FilePathTooLong_ThrowsException() {
        val createRepoModel = CreateRepoModel("repo_valid")
        val user = UserHelper.createTestUser()

        RepositoryHelper.repoRepository.createRepo(createRepoModel, user, RepoCreationResult(filePath = "a".repeat(200)), 1)
    }

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidRepo_NameStartingWithDash_ThrowsException() {
        val createRepoModel = CreateRepoModel("-repo-invalid")
        val user = UserHelper.createTestUser()

        RepositoryHelper.repoRepository.createRepo(createRepoModel, user, RepoCreationResult(), 1)
    }

    @Test
    fun testCreateValidRepo_AndGetIt_IsSuccessful() {
        val repoName = "test_create-valid-of-sensible-length-if-you-understand"

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

            val cacheKey = RepoCacheKeys.REPO_BY_ID.inject(id)

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

    @Test
    fun testGetReposForUser_WithNoRepos_IsSuccessful() {
        val testUser = UserHelper.createTestUser()

        val cleaner = {
            CleanupHelper.cleanupUser(testUser.email)
        }

        try {
            val repos = RepositoryHelper.repoRepository.getAllReposForUser(testUser)
            assertNotNull(repos); repos!!

            assertEquals(0, repos.size)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetReposForUser_WithRevokedRepoAccesses_IsSuccessful() {
        val userEmail = "merry@pippin.com"
        val repoName1 = "merry"
        val repoName2 = "pippin"
        val repoName3 = "tree-beard"

        val cleaner = {
            CleanupHelper.cleanupUser(userEmail)
            CleanupHelper.cleanupRepos(repoName1, repoName2, repoName3)
            CleanupHelper.cleanupRepoAccess(userEmail, repoName1, repoName2, repoName3)
        }

        cleaner()

        try {
            val userId = RepositoryHelper.userRepository.createUser(UserHelper.createSignupModel(email = userEmail))!!
            val user = RepositoryHelper.userRepository.getUser(userId)!!

            val repoId1 = RepositoryHelper.repoRepository.createRepo(CreateRepoModel(repoName1), user, RepoCreationResult(), 1)
            val repoId2 = RepositoryHelper.repoRepository.createRepo(CreateRepoModel(repoName2), user, RepoCreationResult(), 1)
            val repoId3 = RepositoryHelper.repoRepository.createRepo(CreateRepoModel(repoName3), user, RepoCreationResult(), 1)

            val repo1 = RepositoryHelper.repoRepository.getRepo(repoId1)!!
            val repo2 = RepositoryHelper.repoRepository.getRepo(repoId2)!!
            val repo3 = RepositoryHelper.repoRepository.getRepo(repoId3)!!

            // Grant user access to all of them
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo1, RepoAccessLevel.Owner)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo2, RepoAccessLevel.Owner)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, repo3, RepoAccessLevel.Owner)

            // Revoke the user's access to repo3 - It shouldn't appear in the list
            RepositoryHelper.repoAccessRepository.revokeRepoAccess(user, repo3)

            val repos = RepositoryHelper.repoRepository.getAllReposForUser(user)
            assertNotNull(repos); repos!!

            assertEquals(2, repos.size)
            assertTrue(repos.contains(repo1.toRepoSummary()))
            assertTrue(repos.contains(repo2.toRepoSummary()))
            assertFalse(repos.contains(repo3.toRepoSummary()))
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