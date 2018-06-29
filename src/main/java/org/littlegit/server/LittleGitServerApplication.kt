package org.littlegit.server

import org.glassfish.jersey.server.ResourceConfig
import org.littlegit.server.application.CorsFilter
import org.littlegit.server.application.ExceptionMapper
import org.littlegit.server.application.MoshMessageBodyHandler

class LittleGitServerApplication: ResourceConfig() {

    init {
       // val applicationComponent = DaggerLittleGitApplicationComponent.create()
       // register(applicationComponent.userController)
        register(CorsFilter())
        register(ExceptionMapper())
        register(MoshMessageBodyHandler())
    }

}