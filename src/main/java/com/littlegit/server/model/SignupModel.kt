package com.littlegit.server.model

data class SignupModel(val email: String,
                       val password: String,
                       val firstName: String,
                       val surname: String,
                       val languageCode: String)