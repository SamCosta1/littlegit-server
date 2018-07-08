package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.user.UserId
import com.littlegit.server.util.TokenGenerator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache,
                                          private val tokenGenerator: TokenGenerator) {

    // Creates a new access token for a user and returns that access token
    fun createAndSaveAccessToken(userId: UserId): String {
        val token = tokenGenerator.generateAccessToken(userId)
        saveToken(token)

        return token.token
    }

    fun createAndSaveRefreshToken(userId: UserId): String {
        val token = tokenGenerator.generateRefreshToken(userId)
        saveToken(token)

        return token.token
    }

    private fun saveToken(token: Token) {
        dbCon.executeInsert("""
            INSERT INTO UserTokens
            VALUES (:userId, :token, :tokenType, :expiry)
        """, model = token)
    }

    fun invlaidateCache(userId: UserId) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
