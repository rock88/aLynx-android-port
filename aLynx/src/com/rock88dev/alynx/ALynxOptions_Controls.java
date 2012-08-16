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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ALynxOptions_Controls extends Activity implements OnClickListener {
	private ALynxSetting set;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_controls);
		
		set = new ALynxSetting(this);
		
		Button button1 = (Button)findViewById(R.id.opt_contrl_button_conf_p);
		button1.setOnClickListener(this);
		
		Button button2 = (Button)findViewById(R.id.opt_contrl_button_conf_l);
		button2.setOnClickListener(this);
		
		Button button3 = (Button)findViewById(R.id.opt_contrl_button_hardkey);
		button3.setOnClickListener(this);
		
		Button button4 = (Button)findViewById(R.id.opt_contrl_button_hide);
		button4.setOnClickListener(this);
		
		CheckBox checbox2 = (CheckBox)findViewById(R.id.opt_controls_vibrate_checkBox);
		checbox2.setChecked(set.vibrate==1?true:false);
		checbox2.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton but, boolean b) {
				set.vibrate = b?1:0;
				set.saveSettings();
			}
		});
	}
	
	@Override
	public void onBackPressed(){
		//Log.d("ALYNX","_Controls Need Save!!");
		super.onBackPressed();
	}

	public void onClick(View v) {
	    switch (v.getId())
	    {
	    	case R.id.opt_contrl_button_conf_p:
	    	{
				Intent intent = new Intent(this, ALynxConfPad.class);
				intent.putExtra(ALynxConfPad.SCREEN_MODE, false);
				startActivity(intent);
	    		break;
	    	}
	    	case R.id.opt_contrl_button_conf_l:
	    	{
				Intent intent = new Intent(this, ALynxConfPad.class);
				intent.putExtra(ALynxConfPad.SCREEN_MODE, true);
				startActivity(intent);
	    		break;
	    	}
	    	case R.id.opt_contrl_button_hardkey:
	    	{
				Intent intent = new Intent(this, ALynxOptions_Controls_HardkKey.class);
				startActivity(intent);
	    		break;
	    	}
	    	case R.id.opt_contrl_button_hide:
	    	{
				Intent intent = new Intent(this, ALynxOptions_Controls_HideButtons.class);
				startActivity(intent);
	    		break;
	    	}
	    }
	}
	
}
