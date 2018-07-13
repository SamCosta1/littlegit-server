package com.littlegit.server.model.auth

import com.littlegit.server.model.user.UserId

data class RefreshResponse(val accessToken: String, val scheme: String, val expiry: Int)
data class RefreshRequest(val refreshToken: String, val userId: UserId)