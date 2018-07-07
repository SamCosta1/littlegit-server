package com.littlegit.server.model.auth

enum class TokenType(val code: Int) {

    AccessToken(1),
    RefreshToken(2);

    companion object {
        private val map = TokenType.values().associateBy(TokenType::code)
        fun fromInt(type: Int) = map[type]
    }
}