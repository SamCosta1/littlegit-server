package com.littlegit.server.model.user

import com.littlegit.server.model.Validatable
import com.littlegit.server.model.ValidatableResult
import com.littlegit.server.util.ValidationUtils

data class LoginModel(val email: String, val password: String) : Validatable {

    override fun validate(): ValidatableResult {
        val messages = mutableListOf<String>()

        if (!ValidationUtils.validateEmail(email))       { messages.add("Invalid email") }
        if (!ValidationUtils.validatePassword(password)) { messages.add("Invalid password") }

        return ValidatableResult(messages.isEmpty(), messages)
    }
}

class LoginResponseModel(val accessToken: String, val refreshToken: String, val scheme: String, val user: User)