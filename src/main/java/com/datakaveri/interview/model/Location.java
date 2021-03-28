package com.datakaveri.interview.model;
import java.util.List;

import com.google.gson.Gson;

import io.vertx.core.json.JsonObject;

public class Location {

	private String type;
	private List<Double> coordinates;
	
	public Location() {
		  
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public  List<Double> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<Double> coordinates) {
		this.coordinates = coordinates;
	}
	
	public Location(String type, List<Double> coordinates) {
		super();
		this.type = type;
		this.coordinates = coordinates;
	}

	public Location(JsonObject json) {
		/*
		 * this.type = json.getString("type"); this.coordinates =
		 * json.getString("coordinates");
		 */
		Gson gson = new Gson();
		String jsonString = json.toString();
		Location temp =  gson.fromJson(jsonString, Location.class);
		this.type = temp.type;
		this.coordinates = temp.coordinates;
	}

public JsonObject toJson() {
		/*
		 * JsonObject json = new JsonObject(); this.type = json.getString("type");
		 * this.coordinates = json.getString("coordinates"); return json;
		 */
	    Gson gson = new Gson();
    	String jsonString = gson.toJson(this);
    	return new JsonObject(jsonString);

  }
}
