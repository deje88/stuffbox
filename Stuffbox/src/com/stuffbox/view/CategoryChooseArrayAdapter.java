package com.stuffbox.view;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.stuffbox.R;
import com.stuffbox.controller.Controller;
import com.stuffbox.model.Category;
import com.stuffbox.model.Icon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryChooseArrayAdapter extends ArrayAdapter<Category> {
	private final Context context;
	private final ArrayList<Category> values;
	private ArrayList<Category> selectedvalues = new ArrayList<Category>();

	public CategoryChooseArrayAdapter(Context context, ArrayList<Category> values) {
		super(context, R.layout.category_choose_row, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.category_choose_row, parent, false);
				
		//zeige Name der Kategorie
		TextView mainText = (TextView) rowView.findViewById(R.id.cat_row_text1);
		mainText.setText(values.get(position).getName());
		
		//zeige Icon der Kategorie an
		ImageView imageView = (ImageView) rowView.findViewById(R.id.cat_row_icon);
		
		Icon icon = values.get(position).getIcon();

		// TODO .. dat kann man schöner machen (default icon)
		String iconName = null;
		if(icon == null || !icon.getName().startsWith("category_icon_")){
			iconName = "category_icon_default";
		}else{
			iconName = icon.getName();
		}
		
		imageView.setImageResource(values.get(position).getIcon().getDrawableId());
		CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.cat_row_checkbox);
		
		// TODO Aktuelle ID sollte angeklickt und am besten vorne sein,
		// beides klappt noch nicht.
		if (values.get(position).getId() == Controller.getInstance().getCurrentCategory().getId()) {
			checkbox.setSelected(true);
			rowView.bringToFront();
		}
		
        rowView.setOnClickListener(new View.OnClickListener()
        {
    		CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.cat_row_checkbox);
    		
            @Override
            public void onClick(View v)
            {
                if(checkbox.isSelected()){
                	selectedvalues.remove(values.get(position));
                	checkbox.setSelected(false);
                }else{
                	selectedvalues.add(values.get(position));
                	checkbox.setSelected(true);
                }
                //checkbox.setSelected(!checkbox.isSelected());
                //CategoryChooseArrayAdapter.this.notifyDataSetChanged();
            }
        });

		return rowView;
	}
	/**
	 * Gibt die selektierten Kategorien zurueck
	 * @return
	 */
	public ArrayList<Category> getSelectedCategories(){
		return selectedvalues;
	}
}
