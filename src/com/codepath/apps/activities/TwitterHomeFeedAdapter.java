package com.codepath.apps.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.apps.DataModel.Tweet;
import com.codepath.apps.restclienttemplate.R;
import com.squareup.picasso.Picasso;


public class TwitterHomeFeedAdapter extends ArrayAdapter<Tweet>{

	public TwitterHomeFeedAdapter(Context context, ArrayList<Tweet> tweets){
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		Tweet tweet = this.getItem(position);
		if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, null);
        }
		convertView.setTag(convertView.toString());
        // Lookup views within item layout
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvHandle = (TextView) convertView.findViewById(R.id.tvHandle);
        TextView tvContent = (TextView) convertView.findViewById(R.id.tvContent);
        ImageView ivPosterImage = (ImageView) convertView.findViewById(R.id.ivPosterImage);
        TextView tvWhen = (TextView) convertView.findViewById(R.id.tvWhen);
        // Populate the data into the template view using the data object
        tvName.setText(tweet.getName());
        tvHandle.setText(tweet.getHandle());
        tvContent.setText(tweet.getContent());
        Picasso.with(getContext()).load(tweet.getImageUrl()).into(ivPosterImage);
        //scaleImage(ivPosterImage, 200);
        // Return the completed view to render on screen
        tvWhen.setText(getWhenCreated(tweet.getCreatedAt()));
        setUpImageViewListener(ivPosterImage, tweet.getHandle());
        setUpItemClickListener(convertView, tweet);
        
        return convertView;
	}
	
	private void setUpItemClickListener(View v, final Tweet clicked){
		
			v.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				launchDetailsActivity(clicked);
			}
		});
	}
	
	protected void launchDetailsActivity(Tweet clicked) {
		Intent detailView = new Intent(getContext(), TweetDetailActivity.class);
		detailView.putExtra("tweet", clicked);
		getContext().startActivity(detailView);
	}
	
	private void setUpImageViewListener(ImageView iv, final String screen_handle){
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchUserDetailActivity(screen_handle);
				
			}
		});
	}
	
	private void launchUserDetailActivity(String screen_name){
		Intent i = new Intent(getContext(), UserDetailActivity.class);
		i.putExtra("screen_name", screen_name);
		getContext().startActivity(i);
	}
	
	private void scaleImage(ImageView view, int boundBoxInDp)
	{
	    // Get the ImageView and its bitmap
	    Drawable drawing = view.getDrawable();
	    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	    // Get current dimensions
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.
	    float xScale = ((float) boundBoxInDp) / width;
	    float yScale = ((float) boundBoxInDp) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);
	    width = scaledBitmap.getWidth();
	    height = scaledBitmap.getHeight();

	    // Apply the scaled bitmap
	    view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);
	}
	
	private String getWhenCreated(String time){
		
		final String TWITTER="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		  SimpleDateFormat sf = new SimpleDateFormat(TWITTER);
		  sf.setLenient(true);
		Date d = null;
		try {
			d = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long currentTime = System.currentTimeMillis();
		long created = d.getTime();
		String timeAgo = DateUtils.getRelativeDateTimeString(getContext(), created, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();
		//String timeAgo = TimeUtils.millisToLongDHMS(currentTime - created);
		String result = timeAgo.substring(0,  timeAgo.indexOf(","));
		int indexOfSpace = result.indexOf(" ");
		String t1 = result.substring(0,indexOfSpace);
		String t2 = result.substring(indexOfSpace+1, indexOfSpace+2);
		return t1+t2;
	}
}
