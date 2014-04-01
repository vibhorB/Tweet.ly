package com.codepath.apps.activities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.DataModel.User;
import com.codepath.apps.restclient.TwitterClient;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

public class TweetActivity extends Activity {

	private EditText etStatus;
	private TextView tvChars;
	private int MAX_CHARS = 140;
	private TwitterClient client;
	private Context context;
	
	private ImageView ivPic;
	private TextView tvName;
	private TextView tvScreenName;
	private User currentUser;
	
	private ImageButton ibGallery;
	private ImageButton ibCamera;
	private ImageButton ibLocation;
	private ImageView ivTwtImage;
	
	private byte[] image;
	private Bitmap photo;
	
	private boolean isReplyAction;
	private int RESULT_LOAD_IMAGE = 123;
	private static final int PICTURE_RESULT = 124;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;
		isReplyAction = checkIsInitiatedFromReply();
		//ibPost = (ImageButton) findViewById(R.id.ibPost);
		etStatus = (EditText) findViewById(R.id.etStatus);
		tvChars = (TextView) findViewById(R.id.tvChars);
		
		ivPic = (ImageView) findViewById(R.id.ivUserImg);
		tvName = (TextView) findViewById(R.id.tvTwtName);
		tvScreenName = (TextView) findViewById(R.id.tvScreenName);
		ivTwtImage = (ImageView) findViewById(R.id.ivTwtImage);
		initActionButtons();
		
		if(isReplyAction){
			String replyScreen = getIntent().getStringExtra("screen_name");
			etStatus.setText(replyScreen);
			etStatus.setSelection(etStatus.getText().length());
		}
		
		etStatus.requestFocus();
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		verifyCredentials();
		setCharsText();
		setUpStatusListener();
		ivTwtImage.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				ivTwtImage.setImageDrawable(null);
				return false;
			}
		});
		//setUpPostButtonListener();
	}

	private void initActionButtons() {
		ibGallery = (ImageButton) findViewById(R.id.ibGallery);
		ibCamera = (ImageButton) findViewById(R.id.ibCamera);
		ibLocation = (ImageButton) findViewById(R.id.ibLocation);
		
		ibGallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				 startActivityForResult(i, RESULT_LOAD_IMAGE);
				
			}
		});
		
		ibCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
		        startActivityForResult(camera, PICTURE_RESULT);	
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      // REQUEST_CODE is defined above
		if(resultCode == RESULT_OK){
			if(requestCode == PICTURE_RESULT && data != null){
				
		                 Bundle b = data.getExtras(); // Kept as a Bundle to check for other things in my actual code
		                 photo = (Bitmap) b.get("data");

		                 if (photo != null) { // Display your image in an ImageView in your layout (if you want to test it)
		                	 setImageToView();
		                 }
		    
			}else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
		            && null != data) {
		        Uri selectedImage = data.getData();
		        String[] filePathColumn = { MediaStore.Images.Media.DATA };
		        Cursor cursor = getContentResolver().query(selectedImage,
		                filePathColumn, null, null, null);
		        cursor.moveToFirst();
		        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		        String picturePath = cursor.getString(columnIndex);
		        photo = BitmapFactory.decodeFile(picturePath.toString());

		        int bytes = photo.getByteCount();
		        ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
		        photo.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

		        image = buffer.array();
		        setImageToView();
		    }
		}
		
    }
	
	private void setImageToView(){
		ivTwtImage.setImageBitmap(photo);
		ivTwtImage.invalidate();
	}

	private boolean checkIsInitiatedFromReply() {
		String purpose = getIntent().getStringExtra("purpose");
		if(purpose != null && purpose.equals("reply")){
			return true;
		}
		return false;
	}

	private void setUpProfileInfo() {
		tvName.setText(currentUser.getName());
		tvScreenName.setText(currentUser.getScreenName());
		Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivPic);
		
	}

	private void setCharsText() {
		int rem = MAX_CHARS - etStatus.getText().toString().length();
		String limit = rem + " characters remaining";
		tvChars.setText(limit);
		//miChars.setTitle(""+charsRem);
		
	}

	private void setUpStatusListener() {
		etStatus.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setCharsText();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tweet, menu);
		//miChars = (MenuItem) findViewById(R.id.as_chars);
		//setCharsTextToActionBar(MAX_CHARS);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			setResult(RESULT_OK);
            this.finish();
            return true;
		}else if(item.getItemId() == R.id.as_tweet){
			tweet();
			return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
    }
	
	private void tweet() {
		String st = etStatus.getText().toString();
		if(st == null || st.length() == 0 || st.equals("")){
			Toast.makeText(TweetActivity.this,
					"Write something to post",
					Toast.LENGTH_LONG).show();
		}else if(st.length() > 140){
			Toast.makeText(TweetActivity.this,
					"140 chars only",
					Toast.LENGTH_LONG).show();
		}else{
			postTweet(st);
		}
		
	}

	private void postTweet(String status){
		client = new TwitterClient(context);
		String inReplyTo = null;
		if(isReplyAction){
			inReplyTo = String.valueOf(getIntent().getLongExtra("id", 0));
		}
		client.postTweet(status, inReplyTo, getBAISForPhoto(),new JsonHttpResponseHandler() { 
		@Override
	            public void onSuccess(JSONObject body) {
				finishActivity();
					//ComposeTweetActivity.this.finish();
					super.onSuccess(body);
		}
		public void onFailure(Throwable e, JSONObject error) {
		    // Handle the failure and alert the user to retry
		   Toast.makeText(context, "Excepton : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		  }
		});	
	}
	
	private ByteArrayInputStream getBAISForPhoto(){
		if(photo == null){
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        // then flip the stream
        byte[] myTwitterUploadBytes = baos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(myTwitterUploadBytes);
        return bis;
	}
	
	private void finishActivity(){
		setResult(RESULT_OK);
        this.finish();
	}
	
	private void verifyCredentials() {
		client = new TwitterClient(context);
		client.verifyCredentials(new JsonHttpResponseHandler() { 
		@Override
	            public void onSuccess(int code, JSONObject json) {
            currentUser = User.fromJson(json);
            setUpProfileInfo();
		}
		public void onFailure(Throwable e, JSONObject error) {
		    // Handle the failure and alert the user to retry
		   Toast.makeText(context, "Excepton : "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		  }
		});	
	}

}
