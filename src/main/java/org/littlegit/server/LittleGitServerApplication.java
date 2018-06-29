package org.littlegit.server;

import org.glassfish.jersey.server.ResourceConfig;
import org.littlegit.server.application.CorsFilter;
import org.littlegit.server.application.MoshMessageBodyHandler;

import org.littlegit.server.application.ExceptionMapper;

public class LittleGitServerApplication extends ResourceConfig {

    public LittleGitServerApplication() {
        LittleGitApplicationComponent applicationComponent = DaggerLittleGitApplicationComponent.create();
        register(applicationComponent.getUserController());
        register(new CorsFilter());
        register(new ExceptionMapper());
        register(new MoshMessageBodyHandler());
    }
}
