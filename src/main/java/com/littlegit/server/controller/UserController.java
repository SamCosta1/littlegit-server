package com.littlegit.server.controller;

import com.littlegit.server.authfilter.Secured;
import com.littlegit.server.model.user.AuthRole;
import com.littlegit.server.model.user.SignupModel;
import com.littlegit.server.model.user.User;
import com.littlegit.server.service.UserService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    @Secured({ AuthRole.Admin, AuthRole.OrganizationAdmin, AuthRole.BasicUser})
    public User getUser(@PathParam("id") int id) {
        return userService.getUser(id);
    }

    @POST
    @Path("/signup")
    public void signup(SignupModel signupModel) {
        this.userService.createUser(signupModel);
    }
}