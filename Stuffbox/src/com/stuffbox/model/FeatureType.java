package com.stuffbox.model;

public class FeatureType {
	private int id;
	private String name;

	public FeatureType(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	public String toString(){
		return name;
	}
}