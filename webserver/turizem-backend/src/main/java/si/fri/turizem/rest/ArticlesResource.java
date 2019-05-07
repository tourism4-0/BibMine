package si.fri.turizem.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("articles")

public class ArticlesResource {

    @GET
    public Response getArticles(){
        //ToDo: get All Articles from DB

        return null;
    }

    @GET
    @Path("{id}")
    public Response getArticleById(@PathParam("id") Integer articleId){
        //ToDo: get specific article from DB (JSON)

        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("full/{id}")
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
