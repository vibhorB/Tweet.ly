package com.codepath.apps.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclient.TwitterUtils;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class HomeActivity extends Activity {

	private PullToRefreshListView lvTweets;
	private TwitterHomeFeedAdapter adapterTweets;
	private TwitterClient client;
	private Context context;
	
	private int TWEET_REQUEST_CODE = 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		context = this;
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		setTweetListView();
		
		if(TwitterUtils.isNetworkAvailable(context)){
			//clear DB so that only new tweets are saved
			Tweet.deleteAll();
	        fetchHomeTimeLine(1, 0);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
			//load from DB
			adapterTweets.clear();
			adapterTweets.notifyDataSetInvalidated();
			adapterTweets.addAll(Tweet.getAll());
			lvTweets.onRefreshComplete();
			hideProgressBar();
		}
	}
	
	private void setTweetListView(){
		ArrayList<Tweet> aTweets = new ArrayList<Tweet>();
		adapterTweets = new TwitterHomeFeedAdapter(this, aTweets);
		lvTweets.setAdapter(adapterTweets);
		
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	if(TwitterUtils.isNetworkAvailable(context)){
            		Tweet.deleteAll();
        	        fetchHomeTimeLine(1, 0);
        		}else{
        			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
        			adapterTweets.clear();
        			adapterTweets.notifyDataSetInvalidated();
        			adapterTweets.addAll(Tweet.getAll());
        			lvTweets.onRefreshComplete();
					hideProgressBar();
        		}
            }
            

        });
		
//		lvTweets.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
//					long arg3) {
//				Tweet clickedTweet = adapterTweets.getItem(pos);
//				//Tweet clickedTweet = (Tweet) lvTweets.getItemAtPosition(pos);
//				launchDetailsActivity(clickedTweet);
//				
//			}
//		});
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
	        @Override
	        public void onLoadMore(int page, int totalItemsCount) {
	        	//if(page<=8){
	        	if(TwitterUtils.isNetworkAvailable(context)){
	        		Tweet last = (Tweet) lvTweets.getItemAtPosition(totalItemsCount-1);
		        	if(last == null){
		        		fetchHomeTimeLine(1,0);
		        	}else{
		        		fetchHomeTimeLine(page, last.getTweetId()-1);
		        	}
	        	}else{
        			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
        			lvTweets.onRefreshComplete();
					hideProgressBar();
        		}
	        	
	        	
	        	//}
	        }
	        });
	}

	protected void launchDetailsActivity(Tweet clicked) {
		Intent detailView = new Intent(HomeActivity.this, TweetDetailActivity.class);
		detailView.putExtra("tweet", clicked);
		startActivity(detailView);
	}

	private void fetchHomeTimeLine(int page, long l) {
		client = new TwitterClient(context);
		showProgressBar();
		if(page <= 1){
			client.getHomeTimeline(new JsonHttpResponseHandler() { 
				@Override
			            public void onSuccess(int code, JSONArray body) {
					adapterTweets.clear();
					adapterTweets.notifyDataSetInvalidated();
		            JSONArray items = null;
					items = body;
					// Parse json array into array of model objects
					ArrayList<Tweet> tweets = Tweet.fromJSON(items);
					// Load model objects into the adapter
					for (Tweet twt : tweets) {
					   adapterTweets.add(twt);
					   twt.save();
					}
					lvTweets.onRefreshComplete();
					hideProgressBar();
				}
				public void onFailure(Throwable e, JSONObject error) {
				    // Handle the failure and alert the user to retry
					String err = e.getLocalizedMessage();
					if(err.trim().equals("Client Error (429)")){
						err = "Rate limiting, try later";
					}
				   Toast.makeText(context, "Excepton : "+err, Toast.LENGTH_SHORT).show();
				   hideProgressBar();
				  }				
				});	
		}else{
			client.getMoreHomeTimeline(l, new JsonHttpResponseHandler() { 
				@Override
			            public void onSuccess(int code, JSONArray body) {
		            JSONArray items = null;
		            // Get the movies json array
					items = body;
					// Parse json array into array of model objects
					ArrayList<Tweet> tweets = Tweet.fromJSON(items);
					// Load model objects into the adapter
					for (Tweet twt : tweets) {
					   adapterTweets.add(twt);
					   twt.save();
					}
					hideProgressBar();
				}
				public void onFailure(Throwable e, JSONObject error) {
				    // Handle the failure and alert the user to retry
					String err = e.getLocalizedMessage();
					if(err.trim().equals("Client Error (429)")){
						err = "Rate limiting, try later";
					}
				   Toast.makeText(context, "Excepton : "+err, Toast.LENGTH_SHORT).show();
				   hideProgressBar();
				  }
				});	
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		if(item.getItemId() ==  R.id.action_settings){
			postTweet();
            return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
	}

	private void postTweet() {
		if(TwitterUtils.isNetworkAvailable(context)){
			Intent i = new Intent(HomeActivity.this, TweetActivity.class);
			startActivityForResult(i, TWEET_REQUEST_CODE);
		}else{
			Toast.makeText(context, "No network connectivity", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == TWEET_REQUEST_CODE && resultCode == RESULT_OK){
			adapterTweets.clear();
			adapterTweets.notifyDataSetInvalidated();
			fetchHomeTimeLine(1,0);
		}
	}
	
	public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true); 
    }

    // Should be called when an async task has finished
    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false); 
    }
}
