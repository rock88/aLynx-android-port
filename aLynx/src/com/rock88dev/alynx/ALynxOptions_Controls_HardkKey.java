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
import java.util.HashMap;
import java.util.Map;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
//import android.view.InputDevice;

public class ALynxOptions_Controls_HardkKey extends ListActivity   {
	private ALynxSetting set;
	private int position = 0;
	private ArrayList<Map<String, String>> list;
	private String[] from = { "name", "value" };
	private int[] to = { android.R.id.text1, android.R.id.text2 };
	private int simple_list_item_2 = android.R.layout.simple_list_item_2;
	private KeyCharacterMap kcm = KeyCharacterMap.load(0);
	
    private static final String GAMETEL_NAME =    "gametel";
    private static final String GAMETEL_PACKAGE = "com.fructel.gametel";
    private BroadcastReceiver mReceiver;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//takeKeyEvents(true);
		set = new ALynxSetting(this);
		set.loadSettings();
		
		list = buildData();
		SimpleAdapter adapter = new SimpleAdapter(this, list, simple_list_item_2, from, to);
		setListAdapter(adapter);
		
		if (ALynxSetting.bt_gamepad == 1)
		{
	        mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					updateGametelStatus();
				}
	        };
		}
		
		takeKeyEvents(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Set for Xperia Play");
		menu.add(1, 0, 0, "Clear");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getGroupId())
		{
			case 0:
				set.hard_key[0] = KeyEvent.KEYCODE_DPAD_UP;
				set.hard_key[1] = KeyEvent.KEYCODE_DPAD_DOWN;
				set.hard_key[2] = KeyEvent.KEYCODE_DPAD_LEFT;
				set.hard_key[3] = KeyEvent.KEYCODE_DPAD_RIGHT;
				set.hard_key[4] = KeyEvent.KEYCODE_DPAD_CENTER;
				set.hard_key[5] = KeyEvent.KEYCODE_BACK;
				set.hard_key[6] = 108;
				set.hard_key[7] = 99;
				set.hard_key[8] = 100;
				set.saveSettings();
				listUpdate();
				break;
			case 1:
				for(int i=0; i<9; ++i) set.hard_key[i] = 0;
				set.saveSettings();
				listUpdate();
				break;
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		if (ALynxSetting.bt_gamepad == 1) mReceiver = null;
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		if (ALynxSetting.bt_gamepad == 1) 
		{
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED); 
	        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED); 
	        registerReceiver(mReceiver, filter);
	        updateGametelStatus();
		}
		super.onResume();
	}
	
    @Override
    protected void onPause() {
    	if (ALynxSetting.bt_gamepad == 1) unregisterReceiver(mReceiver);
        super.onPause();
    }
    
    void listUpdate()
    {
		list = buildData();
		SimpleAdapter adapter2 = new SimpleAdapter(ALynxOptions_Controls_HardkKey.this, list, simple_list_item_2, from, to);
		setListAdapter(adapter2);    	
    }
    
	private ArrayList<Map<String, String>> buildData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(putData("Up", getKeyNameByKey(set.hard_key[0])));
		list.add(putData("Down", getKeyNameByKey(set.hard_key[1])));
		list.add(putData("Left", getKeyNameByKey(set.hard_key[2])));
		list.add(putData("Right", getKeyNameByKey(set.hard_key[3])));
		list.add(putData("Button A", getKeyNameByKey(set.hard_key[4])));
		list.add(putData("Button B", getKeyNameByKey(set.hard_key[5])));
		list.add(putData("Start", getKeyNameByKey(set.hard_key[6])));
		list.add(putData("Option 1", getKeyNameByKey(set.hard_key[7])));
		list.add(putData("Option 2", getKeyNameByKey(set.hard_key[8])));
		return list;
	}

	private HashMap<String, String> putData(String name, String value) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("value", value);
		return item;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		showKeyDialog(position);
	}
	
	private void showKeyDialog(int i){
		position=i;
		HashMap<String, String> item = (HashMap<String, String>)this.getListAdapter().getItem(i);
		String str = "Current key - "+item.get("value");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Press a key for "+item.get("name"));
		builder.setMessage(str);
		builder.setCancelable(false);
		
		final EditText view = new EditText(this);
		view.setVisibility(View.INVISIBLE);
		view.setText("Text");
		builder.setView(view);
		
		builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   listUpdate();
		           }
		       });
		builder.setNegativeButton("Clear", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {}
	       });
		
		final AlertDialog alert = builder.create();
		alert.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface arg0, int key, KeyEvent event) {
				if (key==KeyEvent.KEYCODE_MENU) return true;
				alert.setMessage("Current key - "+getKeyNameByKey(key));
				set.hard_key[position] = key;
				set.saveSettings();
				//Log.d("ALYNX","Key = "+key);
				return true;
			}
		});
		alert.show();
		alert.takeKeyEvents(true);
		
		Button btnOK = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnOK.setOnClickListener(new OnClickListener(){
			public void onClick(View vies) {
				set.hard_key[position] = 0;
				set.saveSettings();
				alert.setMessage("Current key - "+getKeyNameByKey(0));
			}
        });
	}
	
	private String getKeyNameByKey(int key){
		String str = "Unknown";
		
		switch(key){
			case 0:
				str="None";
				break;
			case KeyEvent.KEYCODE_BACK:
				str="Back";
				break;
			case KeyEvent.KEYCODE_MENU:
				str="Menu";
				break;
			case KeyEvent.KEYCODE_CALL:
				str="Call";
				break;
			case KeyEvent.KEYCODE_SEARCH:
				str="Search";
				break;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				str="Volume Down";
				break;
			case KeyEvent.KEYCODE_VOLUME_UP:
				str="Volume Up";
				break;
			case KeyEvent.KEYCODE_CAMERA:
				str="Camera";
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				str="Up";
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				str="Down";
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				str="Left";
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				str="Right";
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:
				str="Dpad Center/Cross";
				break;
			case 99: // XPERIA PLAY SQUARE KEYCODE_BUTTON_X
				str="Square";
				break;
			case 100: // XPERIA PLAY TRIANGLE KEYCODE_BUTTON_Y
				str="Triangle";
				break;
			case 109: // XPERIA PLAY SELECT KEYCODE_BUTTON_SELECT
				str="Select";
				break;
			case 108: // XPERIA PLAY START KEYCODE_BUTTON_START
				str="Start";
				break;
			case 102: // XPERIA PLAY L1 KEYCODE_BUTTON_L1
				str="L1";
				break;
			case 103: // XPERIA PLAY L1 KEYCODE_BUTTON_R1
				str="R1";
				break;
			case KeyEvent.KEYCODE_SPACE:
				str="Space";
				break;
			case KeyEvent.KEYCODE_ENTER:
				str="Enter";
				break;
			case KeyEvent.KEYCODE_ALT_LEFT:
				str="Left Alt";
				break;
			case KeyEvent.KEYCODE_ALT_RIGHT:
				str="Right Alt";
				break;
			case KeyEvent.KEYCODE_SHIFT_LEFT:
				str="Left Shift";
				break;		
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				str="Right Shift";
				break;
			default:
				str=""+kcm.getDisplayLabel(key);
		}
		
		return str;
	}
	
    private void updateGametelStatus()
    {
        boolean gametelAvailable = false;
        /* Check if there are any Gametels connected as HID gamepad */
        if (isHIDGametelConnected()) gametelAvailable = true;
        /* Check if the Gametel InputMethod is active */
        if (isGametelIMEActive()) gametelAvailable = true;
    }
    
    /* Function that checks if the Gametel InputMethod is currently active */
    private boolean isGametelIMEActive() {
        String activeIme = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        return activeIme.startsWith(GAMETEL_PACKAGE);
    }

    /* Function that checks if there are any Gametels in HID gamepad mode currently connected */
    private boolean isHIDGametelConnected() {
        return true;
    }
	
}
