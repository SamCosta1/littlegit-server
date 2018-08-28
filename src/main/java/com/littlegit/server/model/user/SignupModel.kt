package com.littlegit.server.model.user

import com.littlegit.server.model.Validatable
import com.littlegit.server.model.ValidatableResult
import com.littlegit.server.model.i18n.LocalizableString
import com.littlegit.server.util.ValidationUtils

class SignupModel(val email: String,
                       val password: String,
                       val firstName: String,
                       val surname: String,
                       val languageCode: String): Validatable {

    override fun validate(): ValidatableResult {
        val invalidMessages = mutableListOf<LocalizableString>()

        if (!ValidationUtils.validateEmail(email))       { invalidMessages.add(LocalizableString.InvalidEmail)    }
        if (!ValidationUtils.validatePassword(password)) { invalidMessages.add(LocalizableString.InvalidPassword) }
        if (firstName.isBlank())    { invalidMessages.add(LocalizableString.FirstNameBlank) }
        if (surname.isBlank())      { invalidMessages.add(LocalizableString.SurnameBlank)    }
        if (languageCode.isBlank()) { invalidMessages.add(LocalizableString.LanguageCodeBlank)    }

        if (surname.length > 50)        { invalidMessages.add(LocalizableString.SurnameTooLong) }
        if (firstName.length > 50)      { invalidMessages.add(LocalizableString.FirstNameTooLong) }
        if (languageCode.length > 20)   { invalidMessages.add(LocalizableString.InvalidLanguageCode) }

        return ValidatableResult(invalidMessages.isEmpty(), invalidMessages)
    }

}