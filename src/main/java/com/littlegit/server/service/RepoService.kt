package com.littlegit.server.service

import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoAccessLevel
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

    fun createRepo(user: User, createRepoModel: CreateRepoModel): Repo? {

        val server = gitServerRepository.getBestGitServerForUser(user) ?: throw Exception("Git server couldn't be found") // Should never happen

        // Init the repo on the server
        val clonePath = littleGitCoreWrapper.initRepo(createRepoModel, server)

        // Create a record for the repo in the db
        val repoId = repoRepository.createRepo(createRepoModel, user, clonePath.toString(), server.id)

        // Give this user access to it
        val repoAccessLevel = when(user.role) {
            AuthRole.BasicUser -> RepoAccessLevel.Contributor
            else -> RepoAccessLevel.Owner
        }

        repoAccessRepository.grantRepoAccess(user, repoId, repoAccessLevel)

        return repoRepository.getRepo(repoId)
    }
}