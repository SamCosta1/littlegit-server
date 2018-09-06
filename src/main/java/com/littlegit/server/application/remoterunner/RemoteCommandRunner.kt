package com.littlegit.server.application.remoterunner

import com.littlegit.server.application.exception.RemoteRunnerException
import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.GitServer
import com.littlegit.server.model.user.SshKey
import org.littlegit.core.shell.ShellResult
import org.littlegit.core.shell.ShellRunnerRemote
import java.nio.file.Paths
import javax.inject.Inject

class RemoteCommandRunner @Inject constructor(val settingsProvider: SettingsProvider) {


    fun addSshKey(sshKey: SshKey, server: GitServer) {
        val shellRunner = ShellRunnerRemote(settingsProvider.settings.gitServer.sshUser, server.ip.hostAddress, Paths.get("/"))

        val command = "${settingsProvider.settings.gitServer.scripts.addSshKey} \"${sshKey.publicKey}\" ${sshKey.userId}"
        val result = shellRunner.runCommand(listOf(command))

        if (result is ShellResult.Error) {
            throw RemoteRunnerException(result)
        }
    }
}