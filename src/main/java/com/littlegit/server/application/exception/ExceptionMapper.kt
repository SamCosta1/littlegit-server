package com.littlegit.server.application.exception

import com.littlegit.server.application.settings.SettingsProvider
import com.littlegit.server.model.InvalidModelException
import java.lang.IllegalArgumentException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import java.io.PrintWriter
import java.io.StringWriter



@Provider
class ExceptionMapper: Exception(), ExceptionMapper<Throwable> {

    data class ErrorResponse(val rawMessage: String, val localisedMessage: String, var notes: List<String>? = null)

    override fun toResponse(throwable: Throwable?): Response {

        var status = 500
        var errorResponse = ErrorResponse("Server dead", "Something went wrong")

        when (throwable) {
            is IllegalArgumentException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request", "")
            }
            is InvalidModelException -> {
                status = 400
                errorResponse = ErrorResponse("Bad Request", "", throwable.result.invalidMessages)
            }
        }

        if (SettingsProvider.isDebugMode) {
            val notes = errorResponse.notes?.toMutableList() ?: mutableListOf()

            val sw = StringWriter()
            throwable?.printStackTrace(PrintWriter(sw))
            notes.add(sw.toString())

            errorResponse.notes = notes
        }

        throwable?.printStackTrace()
        return Response.status(status).entity(errorResponse).type(MediaType.APPLICATION_JSON).build()
    }

}