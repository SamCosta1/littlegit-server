package com.littlegit.server;

import com.littlegit.server.application.CorsFilter;
import com.littlegit.server.application.exception.ExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;

public class LittleGitServerApplication extends ResourceConfig {

    private static LittleGitApplicationComponent applicationComponent;
    static {

        applicationComponent = DaggerLittleGitApplicationComponent
                .builder()
                .build();
    }

    public LittleGitServerApplication() {
        register(applicationComponent.getUserController());
        register(applicationComponent.getAuthController());
        register(applicationComponent.getRepoController());
        register(applicationComponent.getAuthFilter());
        register(new CorsFilter());
        register(new ExceptionMapper());
        register(applicationComponent.getMoshiMessageBodyHandler());
    }
}
