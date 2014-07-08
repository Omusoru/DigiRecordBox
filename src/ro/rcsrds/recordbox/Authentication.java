package ro.rcsrds.recordbox;

import net.koofr.api.v2.DefaultClientFactory;
import net.koofr.api.v2.StorageApi;
import net.koofr.api.v2.StorageApiException;
import android.content.SharedPreferences;
import android.util.Log;

public class Authentication {

	private String host = "storage.rcs-rds.ro";
	private String authToken;
	private boolean loggedIn;
	private static final String TAG = "Login";
	private SharedPreferences preferences;
	
	public Authentication(SharedPreferences preferences) {
		this.loggedIn = preferences.getBoolean("loggedIn", false);
		this.authToken = preferences.getString("authToken", "");
		this.preferences = preferences;
	}
	
	public boolean isLoggedIn() {
		return this.loggedIn;
	}
	
	public String getAuthToken() {
		return this.authToken;
	}
	
	public boolean logIn(String username, String password) {
		StorageApi api = null;
		this.loggedIn = true;
		try {
			api = DefaultClientFactory.create(this.host,username, password);
		} catch (StorageApiException sae) {
			Log.e(Authentication.TAG,sae.getMessage());
			this.loggedIn = false;
		}
		
		if(this.loggedIn) {
			this.authToken = api.getAuthToken();
			//Save login info to SharedPreferences	
		    SharedPreferences.Editor editor = this.preferences.edit();
		    editor.putBoolean("loggedIn", true);
		    String authToken = api.getAuthToken();
		    editor.putString("authToken", authToken);
		    editor.commit();
		    return true;
		} else {
			return false;
		}
		
	}
	
	public void logOut() {
		
		this.loggedIn = false;
		this.authToken = "";
		
		// Clear login info
	    SharedPreferences.Editor editor = preferences.edit();
	    editor.putBoolean("loggedIn", false);
	    editor.putString("authToken", "");
	    editor.commit();
	}
	
}
