package Model;

import java.util.ArrayList;
import java.util.List;

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
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String boardName;
    
    @ManyToOne
    @JoinColumn(name = "userid") 
    private User owner;

@Transient
@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "board_collaborators",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "userid")
        )
    private List<User> collaborators = new ArrayList<>();

	@Transient
    @OneToMany(mappedBy = "board")
    private List<Lists> lists = new ArrayList<>();
    
    public Board( ) {};
    public Board(String name, User owner) {
       this. boardName = name;
        this.owner = owner;
        this.collaborators = new ArrayList<>();
        this.lists = new ArrayList<>();
    }
   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getBoardname() {
        return boardName;
    }

    public void setBoardname(String boardname) {
        boardName = boardname;
    }
    
    public void addList(Lists list) {
      lists.add(list);
    }
     public User getOwner() {
            return owner;
        }
     public List<Lists> getLists() {
			return lists;
		}
		public void setLists(List<Lists> lists) {
			this.lists = lists;
		}

        public void setOwner(User owner) {
            this.owner = owner;
        }
        public List<User> getCollaborators() {
            return collaborators;
        }

        public void setCollaborators(List<User> collaborators) {
            this.collaborators = collaborators;
        }
        public void addCollaborator(User user) {
            collaborators.add(user);
            user.getBoards().add(this);
        }

        public void removeCollaborator(User user) {
            collaborators.remove(user);
            user.getBoards().remove(this);
        }

}