package com.datakaveri.interview.model;

import com.google.gson.Gson;

import io.vertx.core.json.JsonObject;


public class Device {
	
	private String deviceId;
	private String domain;
	private String state;
	private String city;
	private Location location;
	private String deviceType;
	
	public Device(String deviceId, String domain, String state, String city, Location location, String deviceType) {
		this.deviceId = deviceId;
		this.domain = domain;
		this.state = state;
		this.city = city;
		this.location = location;
		this.deviceType = deviceType;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void  setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public Device() {
		
	}
	
	public Device(JsonObject json) {
		/*
		 * this.deviceId = json.getString("deviceId"); this.domain =
		 * json.getString("domain"); this.state = json.getString("state"); this.city =
		 * json.getString("city"); //this.location = (Location)
		 * json.getJsonObject("location"); this.deviceType =
		 * json.getString("deviceType");
		 */
		
		Gson gson = new Gson();
		String jsonString = json.toString();
		Device temp =  gson.fromJson(jsonString, Device.class);
		this.deviceId = temp.deviceId;
		this.domain = temp.domain;
		this.state = temp.state;
		this.city = temp.city;
		this.location = temp.location;
		this.deviceType = temp.deviceType;
	}
	
  public JsonObject toJson() {
		/*
		 * JsonObject json = new JsonObject() .put("deviceId",deviceId)
		 * .put("domain",domain) .put("state",state) .put("city",city)
		 * .put("location",location) .put("deviceType",deviceType); return json;
		 */
	    Gson gson = new Gson();
	    String jsonString = gson.toJson(this);
	    return new JsonObject(jsonString);
	  }

}
