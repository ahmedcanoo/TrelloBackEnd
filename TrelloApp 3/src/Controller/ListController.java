package Controller;

import Model.User;
import Model.Board;

import Model.Lists;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Stateless
public class ListController {
	
	@Inject 
	UserController usercontroller;
	
	@Inject
	BoardController boardcontroller;
	
	@Inject
	ListController listcontroller;
	
    @PersistenceContext(unitName = "TrelloPU")
    private EntityManager entityManager;
	
	 public Response createList(@PathParam("listName")String listName, @PathParam("boardName") String boardName,@PathParam("user") String user) {
			User owner = usercontroller.getUserByUsername(user);
		 if (owner == null) 
		 {
    		 return Response.status(Response.Status.NOT_FOUND).entity("User does not existt.").build();
    } 
		    Board boardn = boardcontroller.getBoardByName(boardName);
		    if (boardn == null) {
		        return Response.status(Response.Status.NOT_FOUND).entity("Board does not exist.").build();
		    }
		    if (!boardn.getOwner().equals(owner)) {
		        return Response.status(Response.Status.FORBIDDEN).entity("User does not have access to the specified board.").build();
		    }
		   Lists list = new Lists();
		    list.setOwner(owner); 
		    list.setListname(listName);
		    list.setBoard(boardn);
		    entityManager.persist(list);

		    return Response.status(Response.Status.CREATED).entity("List created successfully.").build();
		}
	 
	 public Response deleteList(@PathParam("listName") String Listname, @PathParam("user") String user) {
		    User requestingUser = usercontroller.getUserByUsername(user);
		    if (requestingUser == null) {
		        return Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build();
		    }

		    Lists list = listcontroller.findListByName(Listname);
		    if (list == null) {
		        return Response.status(Response.Status.NOT_FOUND).entity("List does not exist.").build();
		    }

		    if (!list.getOwner().equals(requestingUser) && !list.getCollaborators().contains(requestingUser)) {
		        return Response.status(Response.Status.FORBIDDEN).entity("User does not have permission to delete this list.").build();
		    }

		    entityManager.remove(list);
		    entityManager.flush(); 
		    return Response.status(Response.Status.OK).entity("List deleted successfully.").build();
		}

	
	    public Lists findListByName(String Listname) {
	        try {
	            return entityManager.createQuery("SELECT l FROM Lists l WHERE l.Listname = :Listname", Lists.class)
	                                .setParameter("Listname", Listname)
	                                .getSingleResult();
	        } catch (NoResultException e) {
	            return null; 
	        }
	    }
	    
	    
}