package com.littlegit.server.repo.repo

import com.littlegit.server.model.CreateGitServerModel
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.GitServerRegion
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.repo.GitServerCacheKeys
import com.littlegit.server.repo.testUtils.CleanupHelper
import com.littlegit.server.repo.testUtils.RepositoryHelper
import com.littlegit.server.repo.testUtils.assertGitServer
import com.littlegit.server.util.inject
import org.junit.Test
import java.net.InetAddress
import kotlin.test.assertNotNull

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


}