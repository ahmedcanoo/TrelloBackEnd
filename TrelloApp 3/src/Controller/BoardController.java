package Controller;

import Model.User;
import Model.Board;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

@Stateless
public class BoardController {
	
	@PersistenceContext(unitName="TrelloPU")
    private EntityManager entityManager;
	
	@Inject
	private UserController usercontroller;
	

	
	public Response createBoard(User user, @PathParam("boardName")String boardName) 
	{
		User owner = usercontroller.getUserByUsername(user.getUsername());
		if (owner == null) {
	    		 return Response.status(Response.Status.NOT_FOUND).entity("User does not existtttt.").build();
	    } 
		if (boardName == null || boardName.trim().isEmpty()) {
		    return Response.status(Response.Status.BAD_REQUEST).entity("Board name is required.").build();
		}

		else if (!owner.getRole().equals("Team Leader")) {
	        return Response.status(Response.Status.FORBIDDEN).entity("Only team leaders can create boards").build();
	    } 
		
		Board existingBoard = getBoardByName(boardName);
		if (existingBoard != null) {
			return Response.status(Response.Status.CONFLICT).entity("A board with the same name already exists.").build();
		}
		
		else {
	    	Board board = new Board(boardName, owner);
	    	board.setOwner(owner);
	    	owner.getOwnedBoards().add(board); 
	    	entityManager.persist(board);
	    	return Response.status(Response.Status.CREATED).entity("Board created successfully").build();
	    }
	}
	


    public Response inviteUser(@PathParam("username")String user,@PathParam("boardName")String boardName, @PathParam("username2")String invitee) {
	    User inviter = usercontroller.getUserByUsername(user);
	    if (inviter == null) {
	        return Response.status(Response.Status.NOT_FOUND).entity("Inviter does not exist.").build();
	    }

	    Board board = getBoardByName(boardName);
	    if (board == null) {
	        return Response.status(Response.Status.NOT_FOUND).entity("Board does not exist.").build();
	    }

	    if (!inviter.equals(board.getOwner())) {
	        return Response.status(Response.Status.FORBIDDEN).entity("Only the board owner can invite users.").build();
	    }

	    User collab = usercontroller.getUserByUsername(invitee);
	    if (collab == null) {
	        return Response.status(Response.Status.NOT_FOUND).entity("Invitee does not exist.").build();
	    }

	    if (board.getCollaborators().contains(collab)) {
	        return Response.status(Response.Status.BAD_REQUEST).entity("Invitee is already a collaborator.").build();
	    }
	    
	    board.addCollaborator(collab);
	    entityManager.merge(board);

	    return Response.ok("User invited successfully.").build();
	}

    public Response deleteBoard(User user, @PathParam("boardName") String boardName) {
        User owner = usercontroller.getUserByUsername(user.getUsername());
        if (owner == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build();
        }

        if (boardName == null || boardName.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Board name is required.").build();
        }

        Board board = getBoardByName(boardName);
        if (board == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Board does not exist.").build();
        }

   
        if (!board.getOwner().equals(owner) || !"Team Leader".equals(owner.getRole())) {
            return Response.status(Response.Status.FORBIDDEN).entity("Only the board owner who is a Team Leader can delete the board.").build();
        }
        entityManager.remove(board);
        return Response.ok("Board '" + boardName + "' deleted successfully.").build();
    }

    public List<Board> getAllBoards(@PathParam("username")String username) {
        User user = usercontroller.getUserByUsername(username);
        if (user == null) 
        {
            return Collections.emptyList();
        }
        Query query = entityManager.createQuery("SELECT b FROM Board b WHERE b.owner = :user ", Board.class);
        query.setParameter("user", user);
        return query.getResultList();
    }
    
    
    
    public Board getBoardByName(String boardName) {
        try {
            Query query = entityManager.createQuery("SELECT b FROM Board b WHERE b.boardName = :boardName");
            query.setParameter("boardName", boardName);
            return (Board) query.getSingleResult();
        } catch (NoResultException e) {
            return null; 
        }
    }
}