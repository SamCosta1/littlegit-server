package com.littlegit.server.controller;

import com.littlegit.server.model.User;
import com.littlegit.server.service.UserService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class UserResource {

    private UserService userService;

    public UserResource() {}

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") int id) {
        return userService.getUser(id);

    }
}