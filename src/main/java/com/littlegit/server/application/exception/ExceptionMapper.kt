package com.littlegit.server.application.exception

import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.InvalidModelException
import com.littlegit.server.model.i18n.LocalizableString
import com.squareup.moshi.JsonDataException
import java.lang.IllegalArgumentException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import java.io.PrintWriter
import java.io.StringWriter

@Provider
class ExceptionMapper: Exception(), ExceptionMapper<Throwable> {

    data class ErrorResponse(val rawMessage: String, val localisedMessage: List<LocalizableString>, var notes: List<String> = emptyList()) {

        constructor(rawMessage: String, localisedMessage: LocalizableString? = null, notes: String? =  null)
                :
                this(rawMessage,
                     if (localisedMessage == null) emptyList() else listOf(localisedMessage),
                     if (notes == null) emptyList() else listOf(notes)
                )
    }

    override fun toResponse(throwable: Throwable?): Response {

        var status = 500
        var errorResponse = ErrorResponse("Server dead", LocalizableString.Response500Body)

        when (throwable) {
            is UserForbiddenException -> {
                status = 403
                errorResponse = ErrorResponse("Forbidden")
            }
            is NotFoundException -> {
                status = 404
                errorResponse = ErrorResponse("Not found")
            }
            is UserUnauthorizedException,
            is InvalidTokenException -> {
                status = 401
                errorResponse = ErrorResponse("Unauthorized")
            }
            is IllegalArgumentException,
            is JsonDataException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request")
            }
            is NoSuchEnumValueException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request", notes = "Enum value ${throwable.code} doesn't exist")
            }
            is InvalidModelException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request", throwable.result.invalidMessages)
            }
            is ValueInUseException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request", throwable.localizableString, "${throwable.value} already in use")
            }
            is DuplicateRecordException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request", LocalizableString.ValueAlreadyExists, throwable.clazz.simpleName)
            }
        }

        if (SettingsProvider.isDebugMode) {
            val notes = errorResponse.notes.toMutableList()

            val sw = StringWriter()
            throwable?.printStackTrace(PrintWriter(sw))
            notes.add(sw.toString())

            errorResponse.notes = notes
        }

        throwable?.printStackTrace()
        return Response.status(status).entity(errorResponse).type(MediaType.APPLICATION_JSON).build()
    }
}