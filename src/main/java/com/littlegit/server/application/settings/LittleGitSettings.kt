package com.littlegit.server.application.settings

data class LittleGitSettings(val db: DbConfig,
                             val redis: RedisConfig,
                             val tokens: TokensConfig,
                             val isDebug: Boolean = false)
