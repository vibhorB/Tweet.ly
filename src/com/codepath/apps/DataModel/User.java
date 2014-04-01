package com.codepath.apps.DataModel;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private String name;
	private String profileImageUrl;
	private String screenName;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public static User fromJson(JSONObject user){
		User t = new User();
		 try {
	            // Deserialize json into object fields
	            t.name = user.getString("name");
	            t.screenName = "@" + user.getString("screen_name");
	            t.profileImageUrl = user.getString("profile_image_url");
	        } catch (JSONException e) {
	            e.printStackTrace();
	            return null;
	        }
	        // Return new object
	        return t;
	}
}
