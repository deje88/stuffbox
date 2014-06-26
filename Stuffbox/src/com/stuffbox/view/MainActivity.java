package com.stuffbox.view;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;
import android.widget.ListView;

import com.stuffbox.R;
import com.stuffbox.controller.Controller;
import com.stuffbox.model.Category;
import com.stuffbox.model.DatabaseHandler;
import com.stuffbox.model.Feature;
import com.stuffbox.model.Formular;
import com.stuffbox.model.Icon;

public class MainActivity extends ActionBarActivity {

	private ListView mainListView ;
	private CategoryArrayAdapter categoryAdapter ;
	private ImageButton btn ;
	
	public final static String EXTRA_KATEGORIE_NAME = "com.stuffbox.KATEGORIENAME";
	private static final String TAG = MainActivity.class.getSimpleName();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        Controller.getInstance(this);

     
        /*ArrayList<Object> os = new ArrayList<Object>();
        os.add(0);
        os.add(1);
        os.add(2);
        os.add(3);
        os.add(4);

        ArrayList<Object> ents = Controller.getEntities(DatabaseHandler.TABLE_CATEGORY, DatabaseHandler.KEY_ID, null, Category.class);
        
        		Category[] cats = new Category[ents.size()];
		
		for (int i = 0 ;i < ents.size();i++)
			cats[i] = (Category) ents.get(i);
        
        */
        
        ArrayList<Category> categories = Controller.getCategories(null);
        
	    //Controller.fillIconTableWithSomeIcons(this);    
        mainListView = (ListView) findViewById( R.id.mainListView );
        
        mainListView.setOnItemClickListener(new OnItemClickListener()
        {
	        @Override
	        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	        {
		        Intent intent = new Intent();		        
		        intent.setClassName(getPackageName(), CategoryActivity.class.getName());
		        intent.putExtra(EXTRA_KATEGORIE_NAME, mainListView.getAdapter().getItem(arg2).toString());
		        startActivity(intent);
	        }
        });
        
		Category[] cats = new Category[categories.size()];
		
		for (int i = 0 ;i < categories.size();i++)
			cats[i] = categories.get(i);

        categoryAdapter = new CategoryArrayAdapter (this, cats);
	    mainListView.setAdapter( categoryAdapter );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    public void openNewCategoryScreen(View view) {    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), NewCategoryActivity.class.getName());
        startActivity(intent);
    }	

    public void openCategoryScreen(View view) {    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), CategoryActivity.class.getName());
        startActivity(intent);
    }	
    
    public void openFormularScreen(View view) {    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), FormularActivity.class.getName());
        startActivity(intent);
    }	
    
    public void openFormularNewScreen(View view){    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), NewFormularActivity.class.getName());
        startActivity(intent);
    }	
    
    public void openDetailScreen(View view) {    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), DetailActivity.class.getName());
        startActivity(intent);
    }	
    
    public void openAbzeichenScreen(View view) {    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), BadgeActivity.class.getName());
        startActivity(intent);
    }	
    
    public void openArtenScreen(View view) {    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), FeatureActivity.class.getName());
        startActivity(intent);
    }	    

    public void openFotoScreen(View view) {    	
    	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
    	startActivityForResult(cameraIntent, 42);
    }	
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	try {
        if (requestCode == 42) {
            Bitmap thumb = (Bitmap) data.getExtras().get("data"); 
            btn = (ImageButton)findViewById(R.id.imageButtonFoto);
            btn.setImageBitmap(thumb);
            OutputStream stream = new FileOutputStream("/sdcard/test.jpg");
            thumb.compress(CompressFormat.JPEG, 100, stream);
        }
    	}catch (Exception e) {
    		Log.e(TAG, "Fehler beim Fotografieren: ", e);
    	}
    }	    
    
    public void openTest(View view) {    	
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), ListViewDraggingAnimation.class.getName());
        startActivity(intent);
    }	
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
