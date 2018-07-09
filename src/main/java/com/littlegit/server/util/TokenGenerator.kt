package com.littlegit.server.util

import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.auth.Token
import com.littlegit.server.model.auth.TokenType
import com.littlegit.server.model.user.UserId
import java.security.SecureRandom
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenGenerator @Inject constructor(private val settingsProvider: SettingsProvider) {

    private val secureRandom = SecureRandom()

    fun generateAccessToken(userId: UserId): Token {
        val tokenDuration = settingsProvider.settings.tokens.accessTokenDuration
        val expiryDate = OffsetDateTime.now().plusSeconds(tokenDuration.toLong()).withNano(0)
        val token = generate(20)

        return Token(userId, token, TokenType.AccessToken, expiryDate)
    }

    fun generateRefreshToken(userId: UserId): Token {
        val tokenDuration = settingsProvider.settings.tokens.refreshTokenDuration
        val expiryDate = OffsetDateTime.now().plusSeconds(tokenDuration.toLong()).withNano(0)
        val token = generate(30)

        return Token(userId, token, TokenType.RefreshToken, expiryDate)
    }

    private fun generate(length: Int): String {
        val bytes = ByteArray(length)
        secureRandom.nextBytes(bytes)

        return StringUtils.bytesToString(bytes)
    }
}