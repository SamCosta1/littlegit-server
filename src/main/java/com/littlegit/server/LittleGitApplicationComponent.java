package com.littlegit.server;

import com.littlegit.server.controller.UserResource;
import dagger.Component;
import com.littlegit.server.dagger.SettingsModule;

import javax.inject.Singleton;

@Component(modules = { SettingsModule.class })
@Singleton
public interface LittleGitApplicationComponent {
    UserResource getUserController();
}
