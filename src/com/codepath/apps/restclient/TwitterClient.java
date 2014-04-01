package com.codepath.apps.restclient;

import java.io.ByteArrayInputStream;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "Dv11tsJlOuZaCuaNrEg";       // Change this
    public static final String REST_CONSUMER_SECRET = "g9Si4JND7D3ztjtr5whfSRJVNyLwxsfbNEbiFyx9yEw"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://codepathtweets"; // Change this (here and in manifest)
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
    
    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
    	  String apiUrl = getApiUrl("statuses/home_timeline.json");
    	  RequestParams params = new RequestParams();
    	  params.put("count", String.valueOf(25));
    	  //params.put("count", String.valueOf(25));
    	  getClient().get(apiUrl, params, handler);
    	}
    
    public void getMoreHomeTimeline(long max_id, AsyncHttpResponseHandler handler) {
  	  String apiUrl = getApiUrl("statuses/home_timeline.json");
  	  RequestParams params = new RequestParams();
  	  params.put("count", String.valueOf(25));
  	params.put("max_id", String.valueOf(max_id));
  	  //params.put("count", String.valueOf(25));
  	  getClient().get(apiUrl, params, handler);
  	}
    
    public void postTweet(String content, String inReplyTo, ByteArrayInputStream image,AsyncHttpResponseHandler handler) {
  	  String apiUrl;
  	  RequestParams params = new RequestParams();
  	  if(image != null){
  		  apiUrl = getApiUrl("statuses/update_with_media.json");
  		  //String imageData = Base64.encodeToString(image, 0);
  		  params.put("media[]", image);
  	  }else{
  		apiUrl = getApiUrl("statuses/update.json");
  	  }
  	//apiUrl = getApiUrl("statuses/update.json");
  	  params.put("status", content);
  	  if(inReplyTo != null){
  		params.put("in_reply_to_status_id", content);
  	  }
  	  getClient().post(apiUrl, params, handler);
  	}
    
    public void verifyCredentials(AsyncHttpResponseHandler handler){
    	String apiUrl = getApiUrl("account/verify_credentials.json");
    	getClient().get(apiUrl, handler);
    }
    
    public void getUserProfile(String screen_handle, AsyncHttpResponseHandler handler){
    	String apiUrl = getApiUrl("users/show.json");
    	RequestParams params = new RequestParams();
    	  params.put("screen_name", screen_handle);
    	  getClient().get(apiUrl, params, handler);
    }
    
    public void getTweetDetail(long tweetId, AsyncHttpResponseHandler handler){
    	String apiUrl = getApiUrl("statuses/show.json");
    	RequestParams params = new RequestParams();
    	  params.put("id", String.valueOf(tweetId));
    	  getClient().get(apiUrl, params, handler);
    }
    
    public void retweet(String tweetId, AsyncHttpResponseHandler handler){
    	String apiUrl = getApiUrl("statuses/retweet/"+tweetId+".json");
    	  getClient().post(apiUrl, null, handler);
    }
    
    public void postFavoriteChange(boolean isFav, String tweetId, AsyncHttpResponseHandler handler){
    	String apiUrl;
    	if(!isFav){
    		apiUrl = getApiUrl("favorites/create.json");
    	}else{
    		apiUrl = getApiUrl("favorites/destroy.json");
    	}
    	RequestParams params = new RequestParams();
  	  params.put("id", tweetId);
  	  getClient().post(apiUrl, params, handler);
    	
    }
}