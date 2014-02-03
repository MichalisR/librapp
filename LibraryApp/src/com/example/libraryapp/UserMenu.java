package com.example.libraryapp;

import com.example.libraryapp.php_con.UserFunctions;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class UserMenu extends Activity {
	
	private TextView welcome;
	private Button logout,book_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_user_menu);
		
		Intent userMenu = getIntent();
		String uname = userMenu.getStringExtra("username");
		welcome = (TextView) findViewById(R.id.welcomeTxt);
		String conc = welcome.getText() + " " + uname;
		welcome.setText(conc);
		
		logout = (Button) findViewById(R.id.logoutBtn);
		book_search = (Button) findViewById(R.id.searchBtn);
		
		//Logout Function
	    logout.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View arg0) {
	    		UserFunctions logout = new UserFunctions();
	            logout.logoutUser(getApplicationContext());
	            Intent login = new Intent(getApplicationContext(), LoginScreen.class);
	            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	             startActivity(login);
	             finish();
	        }
	    });
	    
	    //Book search
	    book_search.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				Intent bsearch = new Intent(getApplicationContext(),BookSearch.class);
				bsearch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(bsearch);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_menu, menu);
		return true;
	}

}
