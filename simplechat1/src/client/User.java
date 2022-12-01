package client;
import java.io.Serializable;

public class User implements Serializable{
	private String name;
	private int age,id;
	public User() {
		
	}
	public User(String name, int age, int id) {
		this.name=name;
		this.age=age; 
		this.id=id;
	}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public boolean contains(User user) {
		return this.equals(user);
		}
		
	public boolean equals(User user) {
		return user.id==this.id;
	}
	public String toString() {
	return("[Name:"+name+"/Age:"+age+"/Id:"+id+"] ");
	}
}
