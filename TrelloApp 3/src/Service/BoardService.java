package Service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Controller.BoardController;
import Model.Board;

import Model.User;


@Path("/boards")
public class BoardService {


    @Inject
    private BoardController boardcontroller;

    @POST
    @Path("/create/{boardName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBoard(User user, @PathParam("boardName")String boardName) {
        return boardcontroller.createBoard(user, boardName);
    }

    @POST
    @Path("/invite/{username}/{boardName}/{username2}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response inviteUser(@PathParam("username")String user,@PathParam("boardName")String board, @PathParam("username2")String invitee) {
    	return boardcontroller.inviteUser(user, board , invitee);
    }

  
    @DELETE
    @Path("/delete/{boardName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteBoard(User user, @PathParam("boardName")String board) {
        return boardcontroller.deleteBoard(user, board);
    }

    @GET
    @Path("/all/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBoards(@PathParam("username") String username) {
        List<Board> boards = boardcontroller.getAllBoards(username);
        if (boards.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No boards found or user not found").build();
        }
        return Response.ok().entity(boards).build();
    }

}