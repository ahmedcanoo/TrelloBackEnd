package Controller;

import Model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Response;

@Stateless
public class UserController {
	
	@PersistenceContext(unitName="TrelloPU")
    private EntityManager entityManager;
	
	public Response registerUser(User user) {
	    if (getUserByUsername(user.getUsername()) != null) {
	        return Response.ok("User already exists.").build();
	    }
	    entityManager.persist(user);
        return Response.ok("User registered successfully.").build();

	}
	
	public Response login(User user) {
	    User storedUser = getUserByUsername(user.getUsername());
	    if (storedUser != null) {
	        if (user.getPassword().equals(storedUser.getPassword())) {
	            if (user.getUsername().equals(storedUser.getUsername())) {
	                if (user.getEmail().equals(storedUser.getEmail())) {
	                    if (user.getRole().equals(storedUser.getRole())) {
	                        return Response.ok("User logged in successfully.").build();
	                    } else {
	                        return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect role.").build();
	                    }
	                } else {
	                    return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect email.").build();
	                }
	            } else {
	                return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect username.").build();
	            }
	        } else {
	            return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect password.").build();
	        }
	    } else {
	        return Response.status(Response.Status.UNAUTHORIZED).entity("User does not exist.").build();
	    }
	}

	
	public Response update(User user) {
	    
	    if (getUserByUsername(user.getUsername()) != null) {
	    	user.setEmail(user.getEmail());
		    user.setPassword(user.getPassword());
		    
		    entityManager.merge(user);
		    return Response.ok("User information updated successfully.").build();
	    }
	    
        return Response.status(Response.Status.UNAUTHORIZED).entity("User does not exist.").build();
	}
	
	   public User getUserByUsername(String username) {
		    try {
		        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
		        query.setParameter("username", username);
		        User user = (User) query.getSingleResult();
		        System.out.println("User found: " + user.getUsername());
		        return user;
		    } catch (NoResultException e) {
		        System.out.println("No user found for username: " + username);
		        return null;
		    } catch (Exception e) {
		        e.printStackTrace();
		        return null;
		    }
		}

}