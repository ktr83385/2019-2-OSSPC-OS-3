package com.ok.ai;
import com.ok.ai.UserList; 

public class UserList implements Comparable<UserList>{
	String id;
	int score;
	
	public UserList(String name, int sc){  //get user ID & Score, return them
		id = name;
		score = sc;
	}
	
	public String getID(){
		return id;
	}
	public int getScore(){
		return score;
	}
	
	public int compareTo(UserList r2){
		return r2.score - this.score;
	}
	
	public String toString(){
		return id + ", " + score;
	}
}
