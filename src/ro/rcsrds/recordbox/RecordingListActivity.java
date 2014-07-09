package ro.rcsrds.recordbox;

import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;

public class RecordingListActivity extends ListActivity {
	
	ArrayList<String> listItems=new ArrayList<String>();
	ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordinglist);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
	    setListAdapter(adapter);
	   
	    loadRecordings();
    	    
}
	
	public void loadRecordings() {
		
		String path = Environment.getExternalStorageDirectory().toString()+"/DigiRecordbox";
		//Log.d("FTP", "Path: " + path);
		File f = new File(path);        
		File file[] = f.listFiles();
		//Log.d("FTP", "Size: "+ file.length);
		for (int i=0; i < file.length; i++)
		{
		    //Log.d("FTP", "FileName:" + file[i].getName());
			listItems.add(file[i].getName());
		}
		
		
	}

}
