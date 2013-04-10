package tictactoe.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Player {

    @Id
    String email;
    String userId;
    String gcmRegId;
    String nickName;
    String name;
    int age;

    public Player(String email, String userId,String regId, String nickName, String name, int age) {
        this.email = email;
        this.userId = userId;
        this.gcmRegId = regId;
        this.nickName = nickName;
        this.name = name;
        this.age = age;
    }

    public Player(String email, String userId) {
        this(email, userId, null, null, null, 0);
    }

    public Player(Player p) {
        this.email = p.email;
        this.userId = p.userId;
        this.nickName = p.nickName;
        this.name = p.name;
        this.age = p.age;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
    
    public void setGcmRegId(String regId) {
        gcmRegId = regId;
    }
    
    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setInfo(String nickName, String name, int age) {
        this.nickName = nickName;
        this.name = name;
        this.age = age;
    }
}
