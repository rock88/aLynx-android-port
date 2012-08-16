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
import android.widget.Button;
import android.widget.TextView;

public class ALynxAbout extends Activity implements OnClickListener {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView t = (TextView)findViewById(R.id.textView2);
		t.setText("aLynx - is a Atari Lynx emulator for Android OS 2.1 and higher. aLynx supports on-screen keyboard, sound output, OpenGL rendering, load/save games states.\n\n");
		t.append("aLynx based on a Handy 0.95 by K. Wilkins\n\n");
		t.append("Version: 1.032 31/05/12\n\n");
		t.append("Autor: rock88\n");
		t.append("e-mail: rock88a@gmail.com\n");
		t.append("Web: http://rock88dev.blogspot.com\n");
		Button about_back = (Button)findViewById(R.id._about_back);
		about_back.setOnClickListener(this);
		/*
		Button button = (Button)findViewById(R.id._about_emumobi);
		button.setOnClickListener(this);*/
	}

	public void onClick(View v) {
	    switch (v.getId())
	    {
	    	case R.id._about_back:
	    		finish();
	    		break;
	    	/*case R.id._about_emumobi:
	    		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://emu-mobi.com/"));
	    		startActivity(i);
	    		break;*/
	    }
	}
}
