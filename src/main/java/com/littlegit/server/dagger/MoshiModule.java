package com.littlegit.server.dagger;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class MoshiModule {

    @Provides @Singleton Moshi provideMoshi() {
        return new Moshi.Builder().add(new KotlinJsonAdapterFactory()).build();
    }

}
