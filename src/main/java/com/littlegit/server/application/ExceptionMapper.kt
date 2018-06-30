package com.littlegit.server.application

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class ExceptionMapper : Exception(), ExceptionMapper<Throwable> {

    data class ErrorResponse(var rawMessage: String, var localisedMessage: String)

    override fun toResponse(throwable: Throwable?): Response {

        val status = 500
        val errorResponse = ErrorResponse("Server dead", "Something went wrong")

        throwable?.printStackTrace()
        return Response.status(status).entity(errorResponse).type(MediaType.APPLICATION_JSON).build()
    }

}