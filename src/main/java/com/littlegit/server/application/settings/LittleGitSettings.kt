package com.littlegit.server.application.settings

import com.littlegit.server.application.MoshiMessageBodyHandler
import com.squareup.moshi.Moshi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

data class LittleGitSettings(val db: DbConfig, val redis: RedisConfig)