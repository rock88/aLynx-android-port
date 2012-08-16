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
import android.webkit.WebView;

public class ALynxHelp extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    
    	WebView webview = new WebView(this);
    	
    	//String customHtml = "<html><body><h1>Help...</h1></body></html>";
    	//webview.loadData(customHtml, "text/html", "UTF-8");
    	webview.loadUrl("file:///android_asset/help/help.html");
    	setContentView(webview);
    }
}
