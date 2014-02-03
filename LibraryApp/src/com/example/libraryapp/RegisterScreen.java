package com.example.libraryapp;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.libraryapp.php_con.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterScreen extends Activity {
	
	private EditText fname,lname,uname,password,email;
	private TextView birthday;
	private SecureRandom random = new SecureRandom();
	
	//Declaration of Button variables
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";
 
    //Declaration of database table columns
    EditText inputFirstName;
    EditText inputLastName;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
    TextView registerErrorMsg;
	
	static final int DATE_DIALOG_ID = 999;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_screen);
        
        inputFirstName = (EditText) findViewById(R.id.firstname);
        inputLastName = (EditText) findViewById(R.id.lastname);
        inputUsername = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
        
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputFirstName.getText().toString().equals("")) && ( !inputLastName.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals("")) )
                {
                    if ( inputUsername.getText().toString().length() > 4 ){
                    	NetAsync(view);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
			}
		});
	}
	
	/**
     * Async Task to check whether internet connection is working
     **/
 
    private class NetCheck extends AsyncTask<String, Void, Boolean>
    {
        private ProgressDialog nDialog;
 
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(RegisterScreen.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
 
        @Override
        protected Boolean doInBackground(String... args){
 
			/**
			 * Gets current device state and checks for working internet connection by trying Google.
			 **/
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
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                registerErrorMsg.setText("Error in Network Connection");
            }
        }
    }
 
    private class ProcessRegister extends AsyncTask<String, Void, JSONObject> {
 
		/**
		 * Defining Process dialog
		 **/
        private ProgressDialog pDialog;
 
        String email,password,fname,lname,uname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputUsername = (EditText) findViewById(R.id.username);
            inputPassword = (EditText) findViewById(R.id.password);
               fname = inputFirstName.getText().toString();
               lname = inputLastName.getText().toString();
                email = inputEmail.getText().toString();
                uname= inputUsername.getText().toString();
                password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(RegisterScreen.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        @Override
        protected JSONObject doInBackground(String... args) {
 
        UserFunctions userFunction = new UserFunctions();
        JSONObject json = userFunction.registerUser(fname, lname, email, uname, password);
 
            return json;
 
        }
       @Override
        protected void onPostExecute(JSONObject json) {
       /**
        * Checks for success message.
        **/
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        registerErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS);
 
                        String red = json.getString(KEY_ERROR);
 
                        if(Integer.parseInt(res) == 1){
                            pDialog.setTitle("Getting Data");
                            pDialog.setMessage("Loading Info");
 
                            registerErrorMsg.setText("Successfully Registered");
 
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            JSONObject json_user = json.getJSONObject("user");
 
                            /**
                             * Removes all the previous data in the SQlite database
                             **/
 
                            UserFunctions logout = new UserFunctions();
                            logout.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
                            /**
                             * Stores registered data in SQlite Database
                             * Launch Registered screen
                             **/
 
                            //Intent registered = new Intent(getApplicationContext(), Registered.class);
 
                            /**
                             * Close all views before launching Registered screen
                            **/
                            //registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pDialog.dismiss();
                            //startActivity(registered);
 
                              finish();
                        }
 
                        else if (Integer.parseInt(red) ==2){
                            pDialog.dismiss();
                            registerErrorMsg.setText("User already exists");
                        }
                        else if (Integer.parseInt(red) ==3){
                            pDialog.dismiss();
                            registerErrorMsg.setText("Invalid Email id");
                        }
 
                    }
 
                        else{
                        pDialog.dismiss();
 
                            registerErrorMsg.setText("Error occured in registration");
                        }
 
                } catch (JSONException e) {
                    e.printStackTrace();
 
                }
            }}
        public void NetAsync(View view){
            new NetCheck().execute();
        }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_screen, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	/*public void addListenerOnButton() {
		fname = (EditText) findViewById(R.id.firstname);	
		lname = (EditText) findViewById(R.id.lastname);
		uname = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		email = (EditText) findViewById(R.id.email);
		birthday = (TextView) findViewById(R.id.tvDate);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		
		btnRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				 String txt = fname.getText().toString();
				 String txt2 = lname.getText().toString();
				 String txt3 = uname.getText().toString();
				 String txt4 = password.getText().toString();
				 String txt5 = email.getText().toString();
				 String txt6 = birthday.getText().toString();
				 String salt = "";
				 StringBuffer sb = new StringBuffer();
				 try {
					 salt = new BigInteger(130, random).toString(32);
					 MessageDigest md = MessageDigest.getInstance("SHA-256");
					 md.update(salt.getBytes());
					 byte byteData[] = md.digest(txt4.getBytes());
			         for (int i = 0; i < byteData.length; i++) {
			        	 sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			         }
			         String mix = txt+" "+txt2+" "+txt3+" "+salt+" "+sb+" "+txt5+" "+txt6;
			         Toast.makeText(RegisterScreen.this,mix.toString(),
			    			Toast.LENGTH_SHORT).show();
		    	  }
				  catch (NoSuchAlgorithmException e) {
		    		  e.printStackTrace();
		    	  }
				 
				 // declare parameters that are passed to PHP script 
		          ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		          postParameters.add(new BasicNameValuePair("firstname",txt));
		          postParameters.add(new BasicNameValuePair("lastname",txt2));
		          postParameters.add(new BasicNameValuePair("username",txt3));
		          postParameters.add(new BasicNameValuePair("salt",salt));
		          postParameters.add(new BasicNameValuePair("password",sb.toString()));
		          postParameters.add(new BasicNameValuePair("email",txt5));
		          postParameters.add(new BasicNameValuePair("birthday",txt6));
		          
		         
		          
		          try{
		        	 String response = HTTPClientConnect.executeHttpPost(
		        	  "http://librapp.com/registerUser.php", postParameters);
		        		     

		          }
		          catch(Exception e) {
		        	  Log.e("log_tag","Error in http connection!!" + e.toString()); 
		          }
			}
			
		});
	}*/
}
