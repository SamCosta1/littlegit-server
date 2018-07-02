package com.littlegit.server;

import com.littlegit.server.application.CorsFilter;
import com.littlegit.server.application.ExceptionMapper;
import com.littlegit.server.controller.UserResource;
import org.glassfish.jersey.server.ResourceConfig;

public class LittleGitServerApplication extends ResourceConfig {

    public LittleGitServerApplication() {
        LittleGitApplicationComponent applicationComponent = DaggerLittleGitApplicationComponent.create();

        register(applicationComponent.getUserController());
        register(new CorsFilter());
        register(new ExceptionMapper());
        register(applicationComponent.getMoshiMessageBodyHandler());
    }
}
