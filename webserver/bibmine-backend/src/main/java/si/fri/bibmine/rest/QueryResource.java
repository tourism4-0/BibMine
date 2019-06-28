package si.fri.bibmine.rest;

import si.fri.bibmine.beans.entity.QueryEntityBean;
import si.fri.bibmine.models.Query;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("queries")
public class QueryResource {

    @Inject
    private QueryEntityBean queryEntityBean;

    @Inject
    private RestUtils restUtils;

    @GET
    @RolesAllowed("user")
    public Response getQueries() {
        List<Query> queries = queryEntityBean.getAllQueries();
        return restUtils.response(queries, Response.Status.OK);
    }

    @GET
    @Path("{query}")
    @RolesAllowed("user")
    public Response getArticleById(@QueryParam("query") String q){
        Query query = queryEntityBean.getQuery(q);
        return restUtils.response(query, Response.Status.OK);
    }


    @DELETE
    @Path("{query}")
    @RolesAllowed("user")
    public Response deleteQuery(@PathParam("query") String query){
        queryEntityBean.deleteQuery(query);
        return restUtils.response(Response.Status.NO_CONTENT);
    }
}
