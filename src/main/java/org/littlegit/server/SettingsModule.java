package org.littlegit.server;

import dagger.Module;
import dagger.Provides;
import org.littlegit.server.application.LittleGitSettings;

import javax.inject.Singleton;

@Module
public class SettingsModule {
    @Provides
    @Singleton
    public LittleGitSettings provideLittleGitSettings() {
        return new LittleGitSettings();
    }
}
