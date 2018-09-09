package com.littlegit.server.repo.repo

import com.littlegit.server.model.CreateGitServerModel
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.GitServerRegion
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.repo.GitServerCacheKeys
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.assertGitServer
import com.littlegit.server.util.inject
import littlegitcore.RepoCreationResult
import org.junit.Test
import java.net.InetAddress
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitServerRepoTests {

    @Test(expected = InvalidModelException::class)
    fun testCreateInvalidServer_ThrowsException() {
        // Invalid capacity
        val createModel = CreateGitServerModel(InetAddress.getByName("127.0.0.1"), GitServerRegion.UK, -10)
        RepositoryHelper.gitServerRepository.createGitServer(createModel)
    }

    @Test
    fun testCreateValidServer_AndGetIt_IsSuccessful() {
        val ip = InetAddress.getByName("192.0.2.0")

        val cleaner = {
            CleanupHelper.cleanupServer(ip)
        }

        cleaner()

        val createServerModel = CreateGitServerModel(ip, GitServerRegion.UK, 1)

        try {
            val id = RepositoryHelper.gitServerRepository.createGitServer(createServerModel)

            assertNotNull(id)

            val cacheKey = GitServerCacheKeys.SERVER_CACHE_BY_ID.inject(id)

            // Check exists in db
            RepositoryHelper.cache.delete(cacheKey)
            val createdServer = RepositoryHelper.gitServerRepository.getGitServer(id)

            // Check all the values are as expected
            assertGitServer(createServerModel, createdServer)

            // Check exists in cache
            val serverFromCache = RepositoryHelper.cache.get(cacheKey, GitServer::class.java)
            assertGitServer(createServerModel, serverFromCache)
        } finally {
            cleaner()
        }
    }

    @Test
    fun testGetServersForUser_IsSuccessful() {
        val ip1 = InetAddress.getByName("192.0.2.1")
        val ip2 = InetAddress.getByName("192.0.2.2")
        val ip3 = InetAddress.getByName("192.0.2.3")

        val createUserModel = UserHelper.createSignupModel()
        val repo1 = CreateRepoModel("get_servers", "Gimli's awesome repo")
        val repo2 = CreateRepoModel("get_servers_1", "Gandalf's awesome repo")

        val cleaner = {
            CleanupHelper.cleanupServers(ip1, ip2, ip3)
            CleanupHelper.cleanupRepos(repo1.repoName, repo2.repoName)
            CleanupHelper.cleanupRepoAccess(createUserModel.email, repo1.repoName)
            CleanupHelper.cleanupRepoAccess(createUserModel.email, repo2.repoName)
        }

        cleaner()

        try {
            val userId = RepositoryHelper.userRepository.createUser(createUserModel)!!
            val user = RepositoryHelper.userRepository.getUser(userId)!!
            // Create three servers
            val server1Id = RepositoryHelper.gitServerRepository.createGitServer(CreateGitServerModel(ip1, GitServerRegion.UK, 10))
            val server2Id = RepositoryHelper.gitServerRepository.createGitServer(CreateGitServerModel(ip2, GitServerRegion.UK, 10))
            val server3Id = RepositoryHelper.gitServerRepository.createGitServer(CreateGitServerModel(ip3, GitServerRegion.UK, 10))

            // Create two repos, one on server1, one on server2 leaving server 3 without any
            val repo1Id = RepositoryHelper.repoRepository.createRepo(repo1, user, RepoCreationResult(), server1Id)
            val repo2Id = RepositoryHelper.repoRepository.createRepo(repo2, user, RepoCreationResult(), server2Id)

            // Grant the user access
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, RepositoryHelper.repoRepository.getRepo(repo1Id)!!, RepoAccessLevel.Contributor)
            RepositoryHelper.repoAccessRepository.grantRepoAccess(user, RepositoryHelper.repoRepository.getRepo(repo2Id)!!, RepoAccessLevel.Contributor)

            val servers = RepositoryHelper.gitServerRepository.getUserServers(userId)
            assertEquals(2, servers?.size)
            assertTrue(servers?.find { it.ip == ip1 } != null)
            assertTrue(servers?.find { it.ip == ip2 } != null)
            assertTrue(servers?.find { it.ip == ip3 } == null)
        } finally {
            cleaner()
        }
    }
}