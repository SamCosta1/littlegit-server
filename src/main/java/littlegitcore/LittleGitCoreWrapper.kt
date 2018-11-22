package littlegitcore

import com.littlegit.server.application.exception.LittleGitCommandFailedException
import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.Validatable
import com.littlegit.server.model.ValidatableResult
import com.littlegit.server.model.i18n.LocalizableString
import com.littlegit.server.model.repo.CreateRepoModel
import com.littlegit.server.model.user.User
import com.littlegit.server.util.stripWhiteSpace
import org.littlegit.core.LittleGitCore
import org.littlegit.core.commandrunner.GitResult
import javax.inject.Inject

data class RepoCreationResult(val cloneUrl: String = "", val filePath: String = ""): Validatable {
    override fun validate(): ValidatableResult {
        val messages = mutableListOf<LocalizableString>()

        if (cloneUrl.length >= 200) messages.add(LocalizableString.CloneUrlTooLongKey)
        if (filePath.length >= 200) messages.add(LocalizableString.FilePathTooLongKey)

        return ValidatableResult(messages)
    }

}

class LittleGitCoreWrapper @Inject constructor(private val settingsProvider: SettingsProvider) {

    fun initRepo(user: User, repo: CreateRepoModel, server: GitServer): RepoCreationResult {

        val littleGit = buildLittleGitCore(server)
        val repoPath = "${user.username.stripWhiteSpace()}/${repo.repoName.stripWhiteSpace()}"

        val result = littleGit.repoModifier.initializeRepo(bare = true, name = repoPath).result

        if (result is GitResult.Error) {
            throw LittleGitCommandFailedException(result.err)
        }

        val filePath = settingsProvider.settings.gitServer.reposPath.resolve(repoPath).normalize().toString()
        val cloneUrl = "${settingsProvider.settings.gitServer.gitUser}@${server.ip.hostAddress}:$filePath"
        return RepoCreationResult(cloneUrl, filePath)
    }

    private fun buildLittleGitCore(server: GitServer): LittleGitCore =
            LittleGitCore.Builder()
                .setRemoteHost(server.ip.hostAddress)
                .setRemoteUser(settingsProvider.settings.gitServer.sshUser)
                .setRepoDirectoryPath(settingsProvider.settings.gitServer.reposPath)
                .build()
}