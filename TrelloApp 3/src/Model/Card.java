package Model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Card {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    private String description;
  private String comment;
    

    
    
  @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Comment> comments = new ArrayList<>();
  
  
    @ManyToOne
    @JoinColumn(name = "list_id")
    private Lists list;
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User Owner;
    
    @ManyToMany
    @JoinTable(
            name = "card_collaborators",
            joinColumns = @JoinColumn(name = "card_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Transient
    private List<User> collaborators = new ArrayList<>();
    
    public Card() {}
    
    public Card(String description) {
        this.description = description;
    }
    
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getOwner() {
		return Owner;
	}

	public void setOwner(User owner) {
		Owner = owner;
	}
	public List<Comment> getComments() {
		return comments;
	}

	
	public List<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public Lists getList() {
		return list;
	}

	public String getComment() {
        return comment;
    }
	
	public void setComments(List<Comment> comments)
	{
		this.comments =comments;
	}

    public void setComment(String comment) {
        this.comment = comment;
    }
	public void setList(Lists newList) {
	    if (this.list != null) {
	        this.list.getCards().remove(this);  
	    }
	    this.list = newList;
	    if (newList != null && !newList.getCards().contains(this)) {
	        newList.getCards().add(this);  
	    }
	}
	 
	
	public void addComment(String commentText) {
		Comment comment = new Comment();
		comment.setText(commentText);
		comment.setCard(this);
		this.comments.add(comment);
	}

}