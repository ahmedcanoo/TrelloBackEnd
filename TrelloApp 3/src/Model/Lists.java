package Model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Lists {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	  private String Listname;

	  @ManyToOne
	  @JoinColumn(name = "board_id")
	  private Board board;

	  @ManyToOne
	  @JoinColumn(name = "owner_id")
	  private User owner; 

	 // @Transient
	  @ManyToMany
	  @JoinTable(
	           name = "list_collaborators",
	           joinColumns = @JoinColumn(name = "list_id"),
	           inverseJoinColumns = @JoinColumn(name = "user_id")
	    )
	  
	  	
	    private List<User> collaborators = new ArrayList<>();
	  
	  	
	//  	@Transient
	    @OneToMany(mappedBy = "list")
	    private List<Card> cards = new ArrayList<>();

	    public Lists() {
	       
	    }

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }
	    
		public String getListname() {
			return Listname;
		}

		public void setListname(String listname) {
			Listname = listname;
		}

		public User getOwner() {
			return owner;
		}

		public void setOwner(User owner) {
			this.owner = owner;
		}

		public List<User> getCollaborators() {
			return collaborators;
		}

		public Board getBoard() {
			return board;
		}
		
		public void setBoard(Board board)
		{
			this.board = board;
		}
		
		public void setCollaborators(List<User> collaborators) {
			this.collaborators = collaborators;
		}
		
		
	    public void removeCard(Card card) {
	        cards.remove(card);
	    }
	    public  List<Card> getCards()
	    {
	    	return cards;
	    }

	    public void setCards(List<Card> cards)
	    {
	    	this.cards = cards;
	    }
	    public void addCard(Card card) {
	        if (!this.cards.contains(card)) {
	            this.cards.add(card);
	            card.setList(this); 
	        }
	    }
	
}