package se.sandos.android.flickrcheck;


import com.example.checker.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {
	public static final String LOG_TAG = "majs";
	private FlickrFragment flickr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new FlickrFragment(), "flickr").commit();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		
		if(id == R.id.video) {
			Log.w(LOG_TAG, "Selected " + item);
			if(flickr != null) {
				flickr.setVideo(true);
			} else {
				flickr = (FlickrFragment) getFragmentManager().findFragmentByTag("flickr");
				if(flickr != null) {
					flickr.setVideo(true);
				}
			}
		}
		
		if(id == R.id.location) {
			Intent intent = new Intent("org.openintents.action.PICK_DIRECTORY");
			startActivityForResult(intent, 1);
		}
		
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w("majs", "GOT RESULT " + requestCode);
		if(requestCode == 1) {
			Uri data2 = data.getData();
			
			if(flickr == null) {
				Log.w(LOG_TAG, "Getting flickr");
				flickr = (FlickrFragment) getFragmentManager().findFragmentByTag("flickr");
			}
			
			if(flickr != null) {
				Log.w("majs", data2.getPath());
				flickr.setLocation(data2.getPath());
			}
		}
	}
}
