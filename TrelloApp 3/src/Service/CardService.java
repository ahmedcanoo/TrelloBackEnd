package Service;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Controller.CardController;


@Path("/cards")
public class CardService {

	  @Inject
	    private CardController cardcontroller;

	  @POST
	    @Path("/create/{username}/{listName}/{cardDescription}")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response createCard(@PathParam("username")String username,@PathParam("listName") String listName, @PathParam("cardDescription")String cardDescription) {
	        return cardcontroller.createCard(username, listName, cardDescription);
	      	    }
	  

	  @POST
	    @Path("/move/{username}/{cardId}/{targetListName}")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response moveCard(@PathParam("username") String username,
	                             @PathParam("cardId") Long cardId,
	                             @PathParam("targetListName") String targetListName) {
	        return cardcontroller.moveCard(username, cardId, targetListName);
	    }

	  @PUT
	    @Path("/assign/{username}/{cardId}/{assigneeUsername}")
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Response assignCard(@PathParam("username") String username,
	                               @PathParam("cardId") Long cardId,
	                               @PathParam("assigneeUsername") String assigneeUsername) {
	        return cardcontroller.assignCard(username, cardId, assigneeUsername);
	    }
	  @PUT
	    @Path("/add/{cardId}")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response addCommentAndDescription(@PathParam("cardId") Long cardId, String requestBody) 
	    {
	        return cardcontroller.addCommentAndDescription(cardId, requestBody);
	    }
	  
	
	  
	    @PUT
	    @Path("/update/{cardId}")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response updateCard(@PathParam("cardId") Long cardId, String requestBody)
	                               {
	        return cardcontroller.updateCard( cardId, requestBody);
	    }
}