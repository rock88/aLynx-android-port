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
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ALynxEmuActivity_L extends Activity {
	public static String LYNX_ROM_PATH = "LYNX_ROM_PATH";
	private ALynxSetting set;
	private ALynxEmuSurface_Canvas a_canvas;
	private ALynxEmuSurface_GL a_gles;
	private ALynxEmuSurface_GL_Native a_gles_native;
	private BroadcastReceiver mReceiver;
	private static final String GAMETEL_PACKAGE = "com.fructel.gametel";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//
    	set = new ALynxSetting(this);
    	//set.loadSettings();
    	String rom = getIntent().getStringExtra(LYNX_ROM_PATH);
    	
    	if(set.vibrate==1)
    	{
    		ALynxInput.vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    		ALynxInput.vibrator_enable = 1;
    	}
    	else ALynxInput.vibrator_enable = 0;
    	
    	
    	if(set.render == ALynxSetting.CANVAS)
    	{	
	    	a_canvas = new ALynxEmuSurface_Canvas(this, set, rom);
			setContentView(a_canvas);
			a_canvas.requestFocus();
			a_canvas.setFocusableInTouchMode(true);
    	}
    	
    	if(set.render == ALynxSetting.GLES)
    	{	
    		a_gles = new ALynxEmuSurface_GL(this, set, rom);
			setContentView(a_gles);
			a_gles.requestFocus();
			a_gles.setFocusableInTouchMode(true);
    	}
    	
    	if(set.render == ALynxSetting.GLES_NATIVE)
    	{	
    		a_gles_native = new ALynxEmuSurface_GL_Native(this, set, rom);
			setContentView(a_gles_native);
			a_gles_native.requestFocus();
			a_gles_native.setFocusableInTouchMode(true);
    	}
    	
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
	protected void onDestroy() {
    	if (ALynxSetting.bt_gamepad == 1) mReceiver = null;
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.emu_menu, menu);
        MenuItem m1 = menu.getItem(0);
        m1.setIcon(R.drawable.savestates);
        //MenuItem m2 = menu.getItem(1);
        //m2.setIcon(R.drawable.screenshot);
        MenuItem m3 = menu.getItem(1);
        m3.setIcon(R.drawable.close);
        return true;
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	//Log.d("ALYNX","hasFocus = "+hasFocus);
    	if(!hasFocus) ALynxEmuProxy.AL_Emu_Pause();
    		else ALynxEmuProxy.AL_Emu_Resume();
    	super.onWindowFocusChanged(hasFocus);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.emu_menu_state:
                //Log.d("ALYNX","save_slot_1");
				Intent intent = new Intent(this, ALynxEmuStates.class);
			    startActivity(intent);
                return true;
            case R.id.emu_menu_exit:
            	if(set.render == ALynxSetting.CANVAS) a_canvas.Stop();
            	if(set.render == ALynxSetting.GLES) a_gles.Stop();
            	if(set.render == ALynxSetting.GLES_NATIVE) a_gles_native.Stop();
            	finish();
                return true;//super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	ALynxEmuProxy.AL_Emu_Resume();
    	super.onOptionsMenuClosed(menu);
    }
    */
    
    @Override
    protected void onPause() {
    	if (ALynxSetting.bt_gamepad == 1) unregisterReceiver(mReceiver);
        super.onPause();
    }
    
    @Override
    protected void onResume() {
		if (ALynxSetting.bt_gamepad == 1) 
		{
	        /* Register a listener to detect when Gametel devices connects/disconnects */
	        IntentFilter filter = new IntentFilter();
	        /* For devices in RFCOMM mode (which uses the InputMethod) */
	        filter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED); 
	        /* For devices in HID mode */
	        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED); 
	        registerReceiver(mReceiver, filter);
	        /* Check if there are any Gametel devices connected */
	        updateGametelStatus();
		}
		super.onResume();
    }
    
    private void updateGametelStatus()
    {
        boolean gametelAvailable = false;
        if (isHIDGametelConnected()) gametelAvailable = true;
        if (isGametelIMEActive()) gametelAvailable = true;
    }
    
    private boolean isGametelIMEActive() {
        String activeIme = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        return activeIme.startsWith(GAMETEL_PACKAGE);
    }    

    private boolean isHIDGametelConnected() {
        return true;
    }
}
