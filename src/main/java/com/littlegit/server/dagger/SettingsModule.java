package com.littlegit.server.dagger;

import com.littlegit.server.application.LittleGitSettings;
import dagger.Module;
import dagger.Provides;
import com.littlegit.server.application.LittleGitSettings;

import javax.inject.Singleton;

@Module
public class SettingsModule {
    @Provides @Singleton
    LittleGitSettings provideLittleGitSettings() {
        return new LittleGitSettings();
    }
}
