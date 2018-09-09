package com.littlegit.server.application.exception

import com.littlegit.server.model.i18n.LocalizableString
import com.littlegit.server.model.user.User
import org.littlegit.core.model.GitError
import org.littlegit.core.shell.ShellResult
import java.lang.RuntimeException
import kotlin.reflect.KClass

data class EmailInUseException(val email: String) : ValueInUseException(LocalizableString.EmailInUse, email)
data class UsernameInUseException(val username: String) : ValueInUseException(LocalizableString.UsernameInUse, username)
open class ValueInUseException( val localizableString: LocalizableString, val value: String ): Throwable()

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

data class LittleGitCommandFailedException(val err: GitError): Throwable()

data class RemoteRunnerException(val err: ShellResult.Error): Throwable()

data class DuplicateRecordException(val clazz: Class<*>): Throwable()