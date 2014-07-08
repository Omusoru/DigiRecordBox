package ro.rcsrds.recordbox;

import net.koofr.api.v2.DefaultClientFactory;
import net.koofr.api.v2.StorageApi;
import net.koofr.api.v2.StorageApiException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private EditText etEmail;
	private EditText etPassword;
	private Button btnLogin;
	private static final String TAG = "Login";
	public static final String PREFS_NAME = "Authentification";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		etEmail = (EditText) findViewById(R.id.et_email);
		etPassword = (EditText) findViewById(R.id.et_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new ButtonOnClickListener());		
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//Toast message = Toast.makeText(Login.this, R.string.message_authentification_failed, Toast.LENGTH_SHORT);
		//message.show();		
		finish();
	}
	
	private class ButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String host = "storage.rcs-rds.ro";
			String username = etEmail.getText().toString();
			String password = etPassword.getText().toString();
			login(host,username,password);			
		}
		
	}
	
	private void login(String host, String username, String password) {
		
		boolean loggedIn = true;
		
		try {
			StorageApi api = DefaultClientFactory.create(host,username, password);
			Log.d(Login.TAG,api.getUserInfo().getFormattedName());			
		} catch (StorageApiException sae) {			
			Log.e(Login.TAG,sae.getMessage());
			loggedIn = false;
		}	
		
		if(loggedIn) {
			//Save login info to SharedPreferences			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putBoolean("isLoggedIn", true);
		    editor.commit();				
			//Start main
			Intent intent = new Intent(Login.this,Main.class);
			startActivity(intent);	
		} else {
			Toast message = Toast.makeText(Login.this, R.string.message_authentification_failed, Toast.LENGTH_SHORT);
			message.show();
		}
		
	}
}
