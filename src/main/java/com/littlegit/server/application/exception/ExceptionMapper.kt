package com.littlegit.server.application.exception

import com.littlegit.server.application.settings.SettingsProvider
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class ExceptionMapper: Exception(), ExceptionMapper<Throwable> {

    data class ErrorResponse(val rawMessage: String, val localisedMessage: String, var notes: String? = null)

    override fun toResponse(throwable: Throwable?): Response {

        var status = 500
        var errorResponse = ErrorResponse("Server dead", "Something went wrong")

        when (throwable) {
            is IllegalArgumentException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request", "")
            }
        }

        if (SettingsProvider.isDebugMode) {
            errorResponse.notes = throwable.toString()
        }

        throwable?.printStackTrace()
        return Response.status(status).entity(errorResponse).type(MediaType.APPLICATION_JSON).build()
    }

}