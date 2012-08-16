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
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;


public final class ALynxSetting {
	final public static int CANVAS = 0;
	final public static int GLES = 1;
	final public static int GLES_NATIVE = 2;
	
	final public static int SAMPLE_1024 = 1024;
	final public static int SAMPLE_2048 = 2048;
	final public static int SAMPLE_4096 = 4096;
	final public static int SAMPLE_8192 = 8192;
	
	final public static int OPASITY_NONE = 0;
	final public static int OPASITY_LOW = 1;
	final public static int OPASITY_MEDIUM = 2;
	final public static int OPASITY_HIGHT = 3;
	
	final public static int BUTTON_DPAD = 0;
	final public static int BUTTON_A = 1;
	final public static int BUTTON_B = 2;
	final public static int BUTTON_START = 3;
	final public static int BUTTON_OPT1 = 4;
	final public static int BUTTON_OPT2 = 5;
	
	public int orientation_opt = 0;
	public int filter_opt = 0;
	public int fskip_opt = 0;
	public int sound_opt = 0;
	public int scale_opt = 1;
	public int canvas_refresh = 20;
	public int render = CANVAS;
	public int fps_limit = 1;
	public int sample_size = SAMPLE_4096;
	public int vibrate = 0;
	public int powervr_fix = 0;
	public int opacity = 0;
	private int first_run = 1;
	public int first_run2 = 1;
	private static int w;
	private static int h;
	public static int bt_gamepad = 1;
	
	public static int st_sample_size = SAMPLE_4096;
	
	public String rom_path = null;
	public String state_path = null;
	public String pic_path = null;
	public String bios_path = null;
	
	public static int[] button_show = {1,1,1,1,1,1};
	public int[] hard_key = {0,0,0,0,0,0,0,0,0};
	public static int[] st_hard_key = {0,0,0,0,0,0,0,0,0};
	
	SharedPreferences settings;
	  
	public static class Rect {
		Rect(int x, int y, int w, int h){
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
			}
		Rect(int x, int y){
			this.x=x;
			this.y=y;
			this.w=64;
			this.h=64;
			}
		public int x,y,w,h;
		}
	  
	  public static class Pad {
		  Pad(int i, int o, int s, Rect a, Rect b, Rect c, Rect d, Rect e, Rect f, Rect g){
			  type_id=i;
			  opacity=0;
			  size=s;
			  screen=a;
			  dpad=b;
			  key_a=c;
			  key_b=d;
			  key_start=e;
			  key_opt1=f;
			  key_opt2=g;
		  }
		  public int type_id;
		  public int opacity;
		  public int size;
		  public Rect screen, dpad, key_a, key_b, key_start, key_opt1, key_opt2;
	  }
	  
	  public static Pad[] pad = {
	  			// 0: PORTRAIT / PORTRAIT GAME
	  			new Pad(0,0,1,new Rect(0,0,160,102), new Rect(0,110), new Rect(200, 64), new Rect(200, 128), new Rect(140, 0), new Rect(140, 100), new Rect(140, 164)),
	  			// 	: LANDSCAPE / LANDSCAPE GAME
	  			new Pad(1,0,1,new Rect(0,0,160,102), new Rect(0,110), new Rect(200, 64), new Rect(200, 128), new Rect(140, 0), new Rect(140, 100), new Rect(140, 164)),
				};
	  
	  public ALynxSetting(Activity a) {
		  settings = a.getSharedPreferences("alynx_config", Activity.MODE_PRIVATE);
		  
		  Display d = a.getWindowManager().getDefaultDisplay();
		  w = d.getWidth();
		  h = d.getHeight();
		  if(w>h)
		  {
			  int w1 = w;
			  w=h;
			  h=w1;
		  }
		  Log.d("ALYNX","w = "+w+" h = "+h);
		  
		  this.loadSettings();
	  }

	public void loadSettings() {
		  orientation_opt = settings.getInt("orientation_opt", orientation_opt);
		  filter_opt = settings.getInt("filter_opt", filter_opt);
		  fskip_opt = settings.getInt("fskip_opt", fskip_opt);
		  sound_opt = settings.getInt("sound_opt", sound_opt);
		  scale_opt = settings.getInt("scale_opt", scale_opt);
		  canvas_refresh = settings.getInt("canvas_refresh", canvas_refresh);
		  render = settings.getInt("render", render);
		  fps_limit = settings.getInt("fps_limit", fps_limit);
		  sample_size = settings.getInt("sample_size", sample_size);
		  st_sample_size = sample_size;
		  opacity = settings.getInt("opacity", opacity);
		  bt_gamepad = settings.getInt("bt_gamepad", bt_gamepad);
		  
		  vibrate = settings.getInt("vibrate", vibrate);
		  powervr_fix  = settings.getInt("powervr_fix", powervr_fix);
		  
		  rom_path = settings.getString("rom_path", rom_path);
		  state_path = settings.getString("state_path", state_path);
		  pic_path = settings.getString("pic_path", pic_path);
		  bios_path = settings.getString("bios_path", bios_path);
		  
		  first_run = settings.getInt("first_run", first_run);
		  first_run2 = settings.getInt("first_run2", first_run2);
		  
		  if(first_run==1)
		  {
			  first_run = 0;
			  Log.d("ALYNX","First Run!");
			  float p = 160.0f/102.0f;
			  pad[0] = new Pad(0,0,1,new Rect(0,96,w,(int)(w/p)), new Rect(0,h-128,128,128), new Rect(w-128, h-64), new Rect(w-64, h-64), new Rect(0, 0), new Rect(w-128, 0), new Rect(w-64, 0));
			  pad[1] = new Pad(0,0,1,new Rect(20,0,h-40,(int)((h-40)/p)), new Rect(0,w-128,128,128), new Rect(h-128, w-64), new Rect(h-64, w-64), new Rect(0, 0), new Rect(h-128, 0), new Rect(h-64, 0));
		  }
		  else
		  {
			  for(int i=0;i<2;i++){
				  pad[i].opacity = settings.getInt("pad"+i+"_opacity", pad[i].opacity);
				  pad[i].type_id = settings.getInt("pad"+i+"_type_id", pad[i].type_id);
				  pad[i].size = settings.getInt("pad"+i+"_size", pad[i].size);
				  
				  pad[i].screen.x = settings.getInt("pad"+i+"_screen_x", pad[i].screen.x);
				  pad[i].screen.y = settings.getInt("pad"+i+"_screen_y", pad[i].screen.y);
				  pad[i].screen.w = settings.getInt("pad"+i+"_screen_w", pad[i].screen.w);
				  pad[i].screen.h = settings.getInt("pad"+i+"_screen_h", pad[i].screen.h);
				
				  pad[i].dpad.x = settings.getInt("pad"+i+"_dpad_x", pad[i].dpad.x);
				  pad[i].dpad.y = settings.getInt("pad"+i+"_dpad_y", pad[i].dpad.y);
				  pad[i].dpad.w = settings.getInt("pad"+i+"_dpad_w", pad[i].dpad.w);
				  pad[i].dpad.h = settings.getInt("pad"+i+"_dpad_h", pad[i].dpad.h);
				  
				  pad[i].key_a.x = settings.getInt("pad"+i+"_key_a_x", pad[i].key_a.x);
				  pad[i].key_a.y = settings.getInt("pad"+i+"_key_a_y", pad[i].key_a.y);
				  pad[i].key_a.w = settings.getInt("pad"+i+"_key_a_w", pad[i].key_a.w);
				  pad[i].key_a.h = settings.getInt("pad"+i+"_key_a_h", pad[i].key_a.h);
				  
				  pad[i].key_b.x = settings.getInt("pad"+i+"_key_b_x", pad[i].key_b.x);
				  pad[i].key_b.y = settings.getInt("pad"+i+"_key_b_y", pad[i].key_b.y);
				  pad[i].key_b.w = settings.getInt("pad"+i+"_key_b_w", pad[i].key_b.w);
				  pad[i].key_b.h = settings.getInt("pad"+i+"_key_b_h", pad[i].key_b.h);
				  
				  pad[i].key_start.x = settings.getInt("pad"+i+"_key_start_x", pad[i].key_start.x);
				  pad[i].key_start.y = settings.getInt("pad"+i+"_key_start_y", pad[i].key_start.y);
				  pad[i].key_start.w = settings.getInt("pad"+i+"_key_start_w", pad[i].key_start.w);
				  pad[i].key_start.h = settings.getInt("pad"+i+"_key_start_h", pad[i].key_start.h);
				  
				  pad[i].key_opt1.x = settings.getInt("pad"+i+"_key_opt1_x", pad[i].key_opt1.x);
				  pad[i].key_opt1.y = settings.getInt("pad"+i+"_key_opt1_y", pad[i].key_opt1.y);
				  pad[i].key_opt1.w = settings.getInt("pad"+i+"_key_opt1_w", pad[i].key_opt1.w);
				  pad[i].key_opt1.h = settings.getInt("pad"+i+"_key_opt1_h", pad[i].key_opt1.h);
				  
				  pad[i].key_opt2.x = settings.getInt("pad"+i+"_key_opt2_x", pad[i].key_opt2.x);
				  pad[i].key_opt2.y = settings.getInt("pad"+i+"_key_opt2_y", pad[i].key_opt2.y);
				  pad[i].key_opt2.w = settings.getInt("pad"+i+"_key_opt2_w", pad[i].key_opt2.w);
				  pad[i].key_opt2.h = settings.getInt("pad"+i+"_key_opt2_h", pad[i].key_opt2.h);
			  }
		  }
		  
		  for(int i=0;i<hard_key.length;i++){
			  hard_key[i] = settings.getInt("hard_key_"+i, hard_key[i]);
			  st_hard_key[i] = hard_key[i];
		  }
		  
		  for(int i=0;i<button_show.length;i++){
			  button_show[i] = settings.getInt("button_show_"+i, button_show[i]);
		  }
		  
	  }

	  public void saveSettings() {
	    SharedPreferences.Editor editor = settings.edit();

	    editor.putInt("orientation_opt", orientation_opt);
	    editor.putInt("filter_opt", filter_opt);
	    editor.putInt("fskip_opt", fskip_opt);
	    editor.putInt("sound_opt", sound_opt);
	    editor.putInt("scale_opt", scale_opt);
	    editor.putInt("canvas_refresh", canvas_refresh);
	    editor.putInt("render", render);
	    editor.putInt("fps_limit", fps_limit);
	    editor.putInt("sample_size", sample_size);
	    editor.putInt("vibrate", vibrate);
	    editor.putInt("powervr_fix", powervr_fix);
	    editor.putInt("opacity", opacity);
	    editor.putInt("first_run", first_run);
	    editor.putInt("first_run2", first_run2);
	    editor.putInt("bt_gamepad", bt_gamepad);
	    
	    editor.putString("rom_path", rom_path);
	    editor.putString("state_path", state_path);
	    editor.putString("pic_path", pic_path);
	    editor.putString("bios_path", bios_path);
	    
	    for(int i=0;i<2;i++){
			editor.putInt("pad"+i+"_opacity", pad[i].opacity);
			editor.putInt("pad"+i+"_type_id", pad[i].type_id);
			editor.putInt("pad"+i+"_size", pad[i].size);
			
			editor.putInt("pad"+i+"_screen_x", pad[i].screen.x);
			editor.putInt("pad"+i+"_screen_y", pad[i].screen.y);
			editor.putInt("pad"+i+"_screen_w", pad[i].screen.w);
			editor.putInt("pad"+i+"_screen_h", pad[i].screen.h);
					
			editor.putInt("pad"+i+"_dpad_x", pad[i].dpad.x);
			editor.putInt("pad"+i+"_dpad_y", pad[i].dpad.y);
			editor.putInt("pad"+i+"_dpad_w", pad[i].dpad.w);
			editor.putInt("pad"+i+"_dpad_h", pad[i].dpad.h);
					  
			editor.putInt("pad"+i+"_key_a_x", pad[i].key_a.x);
			editor.putInt("pad"+i+"_key_a_y", pad[i].key_a.y);
			editor.putInt("pad"+i+"_key_a_w", pad[i].key_a.w);
			editor.putInt("pad"+i+"_key_a_h", pad[i].key_a.h);
					  
			editor.putInt("pad"+i+"_key_b_x", pad[i].key_b.x);
			editor.putInt("pad"+i+"_key_b_y", pad[i].key_b.y);
			editor.putInt("pad"+i+"_key_b_w", pad[i].key_b.w);
			editor.putInt("pad"+i+"_key_b_h", pad[i].key_b.h);
					  
			editor.putInt("pad"+i+"_key_start_x", pad[i].key_start.x);
			editor.putInt("pad"+i+"_key_start_y", pad[i].key_start.y);
			editor.putInt("pad"+i+"_key_start_w", pad[i].key_start.w);
			editor.putInt("pad"+i+"_key_start_h", pad[i].key_start.h);
					  
			editor.putInt("pad"+i+"_key_opt1_x", pad[i].key_opt1.x);
			editor.putInt("pad"+i+"_key_opt1_y", pad[i].key_opt1.y);
			editor.putInt("pad"+i+"_key_opt1_w", pad[i].key_opt1.w);
			editor.putInt("pad"+i+"_key_opt1_h", pad[i].key_opt1.h);
					  
			editor.putInt("pad"+i+"_key_opt2_x", pad[i].key_opt2.x);
			editor.putInt("pad"+i+"_key_opt2_y", pad[i].key_opt2.y);
			editor.putInt("pad"+i+"_key_opt2_w", pad[i].key_opt2.w);
			editor.putInt("pad"+i+"_key_opt2_h", pad[i].key_opt2.h);
		}
		  
		for(int i=0;i<hard_key.length;i++){
			editor.putInt("hard_key_"+i, hard_key[i]);
		}
		  
		for(int i=0;i<button_show.length;i++){
			editor.putInt("button_show_"+i, button_show[i]);
		}
		  
		editor.commit();
	  }
	  
	  public static void ResetPad(int size, int no)
	  {
		  float p = 160.0f/102.0f;
		  if(no==0)
		  {
			  switch(size)
			  {
			  	case 1:
			  	{
			  		pad[0] = new Pad(0,0,1,new Rect(0,96,w,(int)(w/p)), new Rect(0,h-128,128,128), new Rect(w-128, h-64), new Rect(w-64, h-64), new Rect(0, 0), new Rect(w-128, 0), new Rect(w-64, 0));
			  		break;
			  	}
			  	case 2:
			  	{
			  		pad[0] = new Pad(0,0,2,new Rect(0,96,w,(int)(w/p)), new Rect(0,h-256,256,256), new Rect(w-256, h-128), new Rect(w-128, h-128), new Rect(0, 0), new Rect(w-256, 0), new Rect(w-128, 0));
			  		break;
			  	}
			  	case 3: // 1.5x Size
			  	{
			  		pad[0] = new Pad(0,0,3,new Rect(0,96,w,(int)(w/p)), new Rect(0,h-192,192,192), new Rect(w-192, h-96), new Rect(w-96, h-96), new Rect(0, 0), new Rect(w-192, 0), new Rect(w-96, 0));
			  		break;		  		
			  	}
			  }			  
		  }
		  if(no==1)
		  {
			  switch(size)
			  {
			  	case 1:
			  	{
			  		//pad[0] = new Pad(0,0,1,new Rect(0,96,w,(int)(w/p)), new Rect(0,h-128,128,128), new Rect(w-128, h-64), new Rect(w-64, h-64), new Rect(0, 0), new Rect(w-128, 0), new Rect(w-64, 0));
			  		int ww = (int)((h-40)/p);
			  		if (ww > w) ww = w;
			  		pad[1] = new Pad(0,0,1,new Rect(20,0,h-40,ww), new Rect(0,w-128,128,128), new Rect(h-128, w-64), new Rect(h-64, w-64), new Rect(0, 0), new Rect(h-128, 0), new Rect(h-64, 0));
			  		break;
			  	}
			  	case 2:
			  	{
			  		//pad[0] = new Pad(0,0,2,new Rect(0,96,w,(int)(w/p)), new Rect(0,h-256,256,256), new Rect(w-256, h-128), new Rect(w-128, h-128), new Rect(0, 0), new Rect(w-256, 0), new Rect(w-128, 0));
			  		int ww = (int)((h-40)/p);
			  		if (ww > w) ww = w;
			  		pad[1] = new Pad(0,0,2,new Rect(20,0,h-40,ww), new Rect(0,w-256,256,256), new Rect(h-256, w-128), new Rect(h-128, w-128), new Rect(0, 0), new Rect(h-256, 0), new Rect(h-128, 0));
			  		break;
			  	}
			  	case 3: // 1.5x Size
			  	{
			  		//pad[0] = new Pad(0,0,3,new Rect(0,96,w,(int)(w/p)), new Rect(0,h-192,192,192), new Rect(w-192, h-96), new Rect(w-96, h-96), new Rect(0, 0), new Rect(w-192, 0), new Rect(w-96, 0));
			  		int ww = (int)((h-40)/p);
			  		if (ww > w) ww = w;
			  		pad[1] = new Pad(0,0,3,new Rect(20,0,h-40,ww), new Rect(0,w-192,192,192), new Rect(h-192, w-96), new Rect(h-96, w-96), new Rect(0, 0), new Rect(h-192, 0), new Rect(h-96, 0));
			  		break;		  		
			  	}
			  }
		  }
	  }
	}
