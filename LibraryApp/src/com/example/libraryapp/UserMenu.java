package com.example.libraryapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class UserMenu extends Activity {
	
	private TextView welcome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_menu);
		Intent userMenu = getIntent();
		String uname = userMenu.getStringExtra("username");
		welcome = (TextView) findViewById(R.id.welcomeTxt);
		String conc = welcome.getText() + " " + uname;
		welcome.setText(conc);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_menu, menu);
		return true;
	}

}
