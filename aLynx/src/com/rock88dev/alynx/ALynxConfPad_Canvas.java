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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ALynxConfPad_Canvas extends SurfaceView implements Runnable,SurfaceHolder.Callback {
	
	class nRect{
		nRect(int x,int y,int w,int h){
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
		}
		int x,y,w,h;
	}
	
	private nRect[] rect = new nRect[10];
	
	private Bitmap[] screen = new Bitmap[2];
	private Bitmap[] bitmap = new Bitmap[8];
	
	private String[] padString = {
			"img/dpad.png",
	        "img/button_a.png",
	        "img/button_b.png",
	        "img/button_start.png",
	        "img/button_opt1.png",
	        "img/button_opt2.png",
	        "img/move.png",
	        "img/scale.png"
			};
	
	private Thread thread;
	private Paint ScreenPaint = new Paint();
	static SurfaceHolder holder = null;
	//private long time = 0, fps = 0;
	private boolean running = false, pad_landscape = true, down = false, down_scale=false;
	public int s_width, s_height;

	public int pad_no = 0;
	private int opacity = 0;

	private int current_select = 0;
	private ALynxSetting set;
	public int size = 1;
	
    public ALynxConfPad_Canvas(Context context, ALynxSetting set, boolean mode) { 
        super(context);
        if (mode) pad_no=1; else pad_no=0;
        this.set = set;

        screen[0] = ALynxUtils.getTextureFromBitmapResource(context, "img/logo_h.png");
        screen[1] = ALynxUtils.getTextureFromBitmapResource(context, "img/logo_h.png");
        
        rect[0] = new nRect(0,0,0,0);
        
        for(int i=0;i<padString.length;i++){
        	bitmap[i] = ALynxUtils.getTextureFromBitmapResource(context, padString[i]);
        	rect[i+1] = new nRect(0,0,bitmap[i].getWidth(),bitmap[i].getHeight());
        }
        
        size = ALynxSetting.pad[pad_no].size;
        loadPadPosition();
        setOpacity(set.opacity);
        setSize(ALynxSetting.pad[pad_no].size);
        
        holder = getHolder();
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
        
        requestFocus();
        setFocusableInTouchMode(true);
    }

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		s_width = width;
		s_height = height;
	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		switch(keyCode)
		{
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				current_select--;
				if (current_select<0) current_select=6;
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				current_select++;
				if (current_select>6) current_select=0;
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		Log.d("ALYNX","Thread Start");
		while(running){
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {}
			Canvas canvas=holder.lockCanvas();
			if(canvas!=null)
			{
				
				drawPad(canvas, ScreenPaint);
				
				holder.unlockCanvasAndPost(canvas);
			}
			/*fps++;
			if (System.currentTimeMillis()-time>1000){
				Log.d("ALYNX","FPS = "+fps);
				time=System.currentTimeMillis();
				fps=0;
			}*/
		}
		Log.d("ALYNX","Thread Stop");
	}
	
	public void Run() {
		if(!running){
			running=true;
			thread = new Thread(this);
        	thread.start();
		}
	}
	
    public void Stop() {
    	savePadPosition();
        running=false;
        try {
        	Thread.sleep(10L);
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
	public void setButton(int i){
		current_select=i;
	}
	
	public void setOpacity(int i){
		switch(i)
		{
			case ALynxSetting.OPASITY_NONE:
				opacity = 255;
				break;
			case ALynxSetting.OPASITY_LOW:
				opacity = 210;
				break;
			case ALynxSetting.OPASITY_MEDIUM:
				opacity = 150;
				break;
			case ALynxSetting.OPASITY_HIGHT:
				opacity = 80;
				break;
			default:
				opacity = 255;
				break;
		}
		set.opacity = i;
		Log.d("ALYNX","opacity = "+set.opacity);
		set.saveSettings();
	}
	
	public void resizeImage(int mode)
	{
		switch(mode)
		{
			case 0:
				rect[0].x=0;
		    	rect[0].y=0;
		    	rect[0].w=s_width;
		    	rect[0].h=s_height;			
				break;
			case 1:
				rect[0].x=20;
		    	rect[0].y=20;
		    	rect[0].w=s_width-40;
		    	rect[0].h=s_height-40;
				break;
		}
		//savePadPosition();
	}
	
	public void setSize(int size){
		if(ALynxSetting.pad[pad_no].size != size)
		{
			ALynxSetting.ResetPad(size, pad_no);
			this.size = size;
			loadPadPosition();
		}
		ALynxSetting.pad[pad_no].size = size;
		this.size = size;
		set.saveSettings();
	}
	
    synchronized private void drawPad(Canvas canvas, Paint paint){
    	canvas.drawColor(255);
    	canvas.drawRect(0, 0, s_width, s_height, ScreenPaint);
    	int d = 0;
    	//if ((pad_no==1)||(pad_no==3)) d=1;
    	
		Rect c = new Rect(0, 0, screen[d].getWidth(), screen[d].getHeight());
		Rect v = new Rect(rect[0].x, rect[0].y, rect[0].x+rect[0].w, rect[0].y+rect[0].h);
		
    	canvas.drawBitmap(screen[d], c, v, ScreenPaint);
		
		//float f = (100-opacity)*(255.0f/100.0f);
		ScreenPaint.setAlpha(opacity);
		
		//Log.d("ALYNX","opacity = "+opacity+" f = "+f);
		
		for(int i=0;i<6;i++)
		{
			switch(size)
			{
				case 1:
					canvas.drawBitmap(bitmap[i], rect[i+1].x, rect[i+1].y, ScreenPaint);
					break;
				case 2:
				{
					Rect src = new Rect(0, 0, bitmap[i].getWidth(), bitmap[i].getHeight());
					Rect dst = new Rect(rect[i+1].x, rect[i+1].y, rect[i+1].x+bitmap[i].getWidth()*2, rect[i+1].y+bitmap[i].getHeight()*2);
					canvas.drawBitmap(bitmap[i], src, dst, ScreenPaint);
					break;
				}
				case 3:
				{
					Rect src = new Rect(0, 0, bitmap[i].getWidth(), bitmap[i].getHeight());
					Rect dst = new Rect(rect[i+1].x, rect[i+1].y, (int) (rect[i+1].x+bitmap[i].getWidth()*1.5f), (int) (rect[i+1].y+bitmap[i].getHeight()*1.5f));
					canvas.drawBitmap(bitmap[i], src, dst, ScreenPaint);
					break;
				}
			}
		}
		
		ScreenPaint.setAlpha(255);
		
	    rect[7].x = rect[current_select].x;
	    rect[7].y = rect[current_select].y;
	    canvas.drawBitmap(bitmap[6], rect[7].x, rect[7].y, ScreenPaint);
	    
	    if(current_select==0){
		    rect[8].x = rect[current_select].x+rect[current_select].w-rect[8].w;
		    rect[8].y = rect[current_select].y+rect[current_select].h-rect[8].h;
	    	canvas.drawBitmap(bitmap[7], rect[8].x, rect[8].y, ScreenPaint);   	
	    }
    }
    
    @Override 
    synchronized public boolean onTouchEvent(MotionEvent event) {
    	int x = (int)event.getX();
    	int y = (int)event.getY();
    	
    	switch(event.getAction()){
    		case MotionEvent.ACTION_DOWN:
    			if(((rect[7].x<=x)&&(x<=(rect[7].x+rect[7].w))) && 
    				((rect[7].y<=y)&&(y<(rect[7].y+rect[7].h))))
    				{
    					down=true;
    					//Log.d("ALYNX","DOWN");
    				}
    			if (current_select==0){
	    			if(((rect[8].x<=x)&&(x<=(rect[8].x+rect[8].w))) && 
	        				((rect[8].y<=y)&&(y<(rect[8].y+rect[8].h))))
	        				{
	    						down_scale=true;
	        					//Log.d("ALYNX","DOWN");
	        				}
    			}
    			break;
    		case MotionEvent.ACTION_MOVE:
    			if(down){
    				//Log.d("ALYNX","MOVE");
    				rect[7].x = x-rect[7].w/2;
    				rect[7].y = y-rect[7].h/2;

    				if(rect[7].x<0) rect[7].x=0;
    				if(rect[7].y<0) rect[7].y=0;
    				int size = 1;
    				//Log.d("ALYNX","1: x = "+rect[7].x+"  y = "+rect[7].y);
    				if(rect[7].x+rect[current_select].w*size>s_width) rect[7].x=s_width-rect[current_select].w*size;
    				if(rect[7].y+rect[current_select].h*size>s_height) rect[7].y=s_height-rect[current_select].h*size;
    				
    				rect[current_select].x = rect[7].x;
    				rect[current_select].y = rect[7].y;
    				//Log.d("ALYNX","2: x = "+rect[7].x+"  y = "+rect[7].y);
    			}
    			if(down_scale){
    				rect[8].x = x+rect[8].w/2;
    				rect[8].y = y+rect[8].h/2;
    				
    				int min_w = 160, min_h = 102;
    				
    				if(!pad_landscape){
    					min_w=102;
    					min_h=160;
    				}
    				
    				float p = (float)min_w/(float)min_h;
    				
    				if(rect[8].x-rect[7].x<min_w) rect[8].x=rect[7].x+min_w;
    				if(rect[8].y-rect[7].y<min_h) rect[8].y=rect[7].y+min_h;
    				
    				rect[current_select].w = rect[8].x-rect[current_select].x;
    				//rect[current_select].h = rect[8].y-rect[current_select].y;
    				rect[current_select].h = (int)(rect[current_select].w/p);
    				
    				if(rect[current_select].x+rect[current_select].w>s_width) rect[current_select].w=s_width-rect[current_select].x;
    				if(rect[current_select].y+rect[current_select].h>s_height) rect[current_select].h=s_height-rect[current_select].y;
    			}
    			break;
    		case MotionEvent.ACTION_UP:
    			//Log.d("ALYNX","UP");
    			down = false;
    			down_scale = false;
    			
    			break;
    	}
        return true;//super.onTouchEvent(event);
    }
    
    void loadPadPosition(){
        rect[0].x = ALynxSetting.pad[pad_no].screen.x;
        rect[0].y = ALynxSetting.pad[pad_no].screen.y;
        rect[0].w = ALynxSetting.pad[pad_no].screen.w;
        rect[0].h = ALynxSetting.pad[pad_no].screen.h;
        rect[1].x = ALynxSetting.pad[pad_no].dpad.x;
        rect[1].y = ALynxSetting.pad[pad_no].dpad.y;
        rect[2].x = ALynxSetting.pad[pad_no].key_a.x;
        rect[2].y = ALynxSetting.pad[pad_no].key_a.y;
        rect[3].x = ALynxSetting.pad[pad_no].key_b.x;
        rect[3].y = ALynxSetting.pad[pad_no].key_b.y;
        rect[4].x = ALynxSetting.pad[pad_no].key_start.x;
        rect[4].y = ALynxSetting.pad[pad_no].key_start.y;
        rect[5].x = ALynxSetting.pad[pad_no].key_opt1.x;
        rect[5].y = ALynxSetting.pad[pad_no].key_opt1.y;       
        rect[6].x = ALynxSetting.pad[pad_no].key_opt2.x;
        rect[6].y = ALynxSetting.pad[pad_no].key_opt2.y;
        
        rect[7].x = rect[0].x;
        rect[7].y = rect[0].x;
        
        for(int i=0;i<6;i++){
        	switch(size)
        	{
        		case 1:
        			rect[i+1].w = bitmap[i].getWidth();
        			rect[i+1].h = bitmap[i].getHeight();
        			break;
        		case 2:
        			rect[i+1].w = bitmap[i].getWidth()*2;
        			rect[i+1].h = bitmap[i].getHeight()*2;
        			break;
        		case 3: // 1.5x Size
        			rect[i+1].w = (int) (bitmap[i].getWidth()*1.5f);
        			rect[i+1].h = (int) (bitmap[i].getHeight()*1.5f);
        			break;
        	}
        	
        }
        //opacity = set.opacity;//ALynxSetting.pad[pad_no].opacity;
    }
    
    private void savePadPosition(){
    	ALynxSetting.pad[pad_no].screen.x = rect[0].x;
    	ALynxSetting.pad[pad_no].screen.y = rect[0].y;
    	ALynxSetting.pad[pad_no].screen.w = rect[0].w;
    	ALynxSetting.pad[pad_no].screen.h = rect[0].h;
    	ALynxSetting.pad[pad_no].dpad.x = rect[1].x;
    	ALynxSetting.pad[pad_no].dpad.y = rect[1].y;
    	ALynxSetting.pad[pad_no].dpad.w = rect[1].w;
    	ALynxSetting.pad[pad_no].dpad.h = rect[1].h;
    	ALynxSetting.pad[pad_no].key_a.x = rect[2].x;
    	ALynxSetting.pad[pad_no].key_a.y = rect[2].y;
    	ALynxSetting.pad[pad_no].key_a.w = rect[2].w;
    	ALynxSetting.pad[pad_no].key_a.h = rect[2].h;
    	ALynxSetting.pad[pad_no].key_b.x = rect[3].x;
    	ALynxSetting.pad[pad_no].key_b.y = rect[3].y;
    	ALynxSetting.pad[pad_no].key_b.w = rect[3].w;
    	ALynxSetting.pad[pad_no].key_b.h = rect[3].h;
    	ALynxSetting.pad[pad_no].key_start.x = rect[4].x;
    	ALynxSetting.pad[pad_no].key_start.y = rect[4].y;
    	ALynxSetting.pad[pad_no].key_start.w = rect[4].w;
    	ALynxSetting.pad[pad_no].key_start.h = rect[4].h;
    	ALynxSetting.pad[pad_no].key_opt1.x = rect[5].x;
        ALynxSetting.pad[pad_no].key_opt1.y = rect[5].y;
    	ALynxSetting.pad[pad_no].key_opt1.w = rect[5].w;
        ALynxSetting.pad[pad_no].key_opt1.h = rect[5].h; 
        ALynxSetting.pad[pad_no].key_opt2.x = rect[6].x;
        ALynxSetting.pad[pad_no].key_opt2.y = rect[6].y;
        ALynxSetting.pad[pad_no].key_opt2.w = rect[6].w;
        ALynxSetting.pad[pad_no].key_opt2.h = rect[6].h;
        //set.opacity = opacity;
        set.saveSettings();
        
        try {
			Thread.sleep(20L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
}
