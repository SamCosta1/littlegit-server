package com.littlegit.server.repo.testUtils

import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.moshi.MoshiModule
import com.littlegit.server.repo.UserRepository

object RepositoryHelper {

    val dbConnector: DatabaseConnector
    val cache: Cache
    init {
        val moshi = MoshiModule().provideMoshi()
        val settingsProvider = SettingsProvider(moshi)
        cache = Cache(moshi, settingsProvider)
        dbConnector = DatabaseConnector(settingsProvider)
    }

    val userRepository = UserRepository(dbConnector, cache)
}