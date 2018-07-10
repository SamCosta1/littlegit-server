package com.littlegit.server.serializatoin;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class MoshiModule {

    private Moshi _moshi = null;
    @Provides @Singleton public Moshi provideMoshi() {
        if (_moshi == null) {
            _moshi = new Moshi.Builder()
                    .add(new KotlinJsonAdapterFactory())
                    .add(new AuthRoleAdapter())
                    .add(new TokenTypeAdapter())
                    .build();
        }

        return _moshi;
    }

}
