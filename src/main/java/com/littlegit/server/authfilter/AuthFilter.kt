package com.littlegit.server.authfilter

import com.littlegit.server.application.exception.UserForbiddenException
import com.littlegit.server.application.exception.UserUnauthorizedException
import com.littlegit.server.model.user.AuthRole
import com.littlegit.server.service.AuthService
import java.io.IOException
import java.lang.reflect.AnnotatedElement
import java.security.Principal
import java.util.*
import javax.annotation.Priority
import javax.inject.Inject
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.SecurityContext


@Secured
@Priority(Priorities.AUTHORIZATION)
open class AuthFilter @Inject
constructor(private val authService: AuthService) : ContainerRequestFilter {

    @Context
    private lateinit var resourceInfo: ResourceInfo

    private val allowedRoles: List<AuthRole>
        get() {

            val resourceClass = resourceInfo.resourceClass
            val classRoles = extractRoles(resourceClass)

            val resourceMethod = resourceInfo.resourceMethod
            val methodRoles = extractRoles(resourceMethod)
            return if (methodRoles.isEmpty()) {
                classRoles
            } else {
                methodRoles
            }
        }

    @Throws(IOException::class)
    override fun filter(requestContext: ContainerRequestContext) {
        val authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)

        if (authHeader?.startsWith(AuthConstants.AuthScheme) == true) {

            val token = authHeader.removePrefix("${AuthConstants.AuthScheme} ")
            val user = authService.getUserForToken(token)

            if (!user.hasAnyRoleOf(allowedRoles)) {
                throw UserForbiddenException()
            }

            requestContext.securityContext = object: SecurityContext {
                override fun isUserInRole(p0: String?) = true
                override fun getAuthenticationScheme(): String = AuthConstants.AuthScheme
                override fun getUserPrincipal(): Principal = user
                override fun isSecure(): Boolean = requestContext.securityContext.isSecure
            }
        } else {
            throw UserUnauthorizedException()
        }
    }

    // Extract the roles from the annotated element
    private fun extractRoles(annotatedElement: AnnotatedElement?): List<AuthRole> {
        return if (annotatedElement == null) {
            ArrayList()
        } else {
            val secured = annotatedElement.getAnnotation(Secured::class.java)
            if (secured == null) {
                ArrayList()
            } else {

                val allowedRoles = secured.value
                Arrays.asList(*allowedRoles)
            }
        }
    }
}