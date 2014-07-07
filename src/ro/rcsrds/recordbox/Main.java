package ro.rcsrds.recordbox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new ButtonClickListener());
		
		recorder = new AudioRecorder();
	}
	
	private class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			if (v.getId()==R.id.btn_stop) {
				recorder.stopRecording();
			} else if (v.getId()==R.id.btn_cancel) {
				
			} 
			
		}		
		
	}
	
	private class ButtonToggleListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
	            recorder.startRecording();
	        } else {
	        	Log.d("button","is not checked");
	        }

			
		}
		
	}
	
}
