package ro.rcsrds.recordbox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	private ToggleButton tglRecord;
	private Button btnStop;
	private Button btnCancel;
	private AudioRecorder recorder;
	public static final String PREFS_NAME = "Authentication";
	private Authentication auth;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//!!!!!!!!!!!!!Temp fix for networking on main thread!!!!!!!!!!!!!!!!!!!
		//TODO Use separate thread!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
		setContentView(R.layout.main);
		
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
		auth = new Authentication(preferences);
		
		//Check if user is logged in
		if(!auth.isLoggedIn()) {
			//Close main activity so user can't bypass login screen
			finish();
			//Start login activity
			Intent login = new Intent(MainActivity.this,LoginActivity.class);
			startActivity(login);			
		}		
		
		tglRecord = (ToggleButton) findViewById(R.id.btn_recorder_start);
		tglRecord.setOnCheckedChangeListener(new ButtonToggleListener());
		btnStop = (Button) findViewById(R.id.btn_recorder_stop);
		btnStop.setOnClickListener(new ButtonClickListener());
		btnStop.setVisibility(View.INVISIBLE);
		btnCancel = (Button) findViewById(R.id.btn_recorder_cancel);
		btnCancel.setOnClickListener(new ButtonClickListener());
		btnCancel.setVisibility(View.INVISIBLE);

		recorder = new AudioRecorder();
	}
	
	//Clear login on app close
	/*@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("isLoggedIn", false);
	    editor.commit();	
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_options_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.option_menu_logout) {			
			auth.logOut();	
		    //Close main activity so user can't bypass login screen
			finish();
			//Start login activity
			Intent login = new Intent(MainActivity.this,LoginActivity.class);
			startActivity(login);	
		} else if(item.getItemId()==R.id.option_menu_mediaplayer) {
			Intent mediaPlayer = new Intent(MainActivity.this,MediaPlayerActivity.class);
			startActivity(mediaPlayer);
		} else if(item.getItemId()==R.id.option_menu_list) {
			Intent mediaPlayer = new Intent(MainActivity.this,RecordingListActivity.class);
			startActivity(mediaPlayer);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if (v.getId()==R.id.btn_recorder_stop) {
				recorder.stopRecording();
				btnStop.setVisibility(View.INVISIBLE);
	            btnCancel.setVisibility(View.INVISIBLE);	
	            tglRecord.setChecked(false);
			} else if (v.getId()==R.id.btn_recorder_cancel) {
				recorder.cancelRecording();
				btnStop.setVisibility(View.INVISIBLE);
	            btnCancel.setVisibility(View.INVISIBLE);
	            tglRecord.setChecked(false);
			} 
			
		}		
		
	}
	
	private class ButtonToggleListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// Initial state is 'not checked'
			// Recording starts when button is checked
			if (isChecked) { 
	            recorder.startRecording();
	            btnStop.setVisibility(View.VISIBLE);
	            btnCancel.setVisibility(View.VISIBLE);	            
	        // If it's unchecked, it means it's recording
	        // Checking it again, will pause the recording
	        } else {	        	
	        	recorder.startRecording();
	        }

			
		}
		
	}
	
}
