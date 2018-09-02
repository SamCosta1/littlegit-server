package com.littlegit.server.application.exception

import com.littlegit.server.model.user.User
import org.littlegit.core.model.GitError
import java.lang.RuntimeException
import kotlin.reflect.KClass

data class EmailInUseException(val email: String) : Throwable()

data class NoSuchEnumValueException(val code: Any) : Exception()

class UserUnauthorizedException(private val msg: String = ""): RuntimeException() {

    override fun toString(): String {
        return msg + "  " + super.toString()
    }
}

class InvalidTokenException: RuntimeException()

data class NotFoundException(val type: KClass<*>): RuntimeException()

class UserForbiddenException: RuntimeException()

class ProgrammerError: RuntimeException()

class LittleGitCommandFailedException(err: GitError): Throwable()