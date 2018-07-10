package com.littlegit.server.model

data class ValidatableResult(val isValid: Boolean, val invalidMessages: List<String>) {
    val isNotValid: Boolean; get() = !isValid
}

class InvalidModelException(val result: ValidatableResult): Exception()

interface Validatable {
    fun validate(): ValidatableResult
}
