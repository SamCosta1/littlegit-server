package org.littlegit.server.controller

import org.littlegit.server.model.User
import org.littlegit.server.service.UserService
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
class UserController @Inject constructor(private val userService: UserService) {

    @GET
    @Path("/{id}")
    fun getUser(@PathParam("id") id: Int): User {
        return User("email@email", "john", "smith")

    }
}