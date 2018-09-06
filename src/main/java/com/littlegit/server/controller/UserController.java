package com.littlegit.server.controller;

import com.littlegit.server.authfilter.Secured;
import com.littlegit.server.model.user.AuthRole;
import com.littlegit.server.model.user.CreateSshKeyModel;
import com.littlegit.server.model.user.SignupModel;
import com.littlegit.server.model.user.User;
import com.littlegit.server.service.UserService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.littlegit.server.util.CastingUtilsKt;

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
    public User getUser(@PathParam("id") int id,
                        @Context SecurityContext context) {

        return userService.getUser(CastingUtilsKt.asUser(context.getUserPrincipal()), id);
    }

    @POST
    @Path("/signup")
    public void signup(SignupModel signupModel) {
        this.userService.createUser(signupModel);
    }

    @POST
    @Path("/add-ssh-key")
    @Secured({ AuthRole.Admin, AuthRole.OrganizationAdmin, AuthRole.BasicUser})
    public void addSshKey(@Context SecurityContext context, CreateSshKeyModel model) {
        this.userService.addSshKeyToUser(CastingUtilsKt.asUser(context.getUserPrincipal()), model);
    }
}