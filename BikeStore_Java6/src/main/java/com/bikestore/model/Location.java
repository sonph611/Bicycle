package com.bikestore.model;

import lombok.Data;

@Data
public class Location {
    private int id;
    private String fullName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
    
    
}
