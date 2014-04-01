package com.codepath.apps.DataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ClipData.Item;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8863151147410539404L;
	@Column(name = "imageUrl")
	private String imageUrl;
	@Column(name = "name")
	private String name;
	@Column(name = "handle")
	private String handle;
	@Column(name = "content")
	private String content;
	@Column(name = "createdAt")
	private String createdAt;
	
	 @Column(name = "tweet_id", unique = true)
	private long tweetId;
	 @Column(name = "retweet_count")
	 private int retweet_count;
	 @Column(name = "favourites_count")
	 private int favourites_count;
	 @Column(name = "favorited")
	 private boolean favorited;
	 private boolean retweeted;
	 
	 public Tweet(){
		 super();
	 }
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHandle() {
		return handle;
	}
	public void setHandle(String urlHandle) {
		this.handle = urlHandle;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public long getTweetId() {
		return tweetId;
	}
	public void setTweetId(long id) {
		this.tweetId = id;
	}
	public int getRetweet_count() {
		return retweet_count;
	}
	public void setRetweet_count(int retweet_count) {
		this.retweet_count = retweet_count;
	}
	public int getFavourites_count() {
		return favourites_count;
	}
	public void setFavourites_count(int favourites_count) {
		this.favourites_count = favourites_count;
	}
	public boolean isFavorited() {
		return favorited;
	}
	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}
	public boolean isRetweeted() {
		return retweeted;
	}
	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}
	public static Tweet fromJSON(JSONObject jsonObject){
		Tweet t = new Tweet();
		 try {
	            // Deserialize json into object fields
	            t.name = jsonObject.getJSONObject("user").getString("name");
	            t.handle = "@" + jsonObject.getJSONObject("user").getString("screen_name");
	            t.content = jsonObject.getString("text");
	            t.createdAt = jsonObject.getString("created_at");
	            t.imageUrl = jsonObject.getJSONObject("user").getString("profile_image_url");
	            t.tweetId = jsonObject.getLong("id");
	            t.retweet_count = jsonObject.getInt("retweet_count");
	            t.favourites_count = jsonObject.getInt("favorite_count");
	            t.favorited = jsonObject.getBoolean("favorited");
	            t.retweeted = jsonObject.getBoolean("retweeted");
	        } catch (JSONException e) {
	            e.printStackTrace();
	            return null;
	        }
	        // Return new object
	        return t;
	}
	
	public static ArrayList<Tweet> fromJSON(JSONArray jsonArray){
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject businessJson = null;
            try {
                businessJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet twt = Tweet.fromJSON(businessJson);
            if (twt != null) {
                tweets.add(twt);
            }
        }

        return tweets;
	}
	
	public static List<Tweet> getAll() {
        return new Select()
          .from(Tweet.class)
          .execute();
    }
	
	public static void deleteAll(){
		new Delete().from(Tweet.class).execute();
	}
}
