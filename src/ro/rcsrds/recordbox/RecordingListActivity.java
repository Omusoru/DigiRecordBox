package ro.rcsrds.recordbox;

import android.app.ListActivity;
import android.os.Bundle;

public class RecordingListActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordinglist);
	}

}
