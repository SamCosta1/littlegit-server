package com.littlegit.server.repo

import com.littlegit.server.db.Cache
import com.littlegit.server.db.DatabaseConnector
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.user.UserId
import com.littlegit.server.util.TokenGenerator
import com.littlegit.server.util.inject
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor (private val dbCon: DatabaseConnector,
                                          private val cache: Cache,
                                          private val tokenGenerator: TokenGenerator) {

    companion object {
        const val FULL_TOKEN = "FullToken(token:{0})"
    }

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
        val retrieved = cache.retrieve(FULL_TOKEN.inject(token), Token::class.java) {
            dbCon.executeSelect("""
                SELECT  userId, token, tokenType, expiry
                FROM    UserTokens
                WHERE   token = :token
            """, Token::class.java, params = mapOf("token" to token))?.firstOrNull()
        } ?: return null

        return if (retrieved.expiry.isAfter(OffsetDateTime.now())) {
            retrieved
        } else {
            // Most common use case is after retrieving a null token, that the user tries to re-authorize i.e it'll be
            // rare that the same token is checked twice so we invalidate the cache to free up space

            invalidateCache(token)
            null
        }
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

    fun invalidateCache(token: String) {
        cache.delete(FULL_TOKEN.inject(token))
    }
}
