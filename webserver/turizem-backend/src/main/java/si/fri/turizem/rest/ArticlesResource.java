package si.fri.turizem.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import si.fri.turizem.util.RestUtils;
import si.fri.turizem.util.ScopusClientUtil;

@Path("articles")
@RequestScoped
@Log(LogParams.METRICS)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticlesResource {
    private static final Logger LOG = LogManager.getLogger(ScopusClientUtil.class.getName());

    @Inject
    private RestUtils restUtils;

    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getArticles(@FormDataParam("query") String query){
        if(query != null && !query.isEmpty())
            return restUtils.response(ScopusClientUtil.getArticlesList(query), Response.Status.OK);
        else
            throw new RuntimeException("Search query can not be null or empty");

    }

    @GET
    @Path("{id}")
    public Response getArticleById(@PathParam("id") Integer articleId){
        //ToDo: get specific article from DB (JSON)

        return null;
    }

    @GET
    @Path("full/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getFullArticleById(@PathParam("id") Integer articleId){
        //ToDo: get specific article from DB (PDF)

        return null;
    }

    @POST
    public Response createArticle(){
        //ToDo: create article and persit in DB

        return null;
    }

    @PUT
    @Path("{id}")
    public Response updateArticle(@PathParam("id") Integer articleId){
        //ToDo: update and persist article in DB

        return null;
    }

    @DELETE
    @Path("{id}")
    public Response deleteArticle(@PathParam("id") Integer articleId){
        //ToDo: delete article from DB

        return null;
    }



}
