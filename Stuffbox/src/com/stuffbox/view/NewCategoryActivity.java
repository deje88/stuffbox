package com.stuffbox.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import com.stuffbox.R;
import com.stuffbox.R.id;
import com.stuffbox.R.layout;
import com.stuffbox.R.menu;
import com.stuffbox.controller.Controller;
import com.stuffbox.model.FeatureType;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Build;

public class NewCategoryActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_new);
		//Intent intent = getIntent();
		//((TextView)(findViewById(R.id.categoryText))).setText("Es wurde "+intent.getStringExtra(MainActivity.EXTRA_KATEGORIE_NAME)+ " gewählt!");
		
		ArrayList<FeatureType> types = Controller.getTypes();
		Spinner spinner = (Spinner) findViewById(R.id.spinner_new_category_icon);

		// holt alle Icon-Namen
        Field[] drawableFields = android.R.drawable.class.getFields();  
		String[] drawableIDs = new String[drawableFields.length];
		R.drawable drawableResources = new R.drawable();
		
        for(int i = 0 ;i < drawableFields.length;i++)
			try {
				drawableIDs[i] = ""+ drawableFields[i].getInt(drawableResources);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
		IconArrayAdapter adapter = new IconArrayAdapter(this, drawableIDs);
		spinner.setAdapter(adapter);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//Intent intent = getIntent();
		//String message = intent.getStringExtra(MainActivity.EXTRA_KATEGORIE_NAME);
		//getSupportActionBar().setTitle(message);

		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.category, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
