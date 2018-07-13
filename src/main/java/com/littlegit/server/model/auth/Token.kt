package com.littlegit.server.model.auth

import com.littlegit.server.model.user.UserId
import java.time.OffsetDateTime

// Expiry in seconds
data class Token(val userId: UserId, val token: String, val tokenType: TokenType, val expiry: OffsetDateTime)
