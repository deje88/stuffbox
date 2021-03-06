package com.stuffbox.view;

import java.util.ArrayList;

import com.stuffbox.R;
import com.stuffbox.controller.Controller;
import com.stuffbox.model.Category;
import com.stuffbox.model.DataSourceCategory;
import com.stuffbox.model.Icon;
import com.stuffbox.model.Item;
import com.stuffbox.view.helper.Utility;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class ListCategoriesActivity extends ActionBarActivity {
	
	private ListView categoryListView ;
	private CategoryArrayAdapter categoryAdapter ;
	private ListView itemListView ;
	private ItemArrayAdapter itemAdapter ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_list);
		
		Controller.getInstance(this).init();
		
		// Anzeigen der Unterkategorien:
		categoryListView = (ListView) findViewById( R.id.categoryListView );
		categoryListView.setOnItemClickListener(new OnItemClickListener()
        {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	        {		        
				Category clickedCategory = (Category) parent.getItemAtPosition(position);
		        Controller.getInstance().setCurrentCategory(clickedCategory);
		        Intent intent = new Intent();		        
		        intent.setClassName(getPackageName(), ListCategoriesActivity.class.getName());
		        startActivity(intent);
	        }
        });
		
        Category currentCategory = Controller.getInstance().getCurrentCategory();
        ArrayList<Category> subCategories = Controller.getInstance().getSubCategories(currentCategory.getId());
        
        //Anzeigen der Kategorien:
        categoryAdapter = new CategoryArrayAdapter (this, subCategories);
        categoryListView.setAdapter( categoryAdapter );	
        
        //Anzeigen Kategorie anlegen Hilfe, falls keine Kategorie vorhanden
		if(subCategories != null && !subCategories.isEmpty()){
			LinearLayout layoutNewCategoryInListview 
			= (LinearLayout) findViewById( R.id.linearlayout_new_category_in_listview );
			layoutNewCategoryInListview.setVisibility(View.GONE);
		}
        
        // Anzeigen der Items:
        itemListView = (ListView) findViewById( R.id.itemListView );
        itemListView.setOnItemClickListener(new OnItemClickListener()
        {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	        {		        
				Item clickedItem = (Item) parent.getItemAtPosition(position);
				Controller.getInstance().setCurrentItem(clickedItem);
		        Intent intent = new Intent();        
		        intent.setClassName(getPackageName(), DetailItemActivity.class.getName());
		        startActivity(intent);
	        }
        });

        ArrayList<Item> allItems = Controller.getInstance().getItemsOfACategory(Controller.getInstance().getCurrentCategory().getId());
        itemAdapter = new ItemArrayAdapter (this, allItems);
        itemListView.setAdapter( itemAdapter );	
        
	    // Groesse der Listen anhand der Anzahl der Eigenschaften neu setzen.
        Utility.setListViewHeightBasedOnChildren(itemListView, 0);
        Utility.setListViewHeightBasedOnChildren(categoryListView,0);
        
        // Den Divider zwischen der Kategorie- und der Item-Liste verschwinden lassen, wenn keine 
        // Kategorien oder Items vorhanden sind
	    if (subCategories.size() == 0 || allItems.size() == 0)
	    {  
	    	View dividerBetweenCategoriesAndItems= (View) findViewById(R.id.dividerBetweenCategoriesAndItems);
	    	dividerBetweenCategoriesAndItems.setVisibility(View.INVISIBLE);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		Category currentCategory = Controller.getInstance().getCurrentCategory();
		getSupportActionBar().setTitle(currentCategory.getName());

		Icon icon = currentCategory.getIcon();
		if (icon !=null)
			getSupportActionBar().setIcon(icon.getDrawableId());

		if (currentCategory.getName().equals(DataSourceCategory.ROOT_CATEGORY)){
			getMenuInflater().inflate(R.menu.list_categories_start, menu);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}else{
			getMenuInflater().inflate(R.menu.list_categories, menu);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		getMenuInflater().inflate(R.menu.choose_items, menu);
		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
	    
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
	    switch (itemId) {
	        case R.id.menu_new_item:
	        	onNewItem();
	            return true;
	        case R.id.menu_new_categorie:
	        	onNewCategory(null);
	        	return true;
	        case R.id.menu_edit_category:
	        	onEdit();
	            return true;
	        case R.id.menu_badge:
	            Intent intent = new Intent();        
	            intent.setClassName(getPackageName(), BadgeActivity.class.getName());
	            startActivity(intent);
	            return true;
	            // Respond to the action bar's Up/Home button
	        case android.R.id.home:
	        	if(!Controller.getInstance().getCurrentCategory().getName().equals(DataSourceCategory.ROOT_CATEGORY)){
	        		onBackPressed();
	        	}
	            return true;
	        case R.id.action_change_features:
	            Intent intentChooseFeatures = new Intent();        
	            intentChooseFeatures.setClassName(getPackageName(), ListFeatureActivity.class.getName());
	            startActivity(intentChooseFeatures);
	        	return true;
	        case R.id.action_change_formulars:
	            Intent intentChooseFormulars = new Intent();        
	            intentChooseFormulars.setClassName(getPackageName(), ListFormularActivity.class.getName());
	            intentChooseFormulars.putExtra(ListFormularActivity.PURPOSE_IS_CHOOSING_FOR_UPDATE, true);
	            startActivity(intentChooseFormulars);
	        	return true;
	        case R.id.action_add_debug_entries:
	        	onInsertDebugEntries();
	        	return true;
	        case R.id.action_search:
	            // search action
	        	onSearchRequested();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }		
	}
	
	@Override
	public boolean onSearchRequested(){
    	Intent intentSearchItem = new Intent();        
    	intentSearchItem.setClassName(getPackageName(), SearchableActivity.class.getName());
	    startActivity(intentSearchItem);
        return true;
	}
	
	/**
	 * aendert die Kategorie
	 */
	public void onEdit () {
        Intent intent = new Intent();        
        intent.putExtra(Controller.EXTRA_EDIT_CATEGORY, Controller.getInstance().getCurrentCategory());
        intent.setClassName(getPackageName(), NewCategoryActivity.class.getName());
		startActivity(intent);		
	}
	
	/**
	 * Erstellt eine neue Kategorie (zunächst Formular-Auswahl für das neue Item)
	 */
	public void onNewItem () {
        Intent intent = new Intent();        
        intent.setClassName(getPackageName(), ListFormularActivity.class.getName());
        startActivity(intent);
	}	
	
    public void onNewCategory(View view) { 
        Intent intent = new Intent();   
        intent.setClassName(getPackageName(), NewCategoryActivity.class.getName());
        startActivity(intent);
    }	
    
    @Override
    public void onBackPressed(){
    	if(!Controller.getInstance().getCurrentCategory().getName().equals(DataSourceCategory.ROOT_CATEGORY)){
	    	Category preCategory = Controller.getInstance().getPreCategoryId(Controller.getInstance().getCurrentCategory());
			Controller.getInstance().setCurrentCategory(preCategory);		
	        Intent intent = new Intent();   
	        intent.setClassName(getPackageName(), ListCategoriesActivity.class.getName());
	        startActivity(intent);				
    	}
    	finish();
    }
    
    public void onInsertDebugEntries(){
    	Controller.getInstance().insertDebugEntries(this);
    	Category rootCategory = Controller.getInstance().getCurrentCategory();
		Controller.getInstance().setCurrentCategory(rootCategory);	
        Intent intentToRoot = new Intent();   
        intentToRoot.setClassName(getPackageName(), ListCategoriesActivity.class.getName());
        startActivity(intentToRoot);	
        finish();
    }
}
