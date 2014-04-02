package com.codepath.apps.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

public class TweetDetailActivity extends Activity {

	private ImageView ivTwtDetPic;
	private TextView tvTwtDetName;
	private TextView tvTwtDetScreen;
	private TextView tvTwtDetContent;
	private TextView tvTwtDetTime;
	private TextView tvTwtDetRTCount;
	private TextView tvTwtDetFavCount;
	private EditText etTwtDetRep;
	private TextView tvTwtDetChars;
	private TextView tvTweetButton;

	private ImageButton ibTwtDetReply;
	private ImageButton ibTwtDetRT;
	private ImageButton ibTwtDetFav;
	private ImageButton ibTwtDetShare;

	private Tweet tweet;
	private int MAX_CHARS = 140;
	private TwitterClient client;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		tweet = (Tweet) getIntent().getSerializableExtra("tweet");
		context = this;
		client = new TwitterClient(this);
		initUIElements();
		setValuesToUI();

		initFunctions();
	}

	private void setValuesToUI() {
		Picasso.with(this).load(tweet.getImageUrl()).into(ivTwtDetPic);
		tvTwtDetName.setText(tweet.getName());
		tvTwtDetScreen.setText(tweet.getHandle());
		tvTwtDetContent.setText(tweet.getContent());
		tvTwtDetTime.setText(getWhenCreated(tweet.getCreatedAt()));
		
		tvTwtDetFavCount.setText(""+tweet.getFavourites_count()+" ");
		tvTwtDetRTCount.setText(""+tweet.getRetweet_count()+" ");
		etTwtDetRep.setHint("Reply to "+tweet.getName());
		setCharsText(MAX_CHARS);
		
		setRetweetedIcon(tweet.isRetweeted());
		setFavoritedIcon(tweet.isFavorited());
	}

	private void initFunctions() {
		// actions for image buttons
		// action for edit text
		// action for Tweet textview button
		setupEditTextListeners();
		setUpTweetButton();
		setUpImageButtons();

	}

	private void setUpImageButtons() {
		ibTwtDetReply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startTweetActivity();
			}
		});
		
		ibTwtDetRT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				confirmRetweet();
			}
		});
		
		ibTwtDetFav.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				postFavorite();
			}
		});
		
		ibTwtDetShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startShareIntent();
			}
		});
		
	}

	protected void startShareIntent() {
		String name=tweet.getHandle().substring(1);
		String twtUrl = "https://twitter.com/"+name+"/status/"+tweet.getTweetId();
		String str = "Check out " + tweet.getHandle()+"'s Tweet: "+twtUrl;
		Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.putExtra(Intent.EXTRA_TEXT, str);
	    shareIntent.setType("text/plain");
	    startActivity(Intent.createChooser(shareIntent, "Share Tweet"));		
	}

	protected void confirmRetweet() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
	    b.setTitle("Retweet this to your followers?");
	    b.setPositiveButton("Retweet", new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int whichButton)
	        {
	        	if(!tweet.isRetweeted()){
		        	retweet();
	        	}else{
	        		Toast.makeText(TweetDetailActivity.this, "Already retweeted", Toast.LENGTH_SHORT).show();
	        	}
	        }
	    });
	    b.setNegativeButton("Cancel", null);
	    b.create().show();
	    }

	private void startTweetActivity() {
		Intent reply = new Intent(TweetDetailActivity.this, TweetActivity.class);
		reply.putExtra("purpose", "reply");
		reply.putExtra("screen_name", tweet.getHandle());
		reply.putExtra("id", tweet.getTweetId());
		startActivity(reply);
		
	}

	private void setUpTweetButton() {
		tvTweetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				postTweet(etTwtDetRep.getText().toString());
				
			}
		});
		
	}

	private void setCharsText(int charsRem) {
		String limit = charsRem + "";
		tvTwtDetChars.setText(limit);

	}

	private void setupEditTextListeners() {
		etTwtDetRep.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(hasFocus){
			        etTwtDetRep.setText(tweet.getHandle());
			        etTwtDetRep.setSelection(etTwtDetRep.getText().length());
			    }else {
			        
			    }
			   }
			});
		
		etTwtDetRep.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int length = etTwtDetRep.getText().toString().length();
				int rem = MAX_CHARS - length;
				setCharsText(rem);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initUIElements() {
		ivTwtDetPic = (ImageView) findViewById(R.id.ivTwtDetPic);
		tvTwtDetName = (TextView) findViewById(R.id.tvTwtDetName);
		tvTwtDetScreen = (TextView) findViewById(R.id.tvTwtDetScreen);
		tvTwtDetContent = (TextView) findViewById(R.id.tvTwtDetContent);
		tvTwtDetTime = (TextView) findViewById(R.id.tvTwtDetTime);
		tvTwtDetRTCount = (TextView) findViewById(R.id.tvRetweetC);
		tvTwtDetFavCount = (TextView) findViewById(R.id.tvTwtDetFav);
		etTwtDetRep = (EditText) findViewById(R.id.etTwtDetRep);
		tvTwtDetChars = (TextView) findViewById(R.id.tvTwtDetChars);
		tvTweetButton = (TextView) findViewById(R.id.tvTwtDetTweet);

		ibTwtDetReply = (ImageButton) findViewById(R.id.ibTwtDetReply);
		ibTwtDetFav = (ImageButton) findViewById(R.id.ibTwtDetFav);
		ibTwtDetRT = (ImageButton) findViewById(R.id.ibTwtDetRT);
		ibTwtDetShare = (ImageButton) findViewById(R.id.ibTwtDetShare);

	}
	
	private void setRetweetedIcon(boolean isRetweeted){
		if(isRetweeted){
			ibTwtDetRT.setImageResource(R.drawable.retweet_done_24);
		}else{
			ibTwtDetRT.setImageResource(R.drawable.ic_retweet);
		}
	}
	
	private void setFavoritedIcon(boolean isFavorited){
		if(isFavorited){
			ibTwtDetFav.setImageResource(R.drawable.fav_done_24_red);
		}else{
			ibTwtDetFav.setImageResource(R.drawable.ic_fav);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweet_detail, menu);
		return true;
	}

	private String getWhenCreated(String time) {

		final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
		sf.setLenient(true);
		Date d = null;
		try {
			d = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DateFormat df = new SimpleDateFormat("h:mm a . dd MMM yy");
		return df.format(d);
	}

	private void tweet() {
		String st = etTwtDetRep.getText().toString();
		if (st == null || st.length() == 0 || st.equals("")) {
			Toast.makeText(TweetDetailActivity.this, "Write something to post",
					Toast.LENGTH_LONG).show();
		} else if (st.length() > 140) {
			Toast.makeText(TweetDetailActivity.this, "140 chars only",
					Toast.LENGTH_LONG).show();
		} else {
			postTweet(st);
		}

	}

	private void postTweet(String status) {
		client.postTweet(status, String.valueOf(tweet.getTweetId()), null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject body) {
				finishActivity();
				// ComposeTweetActivity.this.finish();
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				// Handle the failure and alert the user to retry
				Toast.makeText(context,
						"Excepton : " + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	protected void postFavorite() {

		client.postFavoriteChange(tweet.isFavorited(), String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject body) {
				//finishActivity();
				if(!tweet.isFavorited()){
					tweet.setFavorited(true);
					setFavoritedIcon(true);
				}else{
					tweet.setFavorited(false);
					setFavoritedIcon(false);
				}
				// ComposeTweetActivity.this.finish();
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				// Handle the failure and alert the user to retry
				Toast.makeText(context,
						"Excepton : " + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void retweet() {
		
		client.retweet(String.valueOf(tweet.getTweetId()), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject body) {
				finishActivity();
				// ComposeTweetActivity.this.finish();
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				// Handle the failure and alert the user to retry
				Toast.makeText(context,
						"Excepton : " + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			setResult(RESULT_OK);
            this.finish();
            return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
    }
	
	private void finishActivity(){
		setResult(RESULT_OK);
        this.finish();
	}

}
