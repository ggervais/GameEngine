package com.ggervais.gameengine;

public abstract class Entity {
	private int id;
	private String name;
	
	public Entity(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Entity() {
		this(-1, "");
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
