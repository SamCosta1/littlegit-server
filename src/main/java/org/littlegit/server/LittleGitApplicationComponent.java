package org.littlegit.server;

import dagger.Component;
import org.littlegit.server.controller.UserController;

import javax.inject.Singleton;

@Component(modules = { SettingsModule.class })
@Singleton
public interface LittleGitApplicationComponent {
    UserController getUserController();
}
