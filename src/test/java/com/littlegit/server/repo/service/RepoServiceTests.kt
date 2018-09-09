package com.littlegit.server.repo.service

import com.littlegit.server.application.exception.DuplicateRecordException
import com.littlegit.server.application.remoterunner.RemoteCommandRunner
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.repo.GitServerRepository
import com.littlegit.server.repo.RepoAccessRepository
import com.littlegit.server.repo.RepoRepository
import com.littlegit.server.repo.SshKeyRepository
import com.littlegit.server.repo.testUtils.GitServerHelper
import com.littlegit.server.repo.testUtils.RepoHelper
import com.littlegit.server.repo.testUtils.UserHelper
import com.littlegit.server.repo.testUtils.upon
import com.littlegit.server.service.RepoService
import littlegitcore.LittleGitCoreWrapper
import littlegitcore.RepoCreationResult
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
    private lateinit var sshKeyRepository: SshKeyRepository
    private lateinit var remoteCommandRunner: RemoteCommandRunner

    @Before
    fun setup() {
        repoRepoMock = mock(RepoRepository::class.java)
        repoAccessRepoMock = mock(RepoAccessRepository::class.java)
        gitServerRepository = mock(GitServerRepository::class.java)
        littleGitCoreWrapper = mock(LittleGitCoreWrapper::class.java)
        sshKeyRepository = mock(SshKeyRepository::class.java)
        remoteCommandRunner = mock(RemoteCommandRunner::class.java)

        repoService = RepoService(repoRepoMock, repoAccessRepoMock, gitServerRepository, sshKeyRepository, remoteCommandRunner, littleGitCoreWrapper)
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
        val repo = RepoHelper.createTestRepo(name = repoName, user = user)

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(GitServerHelper.gitServer())
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(repo)

        repoService.createRepo(user, CreateRepoModel(repoName))
        verify(repoRepoMock, times(0)).createRepo(ArgumentMatchers.any(), user, ArgumentMatchers.any(), ArgumentMatchers.anyInt())
    }

    @Test
    fun testCreateRepo_AsBasicUser_IsSuccessful() {
        val user = UserHelper.createTestUser(authRole = AuthRole.BasicUser)
        val repoName = "PlansForMoria"
        val createModel = CreateRepoModel(repoName)
        val server = GitServerHelper.gitServer()
        val repoId = 1
        val repo = RepoHelper.createTestRepo(id = repoId, user = user)
        val repoCreationResult = RepoCreationResult("git@ipaddress:/${user.username}/$repoName", "/${user.username}/$repoName")

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(server)
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(null)
        upon(repoRepoMock.createRepo(createModel, user, repoCreationResult, server.id)).thenReturn(repoId)
        upon(repoRepoMock.getRepo(repoId)).thenReturn(repo)
        upon(littleGitCoreWrapper.initRepo(user, createModel, server)).thenReturn(repoCreationResult)

        repoService.createRepo(user, createModel)

        verify(repoRepoMock, times(1)).getRepoSummary(repoId)
        verify(repoAccessRepoMock, times(1)).grantRepoAccess(user, repo, RepoAccessLevel.Contributor)
    }

    @Test
    fun testCreateRepo_AsAdmin_IsSuccessful() {
        val user = UserHelper.createTestUser(authRole = AuthRole.Admin)
        val repoName = "PlansForMoria"
        val createModel = CreateRepoModel(repoName)
        val server = GitServerHelper.gitServer()
        val repoId = 1
        val repoCreationResult = RepoCreationResult("git@ipaddress:/${user.username}/$repoName", "/${user.username}/$repoName")
        val repo = RepoHelper.createTestRepo(user = user)

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(server)
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(null)
        upon(repoRepoMock.createRepo(createModel, user, repoCreationResult, server.id)).thenReturn(repoId)
        upon(littleGitCoreWrapper.initRepo(user, createModel, server)).thenReturn(repoCreationResult)
        upon(repoRepoMock.getRepo(repoId)).thenReturn(repo)

        repoService.createRepo(user, createModel)

        verify(repoRepoMock, times(repoId)).getRepoSummary(repoId)
        verify(repoAccessRepoMock, times(repoId)).grantRepoAccess(user, repo, RepoAccessLevel.Owner)
    }

    @Test
    fun testCreateRepo_AsOrganizationAdmin_IsSuccessful() {
        val user = UserHelper.createTestUser(authRole = AuthRole.OrganizationAdmin)
        val repoName = "PlansForMoria"
        val createModel = CreateRepoModel(repoName)
        val server = GitServerHelper.gitServer()
        val repoId = 1
        val cloneUrlPath = RepoCreationResult("${user.username}/$repoName")
        val repo = RepoHelper.createTestRepo(user = user)

        upon(gitServerRepository.getBestGitServerForUser(user)).thenReturn(server)
        upon(repoRepoMock.getRepoByNameAndCreator(user, repoName)).thenReturn(null)
        upon(repoRepoMock.createRepo(createModel, user, cloneUrlPath, server.id)).thenReturn(repoId)
        upon(littleGitCoreWrapper.initRepo(user, createModel, server)).thenReturn(cloneUrlPath)
        upon(repoRepoMock.getRepo(repoId)).thenReturn(RepoHelper.createTestRepo(user = user))
        upon(repoRepoMock.getRepo(repoId)).thenReturn(repo)

        repoService.createRepo(user, createModel)

        verify(repoRepoMock, times(repoId)).getRepoSummary(repoId)
        verify(repoAccessRepoMock, times(repoId)).grantRepoAccess(user, repo, RepoAccessLevel.Owner)
    }
}