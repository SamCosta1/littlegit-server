package com.littlegit.server.model

data class ValidatableResult(val isValid: Boolean, val invalidMessages: List<String>)

class InvalidModelException(val result: ValidatableResult): Exception()

interface Validatable {
    fun validate(): ValidatableResult
}
