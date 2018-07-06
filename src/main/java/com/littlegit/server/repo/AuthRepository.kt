package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.util.HashingUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache) {

    // Creates a new access token for a user and returns that access token
    fun createAndSaveAccessToken(userId: Int): String {
        return "";
    }
}
