package com.littlegit.server.repo.testUtils

import com.littlegit.server.application.remoterunner.RemoteCommandRunner
import com.littlegit.server.service.AuthService
import com.littlegit.server.service.RepoService
import littlegitcore.LittleGitCoreWrapper

object ServiceHelper {
    private val littlegitCoreWrapper = LittleGitCoreWrapper(RepositoryHelper.settingsProvider)

    val authService: AuthService = AuthService(
            RepositoryHelper.authRepository,
            RepositoryHelper.userRepository,
            RepositoryHelper.settingsProvider)

    val repoService: RepoService = RepoService(
            RepositoryHelper.repoRepository,
            RepositoryHelper.repoAccessRepository,
            RepositoryHelper.gitServerRepository,
            RepositoryHelper.sshKeyRepository,
            littlegitCoreWrapper)
}