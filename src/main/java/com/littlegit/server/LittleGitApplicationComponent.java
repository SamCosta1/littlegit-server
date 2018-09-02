package com.littlegit.server;

import com.littlegit.server.application.MoshiMessageBodyHandler;
import com.littlegit.server.authfilter.AuthFilter;
import com.littlegit.server.controller.AuthController;
import com.littlegit.server.controller.RepoController;
import com.littlegit.server.controller.UserController;
import com.littlegit.server.serializatoin.MoshiModule;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = { MoshiModule.class })
@Singleton
public interface LittleGitApplicationComponent {
    UserController getUserController();
    RepoController getRepoController();
    AuthController getAuthController();
    MoshiMessageBodyHandler getMoshiMessageBodyHandler();
    AuthFilter getAuthFilter();
}
