package com.littlegit.server.controller;

import com.littlegit.server.model.SignupModel;
import com.littlegit.server.service.AuthService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class AuthController {

    private AuthService authService;

    @Inject AuthController(AuthService authService) {
        this.authService = authService;
    }

    public AuthController(){}

    @POST
    @Path("/signup")
    public void signup(SignupModel signupModel) {
        this.authService.signup(signupModel);
    }
}
