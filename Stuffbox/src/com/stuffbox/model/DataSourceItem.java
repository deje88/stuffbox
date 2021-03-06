package com.stuffbox.model;

import java.util.ArrayList;

import com.stuffbox.controller.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataSourceItem {
    /**
     * Erstellt die Tabelle Item auf der Datenbank und die Verknuepfungstabelle Item-Eigenschaft
     * @param database
     */
    public void createItemTable(SQLiteDatabase db){
    	//Erstellt die Item Tabelle
        String CREATE_ITEM_TABLE = "CREATE TABLE " + DatabaseHandler.TABLE_ITEM + "("
                + DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
        		+ DatabaseHandler.KEY_NAME + " TEXT," 
                + DatabaseHandler.TABLE_FORMULAR + " INTEGER, " + 
                "FOREIGN KEY(" + DatabaseHandler.TABLE_FORMULAR + ") REFERENCES " 
    			+ DatabaseHandler.TABLE_FORMULAR + "(" + DatabaseHandler.KEY_ID + ")"  + " ON DELETE CASCADE " + ")";
        db.execSQL(CREATE_ITEM_TABLE);
        
        //Erstellt die Item-Eigenschaft-Wert Verknuepfungstabelle
        String CREATE_FORMULAR_ITEM_TABLE = "CREATE TABLE " + DatabaseHandler.TABLE_FEATURE_ITEM + "("+ 
        		//create column formular
        		DatabaseHandler.TABLE_FEATURE + " INTEGER," + 
        		//create column item
        		DatabaseHandler.TABLE_ITEM + " INTEGER," + 
        		//create column wert
        		DatabaseHandler.KEY_VALUE + " STRING," + 
        		"PRIMARY KEY(" + DatabaseHandler.TABLE_FEATURE + "," + DatabaseHandler.TABLE_ITEM + ")," +
        		//add foreign key to table formular
                "FOREIGN KEY(" + DatabaseHandler.TABLE_FEATURE + ") REFERENCES " 
        			+ DatabaseHandler.TABLE_FEATURE + "(" + DatabaseHandler.KEY_ID + ")"  + " ON DELETE CASCADE " +
        		//add foreign key to table item
                "FOREIGN KEY(" + DatabaseHandler.TABLE_ITEM + ") REFERENCES " 
        			+ DatabaseHandler.TABLE_ITEM + "(" + DatabaseHandler.KEY_ID + ")"  + " ON DELETE CASCADE " +")";
        db.execSQL(CREATE_FORMULAR_ITEM_TABLE);
        
        //Erstellt die Item-Kategorie-Wert Verknuepfungstabelle
        String CREATE_CATEGORY_ITEM_TABLE = "CREATE TABLE " + DatabaseHandler.TABLE_CATEGORY_ITEM + "("+ 
        		//create column kategorie
        		DatabaseHandler.TABLE_CATEGORY + " INTEGER," + 
        		//create column item
        		DatabaseHandler.TABLE_ITEM + " INTEGER," +
        		"PRIMARY KEY(" + DatabaseHandler.TABLE_CATEGORY + "," + DatabaseHandler.TABLE_ITEM + ")," +
        		//add foreign key to table kategorie
                "FOREIGN KEY(" + DatabaseHandler.TABLE_CATEGORY + ") REFERENCES " 
        			+ DatabaseHandler.TABLE_CATEGORY + "(" + DatabaseHandler.KEY_ID + ")" + " ON DELETE CASCADE " +
        		//add foreign key to table item
                "FOREIGN KEY(" + DatabaseHandler.TABLE_ITEM + ") REFERENCES " 
        			+ DatabaseHandler.TABLE_ITEM + "(" + DatabaseHandler.KEY_ID + ")"  + " ON DELETE CASCADE " +")";
        db.execSQL(CREATE_CATEGORY_ITEM_TABLE); 
    }
	
    /**
     * Fuegt ein Item der Datenbank hinzu.
     * @param name
     */
    public Item insertOrUpdateItem(SQLiteDatabase database, Item item){
    	ContentValues values = new ContentValues();
    	values.put(DatabaseHandler.KEY_NAME, item.getName());
    	
    	boolean isUpdate = item.getId() != DatabaseHandler.INITIAL_ID;
    	Item oldItem = null;
    	if(isUpdate){
	    	ArrayList<Long> selectItemIds = new ArrayList<Long>();
	    	selectItemIds.add(item.getId());
	    	oldItem = getItems(database, selectItemIds).get(0);
    	}
    	
    	//Item anlegen/aktualieren
    	if(!isUpdate){
        	values.put(DatabaseHandler.TABLE_FORMULAR, item.getFormular().getId());
        	
    		long rowid = DatabaseHandler.insertIntoDB(database, DatabaseHandler.TABLE_ITEM, values, item.getName());
            if (rowid <= 0){
            	return null;
            }
    		item.setId(rowid);
    	}else{
        	ContentValues whereValues = new ContentValues();
        	whereValues.put(DatabaseHandler.KEY_ID, item.getId());
        	
        	DatabaseHandler.updateEntryInDB(database, DatabaseHandler.TABLE_ITEM, values, whereValues, item.getName());
    	}
        
    	//Formular anlegen/aktualiseren
    	for (Feature feature : item.getFormular().getFeatures()) {
    		if(!isUpdate){
    			insertFeatureOfItem(database, feature, item);
    		}else{
    			updateFeatureOfItem(database, feature, item);
    		}
		}
    	//Kategorien anlegen/aktualiseren
    	if(!isUpdate){
	    	for(Category category : item.getCategories()){
		    	insertCategoryOfItem(database, category, item);
	    	}
    	}else{
			//update Kategorie
	        for(Category category : item.getCategories()){
	        	Category categoryFound = null;
	        	//suche in alten Kategorie nach den aktuellen
	        	for(Category oldCateogry : oldItem.getCategories()){
		        	if(category.getId() == oldCateogry.getId()){
		        		categoryFound = oldCateogry;
		        		break;
		        	}
	        	}
	        	if(categoryFound != null){
	        		//wenn gefunden loesche es aus der liste
	        		oldItem.getCategories().remove(categoryFound);
	        	}else{
	        		//wenn nicht gefunden fuege es auf der Datenbank hinzu
	        		insertCategoryOfItem(database, category, item);
	        	}
	        }
	        //loesche verbleibende Features aus der Liste
	        ArrayList<Long> deleteCategoryIds = new ArrayList<Long>();
	        for(Category oldCategory : oldItem.getCategories()){
	        	deleteCategoryIds.add(oldCategory.getId());	
	        }
	        if(!deleteCategoryIds.isEmpty()){
				String whereStatement = DatabaseHandler.createWhereStatementFromIDList(deleteCategoryIds, DatabaseHandler.TABLE_CATEGORY);
				whereStatement = "(" + whereStatement + ") " + DatabaseHandler.SQL_AND + " " +  DatabaseHandler.TABLE_ITEM + " == " + item.getId();
				DatabaseHandler.deletefromDB(database, DatabaseHandler.TABLE_CATEGORY_ITEM, whereStatement);		
	        }
		}
    	return item;
    }
    
    /**
     * Fuegt den Wert einer Eigenschaft eines Items in der Verknuepfungstabelle auf der Datenbank hinzu

     * @param database
     * @param feature
     * @param item
     */
    public void insertFeatureOfItem(SQLiteDatabase database, Feature feature, Item item){
    	ContentValues values = new ContentValues();
    	values.put(DatabaseHandler.TABLE_FEATURE, feature.getId());
    	values.put(DatabaseHandler.KEY_VALUE, DataSourceFeature.getDatabaseStringOfValue(feature.getValue(), feature.getType()));
    	values.put(DatabaseHandler.TABLE_ITEM, item.getId());
    	
    	DatabaseHandler.insertIntoDB(database, DatabaseHandler.TABLE_FEATURE_ITEM, values, item.getName());
    }

	/**
	 * Loescht mehrere Items
	 *
	 * @param item
	 * @return Ob es erfolgreich geloescht wurde 
	 */
	public boolean deleteItems(SQLiteDatabase database, ArrayList<Item> items) {
		ArrayList<Long> selectItemsIds = new ArrayList<Long>();
		for (Item item : items) {
			selectItemsIds.add(item.getId());	
		}
		String whereStatement = DatabaseHandler.createWhereStatementFromIDList(selectItemsIds,null);
	
		long delRows = DatabaseHandler.deletefromDB(database, DatabaseHandler.TABLE_ITEM, whereStatement);		
		return delRows == 1 ? true : false;
	}
    
    /**
     * Fuegt den Wert einer Eigenschaft eines Items in der Verknuepfungstabelle auf der Datenbank hinzu

     * @param database
     * @param feature
     * @param item
     */
    public void updateFeatureOfItem(SQLiteDatabase database, Feature feature, Item item){
    	ContentValues values = new ContentValues();
    	values.put(DatabaseHandler.KEY_VALUE, DataSourceFeature.getDatabaseStringOfValue(feature.getValue(), feature.getType()));
    	
    	ContentValues whereValues = new ContentValues();
    	whereValues.put(DatabaseHandler.TABLE_FEATURE, feature.getId());
    	
    	String whereClause = DatabaseHandler.createWhereStatementFromContentValues(whereValues); 
    	whereClause = "(" + whereClause + ") AND " + DatabaseHandler.TABLE_ITEM + " == " + item.getId();
    	
    	DatabaseHandler.updateEntryInDB(database, DatabaseHandler.TABLE_FEATURE_ITEM, values, whereClause, item.getName());
    }    
    
	/**
	 * Loescht die Zuorndung eines features zu einem Item
	 * 
	 * @param feature
	 * @param item bei null wird das feature bei allen items gel�scht
	 * @return Ob es erfolgreich geloescht wurde 
	 */
	public boolean deleteCategory(SQLiteDatabase database, Feature feature, Item item) {
		ContentValues whereValues = new ContentValues();
		whereValues.put(DatabaseHandler.TABLE_FEATURE, feature.getId());
		if(item != null){
			whereValues.put(DatabaseHandler.TABLE_ITEM, item.getId());
		}
		long delRows = DatabaseHandler.deletefromDB(database, DatabaseHandler.TABLE_FEATURE_ITEM, whereValues);
		return delRows == 1 ? true : false;
	}
    
    /**
     * Fuegt eine Kategorie der ein Item zuegehoert in der Verknuepfungstabelle auf der Datenbank hinzu

     * @param database
     * @param feature
     * @param item
     */
    public void insertCategoryOfItem(SQLiteDatabase database, Category category, Item item){
    	ContentValues values = new ContentValues();
    	values.put(DatabaseHandler.TABLE_CATEGORY, category.getId());
    	values.put(DatabaseHandler.TABLE_ITEM, item.getId());
    	
    	DatabaseHandler.insertIntoDB(database, DatabaseHandler.TABLE_CATEGORY_ITEM, values, item.getName());
    }
    /**
     * Gibt eine Liste aller Items zurueck, , deren ids in der id Liste enthalten ist
     * @param database
     * @param selectIds Liste aller zu selektierenden Ids (bei null werden alle geladen)
     * @return
     */
    public ArrayList<Item> getItems( SQLiteDatabase database, 
			 			  ArrayList<Long> selectIds){
    	
		ArrayList<Item> items = new ArrayList<Item>();
		if(selectIds != null && selectIds.isEmpty()){
			return items;
		}
    	
    	//erstelle where statement
    	String whereStatement = DatabaseHandler.createWhereStatementFromIDList(selectIds,null);
    	
    	//select types from database
    	Cursor cursor = database.query(DatabaseHandler.TABLE_ITEM, null, whereStatement, null, null, null, null);
		
    	return getItemsFromCursor(database, cursor);
    }
    
    /**
     * Selektiert die Werte der Eigenschaften eines Eintrages aus der Datenbank.
     * @param database
     * @param itemid
     * @param features
     */
    public void setValuesOfFeatures( SQLiteDatabase database,
    								 	 long itemid,
    								 	 ArrayList<Feature> features ){
    	//erstelle where statement
    	StringBuilder whereStatement = new StringBuilder();
		whereStatement.append(" ");
		whereStatement.append(DatabaseHandler.TABLE_ITEM);
		whereStatement.append(" = ");
		whereStatement.append(itemid);
		whereStatement.append(" ");
		whereStatement.append(DatabaseHandler.SQL_AND);
		
    	ArrayList<Long> selectFeatureIds = new ArrayList<Long>();
    	for(Feature feature : features){
    		selectFeatureIds.add(feature.getId());
    	}
		whereStatement.append("(");
		whereStatement.append(DatabaseHandler.createWhereStatementFromIDList(selectFeatureIds,DatabaseHandler.TABLE_FEATURE));
		whereStatement.append(")");
    	
    	
    	//select types from database
    	Cursor cursor = database.query(DatabaseHandler.TABLE_FEATURE_ITEM, null, whereStatement.toString(), null, null, null, null);
    	
		//Werte in Feature speichern
		if (cursor != null && cursor.moveToFirst()) {
			do {
				int featureid = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_FEATURE)));
				String value = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_VALUE));
				if(value == null){
					//TODO Exception/Ausgabe
				}
				
				boolean featureWasFound = false;
				for (Feature feature : features) {
					if(feature.getId() == featureid){
						Object valueAsObject = DataSourceFeature.getValueFromDatabaseString(value, feature.getType());
						feature.setValue(valueAsObject);
						featureWasFound = true;
						break;
					}
				}
				
				if (!featureWasFound) {
					//TODO Exception/Ausgabe
				}
			} while (cursor.moveToNext());
		}
		
		cursor.close();
    }
    
    /**
     * Gibt eine Liste aller Items zurueck, die einer Kategorie enthalten sind.
     * @param database
     * @param categoryID 
     * @return Die Items in der spezifizierten Kategorie
     */
    public ArrayList<Item> getItemsOfACategory( SQLiteDatabase database, long categoryID) {
    	    	
    	ContentValues whereValues = new ContentValues();
    	whereValues.put(DatabaseHandler.TABLE_CATEGORY, categoryID);
    	
    	String whereStatement = DatabaseHandler.createWhereStatementFromContentValues(whereValues);
    	
    	//select types from database
    	Cursor cursor = database.query(DatabaseHandler.TABLE_CATEGORY_ITEM, null, whereStatement, null, null, null, null);
		
		//add all types to list
		ArrayList<Long> anIdinAList = new ArrayList<Long>();
		if (cursor.moveToFirst()) {
			do {
				long itemId = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.TABLE_ITEM));
				anIdinAList.add(itemId);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return this.getItems(database, anIdinAList);
    }
    
    public ArrayList<Item> getItemsFromWordMatches(SQLiteDatabase database, String query, String[] columns) {
 	
    	Cursor cursor = database.rawQuery("SELECT  * FROM " +DatabaseHandler.TABLE_ITEM + " WHERE Name LIKE '%" + query + "%' ",null); 
        
        return getItemsFromCursor(database, cursor);
    }

    private ArrayList<Item> getItemsFromCursor(SQLiteDatabase database, Cursor cursor) {
		ArrayList<Item> items = new ArrayList<Item>();
		
		//add all types to list
		if (cursor.moveToFirst()) {
			do {
				long itemId = Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_ID)));
				String itemName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_NAME));
				long formularId = Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_FORMULAR)));
				
				//selektiere Formular fuer item
				ArrayList<Long> selectFormularId = new ArrayList<Long>();
				selectFormularId.add(formularId);
				Formular formular = Controller.getInstance().getFormulars(selectFormularId).get(0);
				setValuesOfFeatures(database, itemId, formular.getFeatures());
				
				//erhalte ids verknuepfter kategorien aus der verknuepfungstabelle
				ArrayList<Long> selectedFeatureIds = DatabaseHandler.getEntriesOfConjunctionTable(database, 
						 itemId, 
						 DatabaseHandler.TABLE_ITEM, 
						 DatabaseHandler.TABLE_CATEGORY,
						 DatabaseHandler.TABLE_CATEGORY_ITEM);
				//erhalte daten der kategrien aller verknuepften kategorien aus der kategorietabelle
				ArrayList<Category> categories = Controller.getInstance().getCategories(selectedFeatureIds);
				
				//Item erstellen
				Item item = new Item(itemId, itemName, formular, categories);
				
				// Adding type to list
				items.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return items;
    }
    
	/**
	 * Loescht ein Item
	 * 
	 * @param database
	 * @param itemToDelete
	 * @return Ob es erfolgreich geloescht wurde 
	 */
	public boolean deleteItem(SQLiteDatabase database, Item itemToDelete) {
		ContentValues whereValues = new ContentValues();
		whereValues.put(DatabaseHandler.KEY_ID, itemToDelete.getId());
		long delRows = DatabaseHandler.deletefromDB(database, DatabaseHandler.TABLE_ITEM, whereValues);
		return delRows == 1 ? true : false;
	}
	
	/**
	 * Loescht alle Items einer Kategorie
	 * 
	 * @param database
	 * @param categoryID
	 * @return Ob es erfolgreich geloescht wurde 
	 */
	public boolean deleteItemsOfCategory(SQLiteDatabase database, long categoryID) {
		boolean allItemsDeleted = true;
		ArrayList<Item> itemsToDelete = getItemsOfACategory(database, categoryID);
		for (Item itemToDelete : itemsToDelete)
			if (!deleteItem(database, itemToDelete))
				allItemsDeleted = false;
		return allItemsDeleted;
	}	
}
