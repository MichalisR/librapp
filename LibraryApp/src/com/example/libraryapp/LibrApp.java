package com.example.libraryapp;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class LibrApp extends Activity {

	
	protected boolean _active = true;
	protected int _splashTime = 3000; // time to display the splash screen in ms

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 

                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_libr_app);
        
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (Exception e) {

                } finally {

                    startActivity(new Intent(LibrApp.this,
                            LoginScreen.class));
                    finish();
                }
            };
                 };
        splashTread.start();
    }
     

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_libr_app, menu);
        return true;
    }
}
