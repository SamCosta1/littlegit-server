package com.littlegit.server.authfilter;

import com.littlegit.server.model.user.AuthRole;
import com.littlegit.server.service.AuthService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Secured
@Priority(Priorities.AUTHORIZATION)
public class AuthFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;
    private AuthService authService;

    @Inject
    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        List<AuthRole> allowedRoles = getAllowedRoles();
        checkPermissions(allowedRoles);
    }

    private List<AuthRole> getAllowedRoles() {
        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<AuthRole> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<AuthRole> methodRoles = extractRoles(resourceMethod);

        // Check if the user is allowed to execute the method
        // The method annotations override the class annotations
        if (methodRoles.isEmpty()) {
            return classRoles;
        } else {
            return methodRoles;
        }
    }
    // Extract the roles from the annotated element
    private List<AuthRole> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<>();
            } else {
                AuthRole[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private void checkPermissions(List<AuthRole> allowedRoles) {
        System.out.println(allowedRoles);
    }
}