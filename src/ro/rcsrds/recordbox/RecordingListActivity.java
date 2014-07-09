package ro.rcsrds.recordbox;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class RecordingListActivity extends ListActivity {
	
	ArrayList<String> listItems=new ArrayList<String>();
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordinglist);
		
		listItems.add("Obiect 1");
		listItems.add("Obiect 2");
		
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
	    setListAdapter(adapter);
	    
	    
	}

}
