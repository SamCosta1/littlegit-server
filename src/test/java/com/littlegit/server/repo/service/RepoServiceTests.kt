package com.littlegit.server.repo.service

import com.littlegit.server.application.exception.DuplicateRecordException
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.user.AuthRole
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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

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

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(GitServerHelper.gitServer())
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(RepoHelper.createTestRepo(name = repoName, user = user))

        repoService.createRepo(user, CreateRepoModel(repoName))
        verify(repoRepoMock, times(0)).createRepo(ArgumentMatchers.any(), user, ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())
    }

    @Test
    fun testCreateRepo_AsBasicUser_IsSuccessful() {
        val user = UserHelper.createTestUser(authRole = AuthRole.BasicUser)
        val repoName = "PlansForMoria"
        val createModel = CreateRepoModel(repoName)
        val server = GitServerHelper.gitServer()
        val repoId = 1
        val cloneUrlPath = "git@ipaddress:/${user.username}/$repoName"

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(server)
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(null)
        upon(repoRepoMock.createRepo(createModel, user, cloneUrlPath, server.id)).thenReturn(repoId)
        upon(littleGitCoreWrapper.initRepo(user, createModel, server)).thenReturn(cloneUrlPath)

        repoService.createRepo(user, createModel)

        verify(repoRepoMock, times(repoId)).getRepoSummary(repoId)
        verify(repoAccessRepoMock, times(repoId)).grantRepoAccess(user, repoId, RepoAccessLevel.Contributor)
    }

    @Test
    fun testCreateRepo_AsAdmin_IsSuccessful() {
        val user = UserHelper.createTestUser(authRole = AuthRole.Admin)
        val repoName = "PlansForMoria"
        val createModel = CreateRepoModel(repoName)
        val server = GitServerHelper.gitServer()
        val repoId = 1
        val cloneUrlPath = "git@ipaddress:/${user.username}/$repoName"

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(server)
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(null)
        upon(repoRepoMock.createRepo(createModel, user, cloneUrlPath, server.id)).thenReturn(repoId)
        upon(littleGitCoreWrapper.initRepo(user, createModel, server)).thenReturn(cloneUrlPath)

        repoService.createRepo(user, createModel)

        verify(repoRepoMock, times(repoId)).getRepoSummary(repoId)
        verify(repoAccessRepoMock, times(repoId)).grantRepoAccess(user, repoId, RepoAccessLevel.Owner)
    }

    @Test
    fun testCreateRepo_AsOrganizationAdmin_IsSuccessful() {
        val user = UserHelper.createTestUser(authRole = AuthRole.OrganizationAdmin)
        val repoName = "PlansForMoria"
        val createModel = CreateRepoModel(repoName)
        val server = GitServerHelper.gitServer()
        val repoId = 1
        val cloneUrlPath = "${user.username}/$repoName"

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(server)
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(null)
        upon(repoRepoMock.createRepo(createModel, user, cloneUrlPath, server.id)).thenReturn(repoId)
        upon(littleGitCoreWrapper.initRepo(user, createModel, server)).thenReturn(cloneUrlPath)

        repoService.createRepo(user, createModel)

        verify(repoRepoMock, times(repoId)).getRepoSummary(repoId)
        verify(repoAccessRepoMock, times(repoId)).grantRepoAccess(user, repoId, RepoAccessLevel.Owner)
    }
}