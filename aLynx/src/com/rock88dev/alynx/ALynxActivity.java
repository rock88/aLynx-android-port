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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ALynxActivity extends Activity implements OnClickListener {
	
	private static final int REQUEST_LYNXRUN = 0;
	private Button b_open, b_options, b_about, b_exit;
	private ALynxSetting set;
	public static String romPath = null;
	private AlertDialog alert;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        set = new ALynxSetting(this);
        set.loadSettings();
        
        b_open = (Button)findViewById(R.id.b_open1);
        b_open.setOnClickListener(this);
        
        b_options = (Button)findViewById(R.id.b_setting);
        b_options.setOnClickListener(this);
        
        Button b_help = (Button)findViewById(R.id.main_button_help);
        b_help.setOnClickListener(this);
        
        b_about = (Button)findViewById(R.id.b_about);
        b_about.setOnClickListener(this); 
        
        b_exit = (Button)findViewById(R.id.b_exit);
        b_exit.setOnClickListener(this);
        //set.first_run2 = 1;
        if(set.first_run2 == 1)
        {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Hello,");
			builder.setMessage("This is your first time running aLynx.\n" +
					"Recommend reading help file for, something to "+
					"learn about the basic settings, as well as to " +
					"configure the interface aLynx to your liking.");
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {			       			
		           }
		       });
			alert = builder.create();
			alert.show();
        	set.first_run2 = 0;
        	set.saveSettings();
        }
        
	    String s = getIntent().getStringExtra(ALynxRomSelectDialog.RESULT_PATH);
	    if(s!=null)Log.d("ALYNX","string = "+s);
    }
    
    @Override
    protected void onDestroy() {
    	if(alert != null) alert.cancel();
    	super.onDestroy();
    }
	public void onClick(View v) {
	    switch (v.getId())
	    {
	    	case R.id.b_open1:
	    	{
	    		set.loadSettings();
	    		if((set.bios_path == null) || ((ALynxEmuProxy.AL_Check_BIOS(set.bios_path)==0)))
	    		{
	    			Toast.makeText(this, "Not a Lynx BIOS file (lynxboot.img).\nSet it on a Options->Path.", 5).show();
	    			break;
	    		}
	    		
	    		if(ALynxEmuProxy.AL_Check_BIOS(set.bios_path)==2)
	    		{
	    			Toast.makeText(this, "Application version is old\nplease update it from a Google Play.", 5).show();
	    			break;
	    		}
	    		
				Intent intent = new Intent(this, ALynxRomSelectDialog.class);
			    startActivityForResult(intent, REQUEST_LYNXRUN);
	    		break;
	    	}
	    	case R.id.b_setting:
	    	{
				Intent intent = new Intent(this, ALynxOptions.class);
			    startActivity(intent);
	    		break;
	    	}
	    	case R.id.main_button_help:
	    	{
	    		Intent intent = new Intent(this, ALynxHelp.class);
			    startActivity(intent);
	    		break;	    		
	    	}
	    	case R.id.b_about:
	    	{
				Intent intent = new Intent(this, ALynxAbout.class);
			    startActivity(intent);
	    		break;
	    	}
	    	case R.id.b_exit:
	    		System.exit(-1);
	    		finish();
	    		break;
	    	default:
	    		break;
	    }
	}

    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_LYNXRUN) {
                	romPath = data.getStringExtra(ALynxRomSelectDialog.RESULT_PATH);
                    Log.d("ALYNX","run with = "+romPath);
                    set.loadSettings();
                    if(set.orientation_opt==0){
                    	Intent intent = new Intent(this, ALynxEmuActivity_P.class);
                    	intent.putExtra(ALynxEmuActivity_P.LYNX_ROM_PATH, romPath);
                    	startActivity(intent);
                    } else {
                    	Intent intent = new Intent(this, ALynxEmuActivity_L.class);
                    	intent.putExtra(ALynxEmuActivity_L.LYNX_ROM_PATH, romPath);
                    	startActivity(intent);
                    }
    			    
                }
        }
    }
    
    @Override
    public void onBackPressed() {
    	System.exit(-1);
    	super.onBackPressed();
    }

}