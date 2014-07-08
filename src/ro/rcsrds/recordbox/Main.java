package ro.rcsrds.recordbox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class Main extends Activity {
	
	private ToggleButton tglRecord;
	private Button btnStop;
	private Button btnCancel;
	private AudioRecorder recorder;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		tglRecord = (ToggleButton) findViewById(R.id.btn_record);
		tglRecord.setOnCheckedChangeListener(new ButtonToggleListener());
		btnStop = (Button) findViewById(R.id.btn_stop);
		btnStop.setOnClickListener(new ButtonClickListener());
		btnStop.setVisibility(View.INVISIBLE);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new ButtonClickListener());
		btnCancel.setVisibility(View.INVISIBLE);
		
		recorder = new AudioRecorder();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if (v.getId()==R.id.btn_stop) {
				recorder.stopRecording();
				btnStop.setVisibility(View.INVISIBLE);
	            btnCancel.setVisibility(View.INVISIBLE);
			} else if (v.getId()==R.id.btn_cancel) {
				//TODO Cancel recording
			} 
			
		}		
		
	}
	
	private class ButtonToggleListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
	            recorder.startRecording();
	            btnStop.setVisibility(View.VISIBLE);
	            btnCancel.setVisibility(View.VISIBLE);
	        } else {
	        	Log.d("button","is not checked");
	        	tglRecord.setChecked(true);
	        	//TODO Pause recording
	        }

			
		}
		
	}
	
}
