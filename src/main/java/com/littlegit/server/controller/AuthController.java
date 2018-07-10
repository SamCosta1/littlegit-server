package com.littlegit.server.controller;

import com.littlegit.server.model.user.LoginModel;
import com.littlegit.server.model.user.LoginResponseModel;
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
    @Path("/login")
    public LoginResponseModel login(LoginModel loginDetails) {
        return authService.login(loginDetails);
    }

}