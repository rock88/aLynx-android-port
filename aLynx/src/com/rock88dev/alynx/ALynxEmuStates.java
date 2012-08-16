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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ALynxEmuStates extends ListActivity {
	
	private ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private String[] from = { "name", "value" };
	private int[] to = { R.id.sli2_text1, R.id.sli2_text2 };
	private int simple_list_item_2 = R.layout.simple_list_item_2_with_button;
	private Date date = new Date();
	private ALynxSetting set;
	private String path = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		set = new ALynxSetting(this);
		updateList();
	}
	
	private void updateList()
	{
		list.clear();
		
		String romPath = ALynxActivity.romPath;
		String text = null, path = null;
		
		for(int i=0; i<10; i++)
		{
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("name", "Slot "+(i+1));
			//if(i==0)
			{
				text = "free";
				path = getFilePathNoEXT(romPath)+".sav"+(i+1);
				
				if((set.state_path!=null)&&(set.state_path.length()>3)) path = set.state_path+"/"+getFileNameNoEXT(romPath)+".sav"+(i+1);
				
				File f = new File(path);
				if (f.exists())
				{
					date.setTime(f.lastModified());
					text = "Time: "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+" Date: "+date.getDay()+"/"+date.getMonth()+"/"+(1900+date.getYear());
				}
			}
			/*else
			{
				text = "Not available...";
			}*/
			
			item.put("value", text);
			list.add(item);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, list, simple_list_item_2, from, to);
		setListAdapter(adapter);
	}
	
	public void saveHandler(View view) 
    {
		final int position = getListView().getPositionForView(view);
		//if(position!=0) return;
		path = getFilePathNoEXT(ALynxActivity.romPath)+".sav"+(position+1);
		if((set.state_path!=null)&&(set.state_path.length()>3)) path = set.state_path+"/"+getFileNameNoEXT(ALynxActivity.romPath)+".sav"+(position+1);
		
		File f = new File(path);
		if (f.exists())
		{
			//Log.d("ALYNX","File "+path+" exists!");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Save State...");
			builder.setMessage("State from Slot "+(position+1)+" already exists.\nDo you want to replace it?");
			builder.setCancelable(false);
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (ALynxEmuProxy.AL_Save_State(path)==1)
						{
							Toast.makeText(ALynxEmuStates.this, "State "+(position+1)+" save to:\n"+path, 1).show();
							updateList();
						}			       			
			           }
			       });
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
		       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		else
		{
		
			if (ALynxEmuProxy.AL_Save_State(path)==1)
			{
				Toast.makeText(this, "State "+(position+1)+" save to:\n"+path, 1).show();
				updateList();
			}
		}

		//Log.d("ALYNX","path = "+path);
    }
	
	public void loadHandler(View view) 
    {
		int position = getListView().getPositionForView(view);
		//if(position!=0) return;
		String path = getFilePathNoEXT(ALynxActivity.romPath)+".sav"+(position+1);
		if((set.state_path!=null)&&(set.state_path.length()>3)) path = set.state_path+"/"+getFileNameNoEXT(ALynxActivity.romPath)+".sav"+(position+1);
		
		File f = new File(path);
		if (f.exists())
		{
			ALynxEmuProxy.AL_Load_State(path);
			finish();
		}
    }
	
	public void deleteHandler(View view) 
    {
		int position = getListView().getPositionForView(view);
		//if(position!=0) return;
		path = getFilePathNoEXT(ALynxActivity.romPath)+".sav"+(position+1);
		if((set.state_path!=null)&&(set.state_path.length()>3)) path = set.state_path+"/"+getFileNameNoEXT(ALynxActivity.romPath)+".sav"+(position+1);
		
		final File f = new File(path);
		if (f.exists())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Delete State...");
			builder.setMessage("Do you want to delete state from Slot "+(position+1)+" ?");
			builder.setCancelable(false);
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (ALynxEmuProxy.AL_Save_State(path)==1)
						{
							f.delete();
							updateList();
						}			       			
			           }
			       });
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
		       });
			AlertDialog alert = builder.create();
			alert.show();			
		}
    }
	
	private String getFileName(String path)
	{
		String n = path.substring(path.lastIndexOf("/")+1);
		//Log.d("ALYNX","path = "+n);
		return n;
	}
	
	private String getFileNameNoEXT(String path)
	{
		String n = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf(".lnx"));
		//Log.d("ALYNX","path = "+n);
		return n;
	}
	
	private String getFilePathNoEXT(String path)
	{
		String n = path.substring(0, path.lastIndexOf(".lnx"));
		//Log.d("ALYNX","path = "+n);
		return n;
	}

}
