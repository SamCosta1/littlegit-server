package com.littlegit.server.service

import com.littlegit.server.application.exception.DuplicateRecordException
import com.littlegit.server.application.remoterunner.RemoteCommandRunner
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.RepoAccess
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.repo.RepoSummary
import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.model.user.User
import com.littlegit.server.model.user.UserId
import com.littlegit.server.repo.GitServerRepository
import com.littlegit.server.repo.RepoAccessRepository
import com.littlegit.server.repo.RepoRepository
import com.littlegit.server.repo.SshKeyRepository
import littlegitcore.LittleGitCoreWrapper
import javax.inject.Inject

class RepoService @Inject constructor (private val repoRepository: RepoRepository,
                                       private val repoAccessRepository: RepoAccessRepository,
                                       private val gitServerRepository: GitServerRepository,
                                       private val sshKeyRepository: SshKeyRepository,
                                       private val remoteCommandRunner: RemoteCommandRunner,
                                       private val littleGitCoreWrapper: LittleGitCoreWrapper) {

    fun createRepo(user: User, createRepoModel: CreateRepoModel): RepoSummary? {

        val server = gitServerRepository.getBestGitServerForUser(user) ?: throw Exception("Git server couldn't be found") // Should never happen

        // Check the user doesn't already have a repo with this name
        val existingRepo = repoRepository.getRepoByNameAndCreator(user, createRepoModel.repoName)

        if (existingRepo != null) {
            throw DuplicateRecordException(User::class.java)
        }

        val userHasRepoOnServer = repoAccessRepository.userHasRepoOnServer(server, user) ?: throw UnknownError()

        if (!userHasRepoOnServer) {
            // Add the user's ssh keys to the server
            val sshKeys = sshKeyRepository.getSshKeysForUser(user, true)
            sshKeys?.forEach {
                remoteCommandRunner.addSshKey(it, server)
            }
        }

        // Init the repo on the server
        val initResult = littleGitCoreWrapper.initRepo(user, createRepoModel, server)

        // Create a record for the repo in the db
        val repoId = repoRepository.createRepo(createRepoModel, user, initResult, server.id)

        val repo = repoRepository.getRepo(repoId) ?: throw UnknownError()

        // Give this user access to it
        val repoAccessLevel = when(user.role) {
            AuthRole.BasicUser -> RepoAccessLevel.Contributor
            else -> RepoAccessLevel.Owner
        }

        repoAccessRepository.grantRepoAccess(user, repo, repoAccessLevel)
        repoRepository.invalidateCache(user) // Important, otherwise repo won't exist in user's list of repos
        return repoRepository.getRepoSummary(repoId)
    }

    /**
     * Returns all the repos the user currently has access to
     */
    fun getReposForUser(user: User): List<RepoSummary>? {
        return repoRepository.getAllReposForUser(user)
    }

    fun getRepoAccessStatus(user: User, repoFilePath: String): RepoAccess? = getRepoAccessStatus(user.id, repoFilePath)

    fun getRepoAccessStatus(userId: UserId, repoFilePath: String): RepoAccess? {
        return repoAccessRepository.getRepoAccessStatus(userId, repoFilePath)
    }

    /**
     * Returns true if the user can access the repo and no otherwise
     * Optimised this way for use by the git servers
     */
    fun getRepoAccessStatusBoolean(user: UserId, repoFilePath: String): Boolean = getRepoAccessStatus(user, repoFilePath)?.active == true
}