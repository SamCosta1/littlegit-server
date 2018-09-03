package com.littlegit.server.service

import com.littlegit.server.application.exception.DuplicateRecordException
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoAccessLevel
import com.littlegit.server.model.repo.RepoSummary
import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.model.user.User
import com.littlegit.server.repo.GitServerRepository
import com.littlegit.server.repo.RepoAccessRepository
import com.littlegit.server.repo.RepoRepository
import littlegitcore.LittleGitCoreWrapper
import javax.inject.Inject

class RepoService @Inject constructor (private val repoRepository: RepoRepository,
                                       private val repoAccessRepository: RepoAccessRepository,
                                       private val gitServerRepository: GitServerRepository,
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
        }

        // Init the repo on the server
        val clonePath = littleGitCoreWrapper.initRepo(user, createRepoModel, server)

        // Create a record for the repo in the db
        val repoId = repoRepository.createRepo(createRepoModel, user, clonePath, server.id)

        // Give this user access to it
        val repoAccessLevel = when(user.role) {
            AuthRole.BasicUser -> RepoAccessLevel.Contributor
            else -> RepoAccessLevel.Owner
        }

        repoAccessRepository.grantRepoAccess(user, repoId, repoAccessLevel)
        repoAccessRepository.invalidateCache(user, server)
        return repoRepository.getRepoSummary(repoId)
    }
}