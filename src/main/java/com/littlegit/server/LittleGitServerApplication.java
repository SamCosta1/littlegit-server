package com.littlegit.server;

import com.littlegit.server.application.CorsFilter;
import com.littlegit.server.application.exception.ExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;

public class LittleGitServerApplication extends ResourceConfig {

    public LittleGitServerApplication() {
        LittleGitApplicationComponent applicationComponent = DaggerLittleGitApplicationComponent.create();

        register(applicationComponent.getUserController());
        register(applicationComponent.getAuthController());
        register(new CorsFilter());
        register(new ExceptionMapper());
        register(applicationComponent.getMoshiMessageBodyHandler());
    }
}
