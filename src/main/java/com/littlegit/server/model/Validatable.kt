package com.littlegit.server.model

import com.littlegit.server.model.i18n.LocalizableString

data class ValidatableResult(val isValid: Boolean, val invalidMessages: List<LocalizableString>) {
    val isNotValid: Boolean; get() = !isValid
}

class InvalidModelException(val result: ValidatableResult): Exception()

interface Validatable {
    fun validate(): ValidatableResult
}
