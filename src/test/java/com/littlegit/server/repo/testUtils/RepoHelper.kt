package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.repo.Repo
import com.littlegit.server.model.repo.RepoId
import com.littlegit.server.model.user.User
import littlegitcore.RepoCreationResult
import java.time.OffsetDateTime

object RepoHelper {

    fun insertTestRepo(repoName: String = "Test_Repo_Name",
                       description: String = "Test_Repo_Description",
                       user: User,
                       cloneUrl: String = "clone_url",
                       filePath: String = "filePath",
                       serverId: Int = 1): Repo {
        val repoId = RepositoryHelper.repoRepository.createRepo(CreateRepoModel(repoName, description), user, RepoCreationResult(cloneUrl, filePath), serverId)

        return RepositoryHelper.repoRepository.getRepo(repoId)!!
    }

    fun createTestRepo(id: RepoId = 1,
                       name: String = "Test_Repo_Name",
                       description: String = "Test_Repo_Description",
                       user: User,
                       cloneUrl: String = "clone_url",
                       filePath: String = "/Git/${user.username}/$name",
                       serverId: Int = 1): Repo = Repo(id, name, OffsetDateTime.now(), user.id, description, serverId, cloneUrl, filePath)
}