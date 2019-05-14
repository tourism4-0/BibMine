package si.fri.turizem.rest;

import si.fri.turizem.util.ScopusClientUtil;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("queries")

public class QueriesResource {

    @GET
    public Response getQueries(){
        //ToDo: get All query from DB

        return null;
    }

    @GET
    @Path("{id}")
    public Response getQueryId(@PathParam("id") Integer queryId){
        //ToDo: get specific query from DB

        return null;
    }

    @POST
    public Response createQuery(){
        //ToDo: create query and persit in DB

        return null;
    }

    @PUT
    @Path("{id}")
    public Response updateQuery(@PathParam("id") Integer queryId){
        //ToDo: update and persist query in DB

        return null;
    }

    @DELETE
    @Path("{id}")
    public Response deleteQuery(@PathParam("id") Integer queryId){
        //ToDo: delete query from DB

        return null;
    }
}
