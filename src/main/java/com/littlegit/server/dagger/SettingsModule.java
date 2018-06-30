package com.littlegit.server.dagger;

import com.littlegit.server.application.settings.LittleGitSettings;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class SettingsModule {

    private static LittleGitSettings settings;

    @Provides @Singleton
    LittleGitSettings provideLittleGitSettings() {
        return settings == null ? settings = LittleGitSettings.parseSettings() : settings;
    }
}
