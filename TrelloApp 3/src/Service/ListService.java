package Service;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import Controller.ListController;



@Path("/lists")
public class ListService {

    @Inject
    private ListController listcontroller;

    @PersistenceContext(unitName = "TrelloPU")
    private EntityManager entityManager;
    
    @POST
    @Path("/create/{listName}/{boardName}/{user}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	 public Response createList(@PathParam("listName")String listName, @PathParam("boardName") String boardName,@PathParam("user") String user) {
        try {
            return listcontroller.createList(listName, boardName, user);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create list").build();
        }
    }

   
    @DELETE
    @Path("/delete/{listName}/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteList(@PathParam("listName") String Listname, @PathParam("user") String user) {
        try {
            return listcontroller.deleteList(Listname, user);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete list: " + e.getMessage()).build();
        }
    }

 

}