package tictactoe.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Player {
	
	@Id
	String email;
	String userId;
	String nickName;
	String name;
	int age;
	
	public Player(String email, String userId, String nickName, String name, int age) {
		this.email = email;
		this.userId = userId;
		this.nickName = nickName;
		this.name = name;
		this.age = age;
	}
	
	public Player(String email, String userId) {
		this(email, userId, null, null, 0);
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setInfo(String nickName, String name, int age) {
		this.nickName = nickName;
		this.name = name;
		this.age = age;
	}
	
}
