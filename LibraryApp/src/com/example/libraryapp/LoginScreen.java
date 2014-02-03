/*******************
 * Author:Rodios Michalis
 * Description:Class that manages the login screen activity and provides
 * three functions: login, register and password retrieve
 */

package com.example.libraryapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.view.View.OnClickListener;

public class LoginScreen extends Activity {

	//Declaration of Button variables
	Button btnLogin;
    Button Btnregister;
    Button passreset;
    EditText inputUsername;
    EditText inputPassword;
    private TextView loginErrorMsg;
    
    //Declaration of database table columns
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_USERNAME = "uname";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_main);
        
        //Gather login data
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        Btnregister = (Button) findViewById(R.id.button2);
        btnLogin = (Button) findViewById(R.id.btnToLogin);
        passreset = (Button)findViewById(R.id.passres);
        loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);
 
        //On click event for password retrieve
        passreset.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent myIntent = new Intent(view.getContext(), PasswordRetrieve.class);
        		startActivityForResult(myIntent, 0);
        		finish();
        	}});
        
        //On click event for registration
        Btnregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), RegisterScreen.class);
                startActivityForResult(myIntent, 0);
                finish();
             }});
        
        //On click event for Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View view) {
 
                if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
                {
                    NetAsync(view);
                }
                else if ( ( !inputUsername.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Password field empty", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !inputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Email field empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Email and Password field are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
    }
    
    //Async task that checks whether internet connection is working
    private class NetCheck extends AsyncTask<String, Void, Boolean>
    {
    	private ProgressDialog nDialog;
    	 
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(LoginScreen.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        
        @Override
        protected Boolean doInBackground(String... args){
        	//Checks the connection by hitting Google
        	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
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
                new ProcessLogin().execute();
            }
            else{
                nDialog.dismiss();
                loginErrorMsg.setText("Error in Network Connection");
            }
        }
    }
    
    //ASync task that creates and sends JSON request to the database and handles JSON response
    private class ProcessLogin extends AsyncTask<String, Void, JSONObject>
    {
    	private ProgressDialog pDialog;
    	 
        String username,password;
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
 
            inputUsername = (EditText) findViewById(R.id.username);
            inputPassword = (EditText) findViewById(R.id.password);
            username = inputUsername.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(LoginScreen.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        @Override
        protected JSONObject doInBackground(String... args) {
        	
        	//Calls function that logins user
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(username, password);
            return json;
        }
        
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
               if (json.getString(KEY_SUCCESS) != null) {
 
                    String res = json.getString(KEY_SUCCESS);
 
                    if(Integer.parseInt(res) == 1){
                        pDialog.setMessage("Loading User Space");
                        pDialog.setTitle("Getting Data");
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
                       /**
                        *If JSON array details are stored in SQlite it launches the User Panel.
                        **/
                        Intent upanel = new Intent(getApplicationContext(), UserMenu.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        upanel.putExtra("username", json_user.getString(KEY_USERNAME));
                        pDialog.dismiss();
                        startActivity(upanel);
                        /**
                         * Close Login Screen
                         **/
                        finish();
                    }else{
 
                        pDialog.dismiss();
                        loginErrorMsg.setText("Incorrect username/password");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
            	npe.printStackTrace();
            }
       }
    }
    
    //Executes network check
    public void NetAsync(View view){
        new NetCheck().execute();
    }

}
