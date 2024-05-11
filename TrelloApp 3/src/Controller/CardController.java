package Controller;
import Model.User;
import Model.Card;
import Model.Lists;

import java.io.StringReader;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import Messaging.JMSClient;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class CardController {
	
    @PersistenceContext(unitName = "TrelloPU")
    private EntityManager entityManager;
	
    @Inject
    UserController usercontroller;
    
    @Inject
    ListController listcontroller;
   
    @Inject
    JMSClient jmsUtil;

    
    public Response createCard(@PathParam("username")String user,@PathParam("Listname") String Listname, @PathParam("cardDescription")String cardDescription) {
        User currentUser = usercontroller.getUserByUsername(user);
        if (currentUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build();
        }
        Lists list = findListByName(Listname); 

        if (list == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("List does not exist.").build();
        }

        if (!list.getOwner().equals(currentUser) && !list.getCollaborators().contains(currentUser)) {
            return Response.status(Response.Status.FORBIDDEN).entity("User does not have permission to create a card in this list.").build();
        }
        Card card = new Card(cardDescription);
        card.setOwner(currentUser);
        card.setList(list);
        list.addCard(card);
        entityManager.persist(card);
        return Response.status(Response.Status.CREATED).entity("Card created successfullly").build();

    }
    public Response moveCard(@PathParam("username") String username, @PathParam("cardId") Long cardId, @PathParam("targetListName") String targetListName) {
        User collaborator = usercontroller.getUserByUsername(username);
        if (collaborator == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build();
        }
        
        Card card = entityManager.find(Card.class, cardId);
        if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Card does not exist.").build();
        }
        
        entityManager.refresh(card); 

        Lists currentList = card.getList();
        entityManager.refresh(currentList); 
        
        if (!currentList.getCards().contains(card)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Card is not within the current list.").build();
        }

        if (!currentList.getCollaborators().contains(collaborator) && !collaborator.equals(currentList.getOwner())) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not a collaborator or the owner of the current list.").build();
        }

        Lists targetList = listcontroller.findListByName(targetListName);
        if (targetList == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Target list does not exist.").build();
        }

        if (!targetList.getCollaborators().contains(collaborator) && !collaborator.equals(targetList.getOwner())) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not a collaborator of the target list.").build();
        }

        card.setList(targetList);
        entityManager.merge(card);
        
        return Response.ok("Card moved successfully to " + targetList.getListname() + " list.").build();
    }
   
    public Response assignCard(@PathParam("username") String username, @PathParam("cardId") Long cardId, @PathParam("assigneeUsername") String assigneeUsername) {
        User collaborator = usercontroller.getUserByUsername(username);
        if (collaborator == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User does not exist.").build();
        }
        
        Card card = entityManager.find(Card.class, cardId);
        if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Card does not exist.").build();
        }
        entityManager.refresh(card);

        Lists currentList = card.getList();
        entityManager.refresh(currentList);

        if (!currentList.getCards().contains(card)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Card is not within the current list.").build();
        }

        if (!currentList.getCollaborators().contains(collaborator) && !collaborator.equals(currentList.getOwner())) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not a collaborator or the owner of the current list.").build();
        }
     

        User assignee = null;
        if (assigneeUsername != null && !assigneeUsername.isEmpty()) {
            assignee = usercontroller.getUserByUsername(assigneeUsername);
            if (assignee == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Assignee does not exist.").build();
            }
            card.setOwner(assignee);
        } else {
            card.setOwner(collaborator);
        }
        
        entityManager.merge(card);
        String assigneeName = assignee != null ? assignee.getUsername() : collaborator.getUsername();
        return Response.ok("Card assigned successfully to " + assigneeName + ".").build();

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

    public Response addCommentAndDescription(@PathParam("cardId") Long cardId, String requestBody) {
        Card card = entityManager.find(Card.class, cardId);
        if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Card not found.").build();
        }

        JsonObject jsonObject;
        try {
            jsonObject = Json.createReader(new StringReader(requestBody)).readObject();
        } catch (JsonParsingException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid JSON format.").build();
        }

        String newDescription = jsonObject.getString("newDescription", "");
        String comment = jsonObject.getString("comment", "");

        if (!comment.isEmpty()) {
            card.setComment(comment); 
            jmsUtil.sendMessage("Comment added" );

        }
        if (!newDescription.isEmpty()) {
            card.setDescription(newDescription);
            jmsUtil.sendMessage("Description added" );

        }

        entityManager.merge(card);
        return Response.status(Response.Status.OK).entity("Comment and description added successfully.").build();
    }
   
    public Response updateCard(@PathParam("cardId") Long cardId, String requestBody) {
        if (cardId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Card ID cannot be null.").build();
        }

        Card card = entityManager.find(Card.class, cardId);
        if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Card not found.").build();
        }

        JsonObject jsonObject;
        try (JsonReader reader = Json.createReader(new StringReader(requestBody))) {
            jsonObject = reader.readObject();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid JSON format.").build();
        }

        String updatedDescription = jsonObject.getString("updatedDescription", "");
        String updatedComment = jsonObject.getString("updatedComment", "");

        if (!updatedComment.isEmpty()) {
            card.addComment(updatedComment); 
        }

        if (!updatedDescription.isEmpty()) {
            card.setDescription(updatedDescription);
        }

        entityManager.merge(card);
        entityManager.flush();  

        jmsUtil.sendMessage("Card updated" );
        return Response.status(Response.Status.OK).entity("Card updated successfully.").build();
    }

}