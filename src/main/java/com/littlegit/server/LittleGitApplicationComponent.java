package com.littlegit.server;

import com.littlegit.server.application.MoshiMessageBodyHandler;
import com.littlegit.server.controller.UserResource;
import com.littlegit.server.dagger.MoshiModule;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = { MoshiModule.class })
@Singleton
public interface LittleGitApplicationComponent {
    UserResource getUserController();
    MoshiMessageBodyHandler getMoshiMessageBodyHandler();
}
