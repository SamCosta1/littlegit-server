package com.littlegit.server.repo.service

import com.littlegit.server.application.exception.DuplicateRecordException
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.repo.GitServerRepository
import com.littlegit.server.repo.RepoAccessRepository
import com.littlegit.server.repo.RepoRepository
import com.littlegit.server.repo.testUtils.GitServerHelper
import com.littlegit.server.repo.testUtils.RepoHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.upon
import com.littlegit.server.service.RepoService
import littlegitcore.LittleGitCoreWrapper
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class RepoServiceTests {

    private lateinit var repoService: RepoService

    private lateinit var repoRepoMock: RepoRepository
    private lateinit var repoAccessRepoMock: RepoAccessRepository
    private lateinit var gitServerRepository: GitServerRepository
    private lateinit var littleGitCoreWrapper: LittleGitCoreWrapper

    @Before
    fun setup() {
        repoRepoMock = mock(RepoRepository::class.java)
        repoAccessRepoMock = mock(RepoAccessRepository::class.java)
        gitServerRepository = mock(GitServerRepository::class.java)
        littleGitCoreWrapper = mock(LittleGitCoreWrapper::class.java)

        repoService = RepoService(repoRepoMock, repoAccessRepoMock, gitServerRepository, littleGitCoreWrapper)
    }

    @Test(expected = Exception::class)
    fun testCreateRepo_WithNoServer_ThrowsException() {
        val user = UserHelper.createTestUser()
        val repoName = "PlansForMoria"

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(null)

        repoService.createRepo(user, CreateRepoModel(repoName))
    }

    @Test(expected = DuplicateRecordException::class)
    fun testCreateRepo_WhenUserHasRepoOfSameName_ThrowsException() {
        val user = UserHelper.createTestUser()
        val repoName = "PlansForMoria"

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(GitServerHelper.createGitServer())
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(RepoHelper.createTestRepo(name = repoName, user = user))

        repoService.createRepo(user, CreateRepoModel(repoName))
    }
}