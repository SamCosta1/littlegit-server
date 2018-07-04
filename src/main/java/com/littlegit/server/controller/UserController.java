package com.littlegit.server.controller;

import com.littlegit.server.model.SignupModel;
import com.littlegit.server.model.User;
import com.littlegit.server.service.UserService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class UserController {

    private UserService userService;

    public UserController() {}

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") int id) {
        return userService.getUser(id);
    }

    @POST
    @Path("/signup")
    public void signup(SignupModel signupModel) {
        this.userService.createUser(signupModel);
    }
}