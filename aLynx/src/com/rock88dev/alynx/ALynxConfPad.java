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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ALynxConfPad extends Activity {
	public static final String SCREEN_MODE = "SCREEN_MODE";
	private ALynxConfPad_Canvas a;
	private boolean mode = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	mode = getIntent().getBooleanExtra(SCREEN_MODE, false);
    	
		//Log.d("ALYNX","mode = "+mode);

    	ALynxSetting set = new ALynxSetting(this);
    	
    	a = new ALynxConfPad_Canvas(this, set, mode);
		setContentView(a);
		a.Run();
		
		if (mode) setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    @Override
	protected void onDestroy() {
    	//Log.d("ALYNX","Destroy()");
    	a.Stop();
    	super.onDestroy();
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.pad_conf_menu, menu);
        if(!mode) menu.findItem(R.id.padmenu_stretch_image).setVisible(false);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.padmenu_screen:
        		a.setButton(0);
        		break;
        	case R.id.padmenu_dpad:
        		a.setButton(1);
        		break;
        	case R.id.padmenu_button_a:
        		a.setButton(2);
        		break;
        	case R.id.padmenu_button_b:
        		a.setButton(3);
        		break;
        	case R.id.padmenu_button_start:
        		a.setButton(4);
        		break;
        	case R.id.padmenu_button_opt1:
        		a.setButton(5);
        		break;
        	case R.id.padmenu_button_opt2:
        		a.setButton(6);
        		break;
        	case R.id.opacity_None:
        		a.setOpacity(ALynxSetting.OPASITY_NONE);
        		break;
        	case R.id.opacity_Low:
        		a.setOpacity(ALynxSetting.OPASITY_LOW);
        		break;
        	case R.id.opacity_Medium:
        		a.setOpacity(ALynxSetting.OPASITY_MEDIUM);
        		break;
        	case R.id.opacity_Hight:
        		a.setOpacity(ALynxSetting.OPASITY_HIGHT);
        		break;
        	case R.id.padmenu_reset:
        		ALynxSetting.ResetPad(a.size, a.pad_no);
        		a.loadPadPosition();
        		a.setOpacity(ALynxSetting.OPASITY_NONE);
        		break;
        	case R.id.padmenu_saveexit:
        		finish();
        		break;
        	case R.id.size_1x:
        		a.setSize(1);
        		break;
        	case R.id.size_15x:
    			if(a.s_width < 320 || a.s_height < 320)
    			{
    				Toast.makeText(this, "Screen is very small for 1.5x size!", 2).show();
    			} else a.setSize(3);
        		break;
        	case R.id.size_2x:
    			if(a.s_width < 480 || a.s_height < 480)
    			{
    				Toast.makeText(this, "Screen is very small for 2x size!", 2).show();
    			} else a.setSize(2);
        		break;
        	case R.id.padmenu_stretch_full:
        		a.resizeImage(0);
        		break;
        	case R.id.padmenu_stretch_borders:
        		a.resizeImage(1);
        		break;        		
            default:
                return super.onOptionsItemSelected(item);
        }
		return false;
    }
}

