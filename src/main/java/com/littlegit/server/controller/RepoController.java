package com.littlegit.server.controller;

import com.littlegit.server.authfilter.Secured;
import com.littlegit.server.model.repo.CreateRepoModel;
import com.littlegit.server.model.repo.Repo;
import com.littlegit.server.model.repo.RepoSummary;
import com.littlegit.server.model.user.AuthRole;
import com.littlegit.server.model.user.User;
import com.littlegit.server.service.RepoService;
import com.littlegit.server.util.CastingUtilsKt;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("/repo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class RepoController {

    private RepoService repoService;

    @Inject
    public RepoController(RepoService repoService) { this.repoService = repoService; }

    public RepoController() {}

    @POST
    @Path("/create")
    @Secured({ AuthRole.Admin, AuthRole.OrganizationAdmin, AuthRole.BasicUser})
    public RepoSummary createRepo(CreateRepoModel createModel,
                                  @Context SecurityContext context) {

        return repoService.createRepo(CastingUtilsKt.asUser(context.getUserPrincipal()), createModel);
    }

    @GET
    @Path("/check-user-access")
    @Secured({ AuthRole.GitServer })
    public Boolean getRepoAccessStatusBoolean(@QueryParam("userId") int userId, @QueryParam("repoPath") String path) {
        return repoService.getRepoAccessStatusBoolean(userId, path);
    }

    @GET
    @Path("/repos")
    @Secured({ AuthRole.Admin, AuthRole.OrganizationAdmin, AuthRole.BasicUser})
    public List<RepoSummary> getReposForUser(@Context SecurityContext context) {
        return repoService.getReposForUser(CastingUtilsKt.asUser(context.getUserPrincipal()));
    }

}
