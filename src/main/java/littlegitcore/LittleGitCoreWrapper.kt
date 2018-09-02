package littlegitcore

import com.littlegit.server.application.exception.LittleGitCommandFailedException
import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.user.User
import com.littlegit.server.util.stripWhiteSpace
import org.littlegit.core.LittleGitCore
import org.littlegit.core.commandrunner.GitResult
import java.nio.file.Path
import javax.inject.Inject

class LittleGitCoreWrapper @Inject constructor(private val settingsProvider: SettingsProvider) {

    fun initRepo(user: User, repo: CreateRepoModel, server: GitServer): Path {

        val littleGit = buildLittleGitCore(server)
        val repoPath = "${user.username.stripWhiteSpace()}/${repo.repoName.stripWhiteSpace()}"

        val result = littleGit.repoModifier.initializeRepo(bare = true, name = repoPath).result


        if (result is GitResult.Error) {
            throw LittleGitCommandFailedException(result.err)
        }

        return settingsProvider.settings.gitServer.reposPath.resolve(repoPath).normalize()
    }

    private fun buildLittleGitCore(server: GitServer): LittleGitCore =
            LittleGitCore.Builder()
                .setRemoteHost(server.ip.hostAddress)
                .setRemoteUser(settingsProvider.settings.gitServer.sshUser)
                .setRepoDirectoryPath(settingsProvider.settings.gitServer.reposPath)
                .build()
}