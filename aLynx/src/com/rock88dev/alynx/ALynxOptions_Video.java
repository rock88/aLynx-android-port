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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class ALynxOptions_Video extends Activity implements OnClickListener {

	private ALynxSetting set;
	private TextView seek_bar_text;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options_base);
		
		set = new ALynxSetting(this);
		
		Spinner spinner1 = (Spinner)findViewById(R.id.opt_base_orient_spinner);
		spinner1.setSelection(set.orientation_opt);
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				set.orientation_opt = pos;
				set.saveSettings();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	
		final Spinner spinner2 = (Spinner)findViewById(R.id.opt_base_filter_spinner);
		if (set.filter_opt > 2) {
			set.filter_opt = 0;
			set.saveSettings();
		}
		spinner2.setSelection(set.filter_opt);
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				set.filter_opt = pos;
				set.saveSettings();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		Spinner spinner3 = (Spinner)findViewById(R.id.opt_base_fskip_spinner);
		spinner3.setSelection(set.fskip_opt);
		spinner3.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				set.fskip_opt = pos;
				set.saveSettings();
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		CheckBox checbox2 = (CheckBox)findViewById(R.id.opt_base_limit_checkbox);
		checbox2.setChecked(set.fps_limit==1?true:false);
		checbox2.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton but, boolean b) {
				set.fps_limit = b?1:0;
				set.saveSettings();
			}
		});
		
		final CheckBox checbox3 = (CheckBox)findViewById(R.id.opt_base_pwrvr_checkbox);
		checbox3.setChecked(set.powervr_fix==1?true:false);
		checbox3.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton but, boolean b) {
				set.powervr_fix = b?1:0;
				set.saveSettings();
			}
		});
		
		seek_bar_text = (TextView)findViewById(R.id.opt_base_text_seekbar);
		final SeekBar bar = (SeekBar)findViewById(R.id.opt_base_seekbar);
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				if (progress==0) bar.setProgress(1);
				seek_bar_text.setText(""+bar.getProgress());
				set.canvas_refresh=progress;
				set.saveSettings();
			}
		});
		bar.setProgress(set.canvas_refresh);
		
		Spinner spinner = (Spinner)findViewById(R.id.opt_base_render_spinner);
		spinner.setSelection(set.render);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				if(pos==0)
				{
					TextView t1 = (TextView)findViewById(R.id.textView2);
					t1.setVisibility(View.VISIBLE);
					TextView t2 = (TextView)findViewById(R.id.textView5);
					t2.setVisibility(View.VISIBLE);
					TextView t3 = (TextView)findViewById(R.id.textView6);
					t3.setVisibility(View.VISIBLE);
					TextView t4 = (TextView)findViewById(R.id.textView7);
					t4.setVisibility(View.VISIBLE);
					TextView t5 = (TextView)findViewById(R.id.opt_base_text_seekbar);
					t5.setVisibility(View.VISIBLE);				
					spinner2.setVisibility(View.VISIBLE);
					bar.setVisibility(View.VISIBLE);					
					set.render = ALynxSetting.CANVAS;
					set.saveSettings();
				}
				else
				{
					TextView t1 = (TextView)findViewById(R.id.textView2);
					t1.setVisibility(View.GONE);
					TextView t2 = (TextView)findViewById(R.id.textView5);
					t2.setVisibility(View.GONE);
					TextView t3 = (TextView)findViewById(R.id.textView6);
					t3.setVisibility(View.GONE);
					TextView t4 = (TextView)findViewById(R.id.textView7);
					t4.setVisibility(View.GONE);
					TextView t5 = (TextView)findViewById(R.id.opt_base_text_seekbar);
					t5.setVisibility(View.GONE);				
					spinner2.setVisibility(View.GONE);
					bar.setVisibility(View.GONE);
					set.render = pos;//ALynxSetting.GLES;
					set.saveSettings();
				}
				if(pos==0) checbox3.setVisibility(View.GONE);
				if(pos==1) checbox3.setVisibility(View.VISIBLE);
				if(pos==2) checbox3.setVisibility(View.VISIBLE);
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	@Override
	public void onBackPressed(){
		//Log.d("ALYNX","_Config Need Save!!");
		super.onBackPressed();
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
