package com.littlegit.server.model.user

import com.littlegit.server.model.Validatable
import com.littlegit.server.model.ValidatableResult
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

        if (surname.length > 50)        { invalidMessages.add("surname too long") }
        if (firstName.length > 50)      { invalidMessages.add("First name too long") }
        if (languageCode.length > 20)   { invalidMessages.add("Invalid language code") }

        return ValidatableResult(invalidMessages.isEmpty(), invalidMessages)
    }

}