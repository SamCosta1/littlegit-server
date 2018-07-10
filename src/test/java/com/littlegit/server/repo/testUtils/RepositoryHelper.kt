package com.littlegit.server.repo.testUtils

import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.repo.AuthRepository
import com.littlegit.server.serializatoin.MoshiModule
import com.littlegit.server.repo.UserRepository
import com.littlegit.server.util.TokenGenerator

object RepositoryHelper {

    val dbConnector: DatabaseConnector
    val cache: Cache
    val settingsProvider : SettingsProvider
    val tokenGenerator: TokenGenerator
    init {
        val moshi = MoshiModule().provideMoshi()
        settingsProvider = SettingsProvider(moshi)
        cache = Cache(moshi, settingsProvider)
        dbConnector = DatabaseConnector(settingsProvider)
        tokenGenerator = TokenGenerator(settingsProvider)

    }

    val userRepository = UserRepository(dbConnector, cache)
    val authRepository = AuthRepository(dbConnector, cache, tokenGenerator)
}