package world.runna.model;
import jakarta.persistence.*;
import java.util.*;
@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue private Long id;
    private String username; private String email; private String passwordHash;
    private String displayName; private String avatarUrl;
    @OneToMany(mappedBy="user", cascade=CascadeType.ALL) private List<Activity> activities = new ArrayList<>();
    public User(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
    public String getEmail(){return email;} public void setEmail(String v){this.email=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){this.passwordHash=v;}
    public String getDisplayName(){return displayName;} public void setDisplayName(String v){this.displayName=v;}
    public String getAvatarUrl(){return avatarUrl;} public void setAvatarUrl(String v){this.avatarUrl=v;}
    public List<Activity> getActivities(){return activities;} public void setActivities(List<Activity> v){this.activities=v;}
}
