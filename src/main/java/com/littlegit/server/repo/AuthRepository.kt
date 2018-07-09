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
    fun createAndSaveAccessToken(userId: UserId): Token {
        val token = tokenGenerator.generateAccessToken(userId)
        saveToken(token)

        return token
    }

    fun createAndSaveRefreshToken(userId: UserId): Token {
        val token = tokenGenerator.generateRefreshToken(userId)
        saveToken(token)

        return token
    }

    fun getFullToken(token: String): Token? {
        return dbCon.executeSelect("""
            SELECT  userId, token, tokenType, expiry
            FROM    UserTokens
            WHERE   token = :token
        """, Token::class.java, params = mapOf("token" to token))?.firstOrNull()
    }

    private fun saveToken(token: Token) {
        dbCon.executeInsert("""
            INSERT INTO UserTokens (
                userId,
                token,
                tokenType,
                expiry
            )
            VALUES (:userId, :token, :tokenType, :expiry)
        """, model = token)
    }

    fun invlaidateCache(userId: UserId) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
