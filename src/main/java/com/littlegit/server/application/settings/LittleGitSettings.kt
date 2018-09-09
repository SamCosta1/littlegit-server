package com.littlegit.server.application.settings

import java.nio.file.Path
import java.nio.file.Paths

data class LittleGitSettings(val db: DbConfig,
                             val redis: RedisConfig,
                             val tokens: TokensConfig,
                             val isDebug: Boolean = false,
                             val gitServer: GitServerConfig,
                             val apiKeys: Set<String> = HashSet()) {

}

data class GitServerConfig(val sshUser: String,
                           private val reposDirectory: String,
                           val gitUser: String,
                           val scripts: GitServerScripts) {
    val reposPath: Path; get() = Paths.get(reposDirectory)
}

data class GitServerScripts(val addSshKey: String)