package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.user.User

object RepoHelper {

    fun insertTestRepo(repoName: String = "Test_Repo_Name",
                       description: String = "Test_Repo_Description",
                       user: User,
                       cloneUrl: String = "clone_url",
                       serverId: Int = 1): Repo {
        val repoId = RepositoryHelper.repoRepository.createRepo(CreateRepoModel(repoName, description), user, cloneUrl, serverId)

        return RepositoryHelper.repoRepository.getRepo(repoId)!!
    }
}