package com.littlegit.server.model

data class User(val id: Int,
                val email: String,
                val firstName: String,
                val surname: String,
                val passwordHash: String,
                val passwordSalt: String,
                val role: Int,
                val languageCode: String)