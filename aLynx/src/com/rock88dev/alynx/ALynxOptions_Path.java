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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ALynxOptions_Path extends Activity implements OnClickListener {
	
	private static final int REQUEST_ROMPATHSET = 0;
	private static final int REQUEST_STATEPATHSET = 1;
	private static final int REQUEST_PICPATHSET = 2;
	private static final int REQUEST_BIOSSET = 3;
	
	private ALynxSetting set;
	private EditText edit0, edit1, edit2, edit3;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_other);
		
		set = new ALynxSetting(this);
		//set.loadSettings();
		
		edit0 = (EditText)findViewById(R.id.opt_other_bios_edit);
		edit0.setText(set.bios_path);
		edit0.setFocusable(false);
		
		edit1 = (EditText)findViewById(R.id.editText1);
		edit1.setText(set.rom_path);
		edit1.setFocusable(false);
		
		edit2 = (EditText)findViewById(R.id.editText2);
		edit2.setText(set.state_path);
		edit2.setFocusable(false);
		/*
		edit3 = (EditText)findViewById(R.id.editText3);
		edit3.setText(set.pic_path);
		edit3.setFocusable(false);
		*/
		Button bios_set = (Button)findViewById(R.id.opt_other_button_bios_set);
		bios_set.setOnClickListener(this);
		
		Button bios_clear = (Button)findViewById(R.id.opt_other_button_bios_clear);
		bios_clear.setOnClickListener(this);
		
		Button rom_set = (Button)findViewById(R.id.opt_other_button_rom_set);
		rom_set.setOnClickListener(this);
		
		Button rom_clear = (Button)findViewById(R.id.opt_other_button_rom_clear);
		rom_clear.setOnClickListener(this);
		
		Button state_set = (Button)findViewById(R.id.opt_other_button_state_set);
		state_set.setOnClickListener(this);
		
		Button state_clear = (Button)findViewById(R.id.opt_other_button_state_clear);
		state_clear.setOnClickListener(this);
		/*
		Button pic_set = (Button)findViewById(R.id.opt_other_button_pic_set);
		pic_set.setOnClickListener(this);
		
		Button pic_clear = (Button)findViewById(R.id.opt_other_button_pic_clear);
		pic_clear.setOnClickListener(this);
		*/	
	}
	
	public void onClick(View v) {
	    switch (v.getId())
	    {
	    	case R.id.opt_other_button_bios_set:
	    	{
				Intent intent = new Intent(this, ALynxBiosSelectDialog.class);
			    startActivityForResult(intent, REQUEST_BIOSSET);
			    break;
	    	}
	    	case R.id.opt_other_button_bios_clear:
	    	{
	    		set.bios_path = "";
	        	set.saveSettings();
	        	edit0.setText(set.bios_path);
	    		break;
		    }
	    	case R.id.opt_other_button_rom_set:
	    	{
				Intent intent = new Intent(this, ALynxPathSelectDialog.class);
			    startActivityForResult(intent, REQUEST_ROMPATHSET);
			    break;
	    	}
	    	case R.id.opt_other_button_rom_clear:
	    		set.rom_path = "";
            	set.saveSettings();
            	edit1.setText(set.rom_path);	    		
	    		break;
	    	case R.id.opt_other_button_state_set:
	    	{
				Intent intent = new Intent(this, ALynxPathSelectDialog.class);
			    startActivityForResult(intent, REQUEST_STATEPATHSET);
			    break;
	    	}
	    	case R.id.opt_other_button_state_clear:
	    		set.state_path = "";
            	set.saveSettings();
            	edit2.setText(set.state_path);	    		
	    		break;
	    	/*
	    	case R.id.opt_other_button_pic_set:
	    	{
				Intent intent = new Intent(this, ALynxPathSelectDialog.class);
			    startActivityForResult(intent, REQUEST_PICPATHSET);
			    break;
	    	}	    	
	    	case R.id.opt_other_button_pic_clear:
	    		set.pic_path = "";
            	set.saveSettings();
            	edit3.setText(set.pic_path);
	    		break;
	    	*/
	    }
	}
	
    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
        	switch(requestCode)
        	{
	    		case REQUEST_BIOSSET:
	            	set.bios_path = data.getStringExtra(ALynxBiosSelectDialog.RESULT_PATH);
	            	set.saveSettings();
	            	edit0.setText(set.bios_path);
	    			break;
        		case REQUEST_ROMPATHSET:
                	set.rom_path = data.getStringExtra(ALynxPathSelectDialog.RESULT_PATH);
                	set.saveSettings();
                	edit1.setText(set.rom_path);
                	break;
        		case REQUEST_STATEPATHSET:
                	set.state_path = data.getStringExtra(ALynxPathSelectDialog.RESULT_PATH);
                	set.saveSettings();
                	edit2.setText(set.state_path);
                	break;
        		case REQUEST_PICPATHSET:
                	set.pic_path = data.getStringExtra(ALynxPathSelectDialog.RESULT_PATH);
                	set.saveSettings();
                	edit3.setText(set.pic_path);
                	break;
        	}
        }
    }
    
	@Override
	public void onBackPressed(){
		Log.d("ALYNX","_Other Need Save!!");
		super.onBackPressed();
	}
	
}
