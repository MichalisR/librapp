package com.example.libraryapp;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

@SuppressLint("NewApi")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	TextView mTextView;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    
   /* @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

	            // R.layout.my_layout - that's the layout where your textview is placed
	    View view = inflater.inflate(R.layout.activity_register_screen, container, false);
	    mTextView = (TextView) view.findViewById(R.id.tvDate);
	    // you can use your textview.
	    return view;
	}*/

    public void onDateSet(DatePicker view, int year, int month, int day) {
    	int syear = year;
		int smonth = month;
		int sday = day;
		// set selected date into textview
		mTextView = (TextView) ((RegisterScreen)getActivity()).findViewById(R.id.tvDate);
		mTextView.setText(new StringBuilder().append(smonth + 1)
		   .append("-").append(sday).append("-").append(syear)
		   .append(" "));
    }
}