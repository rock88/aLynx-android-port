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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ALynxOptions_Sound extends Activity {
	private ALynxSetting set;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_sound);
		
		set = new ALynxSetting(this);
		
		CheckBox checbox = (CheckBox)findViewById(R.id.opt_sound_checkbox);
		checbox.setChecked(set.sound_opt==1?true:false);
		checbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton but, boolean b) {
				set.sound_opt = b?1:0;
				set.saveSettings();
			}
		});
		
		Spinner spinner = (Spinner)findViewById(R.id.opt_sample_size_spinner);
		
		int i = 0;
		if (set.sample_size==ALynxSetting.SAMPLE_1024) i = 0;
		if (set.sample_size==ALynxSetting.SAMPLE_2048) i = 1;
		if (set.sample_size==ALynxSetting.SAMPLE_4096) i = 2;
		//if (set.sample_size==ALynxSetting.SAMPLE_8192) i = 3;
		
		spinner.setSelection(i);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				switch(pos)
				{
					case 0:
						set.sample_size = ALynxSetting.SAMPLE_1024;
						break;
					case 1:
						set.sample_size = ALynxSetting.SAMPLE_2048;
						break;
					case 2:
						set.sample_size = ALynxSetting.SAMPLE_4096;
						break;
					/*case 3:
						set.sample_size = ALynxSetting.SAMPLE_8192;
						break;*/
				}
				set.saveSettings();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
	}
	
}

