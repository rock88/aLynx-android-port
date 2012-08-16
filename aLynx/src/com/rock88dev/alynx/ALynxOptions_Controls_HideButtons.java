/** aLynx - Atari Lynx emulator for Android OS
 * 
 * Copyright (C) 2012
 * @author: rock88
 * 
 * e-mail: rock88a@gmail.com
 * 
 * http://rock88dev.blogspot.com
 * 
 */

package com.rock88dev.alynx;

import java.util.ArrayList;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class ALynxOptions_Controls_HideButtons extends ListActivity {
	private ALynxSetting set;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		set = new ALynxSetting(this);
		
		setListAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_checked, new ArrayList<Object>()));
		
		fillAdapter();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		int s = this.getListView().getCount();
		//Log.d("ALYNX","getChildCount = "+s);
		
		for(int i=0; i < s; i++)
		{
			getListView().setItemChecked(i, ALynxSetting.button_show[i]==1?true:false);
		}

	}
	
	void fillAdapter()
	{
		((ArrayAdapter<String>) getListAdapter()).clear();
		((ArrayAdapter<String>) getListAdapter()).add("D-Pad");
		((ArrayAdapter<String>) getListAdapter()).add("Button A");
		((ArrayAdapter<String>) getListAdapter()).add("Button B");
		((ArrayAdapter<String>) getListAdapter()).add("Button Start");
		((ArrayAdapter<String>) getListAdapter()).add("Button Opt1");
		((ArrayAdapter<String>) getListAdapter()).add("Button Opt2");
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		CheckedTextView check = (CheckedTextView)v;
		check.setChecked(!check.isChecked());
		if(check.isChecked())
		{
			ALynxSetting.button_show[position] = 1;
		}
		else
		{
			ALynxSetting.button_show[position] = 0;
		}
		set.saveSettings();
	}
	
	
}
