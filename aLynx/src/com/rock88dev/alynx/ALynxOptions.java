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

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ALynxOptions extends TabActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        TabHost tabHost = getTabHost();
        
		TabSpec video = tabHost.newTabSpec("tab1");
		video.setIndicator("Video", getResources().getDrawable(R.drawable.video));
		Intent configIntent = new Intent(this, ALynxOptions_Video.class);
		video.setContent(configIntent);
		
		TabSpec sound = tabHost.newTabSpec("tab2");
		sound.setIndicator("Sound", getResources().getDrawable(R.drawable.sound));
		Intent soundIntent = new Intent(this, ALynxOptions_Sound.class);
		sound.setContent(soundIntent);
		
		TabSpec controls = tabHost.newTabSpec("tab3");
		controls.setIndicator("Controls", getResources().getDrawable(R.drawable.control));
		Intent controlsIntent = new Intent(this, ALynxOptions_Controls.class);
		controls.setContent(controlsIntent);

		TabSpec other = tabHost.newTabSpec("tab4");
		other.setIndicator("Path", getResources().getDrawable(R.drawable.path));
		Intent otherIntent = new Intent(this, ALynxOptions_Path.class);
		other.setContent(otherIntent);
		
		tabHost.addTab(video);
		tabHost.addTab(sound);
		tabHost.addTab(controls);
		tabHost.addTab(other);
	}
	
}
