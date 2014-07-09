package ro.rcsrds.recordbox;

import java.io.IOException;

import android.content.SharedPreferences;
import android.util.Log;

public class Authentication {

	private String host = "storage.rcs-rds.ro";
	private int port = 21;
	//private String authToken;
	private boolean loggedIn;
	private static final String TAG = "Login";
	private SharedPreferences preferences;
	private DigiFTPClient ftp;
	
	public Authentication(SharedPreferences preferences) {
		this.loggedIn = preferences.getBoolean("loggedIn", false);
		//this.authToken = preferences.getString("authToken", "");
		this.preferences = preferences;
	}
	
	public boolean isLoggedIn() {
		return this.loggedIn;
	}
	
	public boolean logIn(String username, String password) {
		/*StorageApi api = null;
		this.loggedIn = true;
		try {
			api = DefaultClientFactory.create(this.host,username, password);
		} catch (StorageApiException sae) {
			Log.e(Authentication.TAG,sae.getMessage());
			this.loggedIn = false;
		}*/
		
		ftp=new DigiFTPClient(this.host,this.port,username,password);
		try {
			ftp.connect();
		} catch (IOException e) {
			Log.d(Authentication.TAG,e.getMessage());
		}
		
		try {
			this.loggedIn = ftp.logIn();
		} catch (Exception e) {
			Log.d(Authentication.TAG, e.getMessage());
		}		
		
		if(this.loggedIn) {
			//this.authToken = api.getAuthToken();
			//Save login info to SharedPreferences	
		    SharedPreferences.Editor editor = this.preferences.edit();
		    editor.putBoolean("loggedIn", true);
		    //String authToken = api.getAuthToken();
		    //editor.putString("authToken", authToken);
		    editor.commit();
		    return true;
		    
		} else {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				Log.d(Authentication.TAG, e.getMessage());
			}
			
			return false;
			
		}
		
	}
	
	public void logOut() {
		
		this.loggedIn = false;
		//this.authToken = "";
		
		// Clear login info
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putBoolean("loggedIn", false);
	    //editor.putString("authToken", "");
	    editor.commit();
	    
	    //Disconnect FTP
	    try {
	    	ftp.disconnect();
	    } catch (IOException e) {
	    	Log.d(Authentication.TAG,e.getMessage());
	    }
	    
	}
	
}
