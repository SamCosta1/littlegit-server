package com.littlegit.server.model

import com.littlegit.server.util.ValidationUtils

class SignupModel(val email: String,
                       val password: String,
                       val firstName: String,
                       val surname: String,
                       val languageCode: String): Validatable {

    override fun validate(): ValidatableResult {
        val invalidMessages = mutableListOf<String>()

        if (!ValidationUtils.validateEmail(email))       { invalidMessages.add("Email invalid")    }
        if (!ValidationUtils.validatePassword(password)) { invalidMessages.add("Password invalid") }
        if (firstName.isBlank())    { invalidMessages.add("First name cannot be empty") }
        if (surname.isBlank())      { invalidMessages.add("Surname cannot be empty")    }
        if (languageCode.isBlank()) { invalidMessages.add("language code cannot be empty")    }

        return ValidatableResult(invalidMessages.isEmpty(), invalidMessages)
    }

}