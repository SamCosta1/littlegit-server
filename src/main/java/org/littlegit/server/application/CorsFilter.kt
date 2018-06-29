package org.littlegit.server.application

import java.io.IOException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter

class CorsFilter : ContainerResponseFilter {

    @Throws(IOException::class)
    override fun filter(request: ContainerRequestContext, response: ContainerResponseContext) {
        response.headers.add("Access-Control-Allow-Origin", "http://localhost:8080")
        response.headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
        response.headers.add("Access-Control-Allow-Credentials", "true")
        response.headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
    }
}