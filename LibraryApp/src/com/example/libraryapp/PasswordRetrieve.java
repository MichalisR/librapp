package com.example.libraryapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.libraryapp.php_con.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PasswordRetrieve extends Activity {

	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private Button btnRecover,btnLogin;
	private EditText email;
	private TextView alert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_password_retrieve);
		
		btnLogin = (Button) findViewById(R.id.btnToLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginScreen.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
 
        });
		
		btnRecover = (Button) findViewById(R.id.btnPwdRetrieve);
		email = (EditText) findViewById(R.id.email_r);
		alert = (TextView) findViewById(R.id.alert_pass);
		btnRecover.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				NetAsync(view);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.password_retrieve, menu);
		return true;
	}
	
	private class NetCheck extends AsyncTask<String, Void, Boolean>
	{
		private ProgressDialog nDialog;
		 
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(PasswordRetrieve.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        
        @Override
        protected Boolean doInBackground(String... args)
        {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) 
                    {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
        
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                alert.setText("Error in Network Connection");
            }
        }
	}
	
	private class ProcessRegister extends AsyncTask<String, Void, JSONObject> {
		 
        private ProgressDialog pDialog;

        String forgotpassword;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            forgotpassword = email.getText().toString();

            pDialog = new ProgressDialog(PasswordRetrieve.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.forPass(forgotpassword);
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    alert.setText("");
                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);

                    if(Integer.parseInt(res) == 1){
                       pDialog.dismiss();
                       alert.setText("A recovery email is sent to you, see it for more details.");

                    }
                    else if (Integer.parseInt(red) == 2)
                    {    
                    	pDialog.dismiss();
                    	alert.setText("Your email does not exist in our database.");
                    }
                    else {
                        pDialog.dismiss();
                        alert.setText("Error occured in changing Password");
                    }

                }
            }
            catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }
	
	public void NetAsync(View view) {
		new NetCheck().execute();
	}

}
