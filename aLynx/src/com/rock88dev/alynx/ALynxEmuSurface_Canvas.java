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

import java.nio.ByteBuffer;

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

public class ALynxEmuSurface_Canvas extends SurfaceView implements Runnable,SurfaceHolder.Callback {

	private String[] padString = {
			"pad/dpad.png",
	        "pad/button_a.png",
	        "pad/button_b.png",
	        "pad/button_start.png",
	        "pad/button_opt1.png",
	        "pad/button_opt2.png",
			};

	static private Bitmap screen;
	static private ByteBuffer ScreenBuffer;
	
	private Canvas canvas;
	
	private Bitmap[] bitmap = new Bitmap[8];
	private Bitmap[] bitmap_big = new Bitmap[8];
	
	private Thread thread;
	private Paint ScreenPaint = new Paint();
	static SurfaceHolder holder = null;
	private ALynxSetting set;
	private int pad_no=1, s_width, s_height, fps=0, opacity=0, refresh=20, fps_print=0;
	private boolean running = false;
	private int size = 1;
	
	public ALynxEmuSurface_Canvas(Context context, ALynxSetting set, String rom) {
		super(context);
        //if (mode) pad_no=2; else pad_no=0;
        this.set = set;
        this.set.loadSettings();
        //opacity=this.set.pad[pad_no].opacity;
        refresh=this.set.canvas_refresh;
        
        if(set.orientation_opt==0) pad_no=0;
        size=ALynxSetting.pad[pad_no].size;
        
        ALynxInput.Init(size, pad_no);
        
        setOpacity(set.opacity);
        
        for(int i=0;i<padString.length;i++){
        	//if(ALynxSetting.button_show[i] == 1) 
        		bitmap[i] = ALynxUtils.getTextureFromBitmapResource(context, padString[i]);
        }
        
        switch(size)
        {
        	case 2:
            	for(int i=0;i<6;i++)
            	{
            		bitmap_big[i] = Bitmap.createScaledBitmap(bitmap[i], bitmap[i].getWidth()*2, bitmap[i].getHeight()*2, true);
            	}
        		break;
        	case 3: // 1.5x Size
            	for(int i=0;i<6;i++)
            	{
            		bitmap_big[i] = Bitmap.createScaledBitmap(bitmap[i], (int)(bitmap[i].getWidth()*1.5f), (int)(bitmap[i].getHeight()*1.5f), true);
            	}
        		break;
        }
        
		ALynxEmuProxy.AL_Set_Rom_Path(rom);
		ALynxEmuProxy.AL_Check_BIOS(set.bios_path);
		ALynxEmuProxy.AL_Emu_Set_Config(set.scale_opt, set.filter_opt, set.fskip_opt, set.sound_opt, set.fps_limit);
		
        int width = ALynxEmuProxy.AL_Image_Get_Width();
        int height = ALynxEmuProxy.AL_Image_Get_Height();
        
		ScreenBuffer=ByteBuffer.allocateDirect(width*height*2);    		
		screen=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
        holder = getHolder();
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
        
        requestFocus();
        setFocusableInTouchMode(true);
        
        ALynxEmuProxy.AL_Emu_Run();
		Run();
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		s_width=width;
		s_height=height;
	}

	public void surfaceCreated(SurfaceHolder holder) {

	}

	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		
		Log.d("ALYNX","Emu Thread Start");
		while(running){
			try {
				Thread.sleep(1000/refresh);
			} catch (InterruptedException e) {}
			canvas=holder.lockCanvas();
			if(canvas!=null)
			{
				drawScreen(canvas, ScreenPaint);
				holder.unlockCanvasAndPost(canvas);
			}
			/*
			fps++;
			if (System.currentTimeMillis()-time>1000){
				//Log.d("ALYNX","FPS = "+fps);
				time=System.currentTimeMillis();
				fps_print=fps;
				fps=0;
			}*/
			
		}
		Log.d("ALYNX","Emu Thread Stop");		
	}

	public void Run() {
		if(!running){
			running=true;
			thread = new Thread(this);
        	thread.start();
		}
	}
	
    public void Stop() {
        running=false;
        try {
        	thread.join();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        ALynxEmuProxy.AL_Emu_Stop();
        ALynxEmuProxy.AudioStop();
    }
    
    private void drawScreen(Canvas canvas, Paint paint) {
		paint.setColor(0xFF000000);
    	//canvas.drawColor(255);
    	canvas.drawRect(0, 0, s_width, s_height, paint);
    	
    	ALynxEmuProxy.AL_Image_Get_Buffer(ScreenBuffer);
    	
    	synchronized (screen){
    		ScreenBuffer.position(0);
    		screen.copyPixelsFromBuffer(ScreenBuffer);
		
    		Rect c = new Rect(0, 0, screen.getWidth(), screen.getHeight());
    		Rect v = new Rect(ALynxSetting.pad[pad_no].screen.x, ALynxSetting.pad[pad_no].screen.y, ALynxSetting.pad[pad_no].screen.x+ALynxSetting.pad[pad_no].screen.w, ALynxSetting.pad[pad_no].screen.y+ALynxSetting.pad[pad_no].screen.h);
    		canvas.drawBitmap(screen, c, v, paint);
			//}
    	
		paint.setAlpha(opacity);
		
		if(size==1)
		{
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_DPAD] == 1) canvas.drawBitmap(bitmap[0], ALynxSetting.pad[pad_no].dpad.x, ALynxSetting.pad[pad_no].dpad.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_A] == 1) canvas.drawBitmap(bitmap[1], ALynxSetting.pad[pad_no].key_a.x, ALynxSetting.pad[pad_no].key_a.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_B] == 1) canvas.drawBitmap(bitmap[2], ALynxSetting.pad[pad_no].key_b.x, ALynxSetting.pad[pad_no].key_b.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_START] == 1) canvas.drawBitmap(bitmap[3], ALynxSetting.pad[pad_no].key_start.x, ALynxSetting.pad[pad_no].key_start.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT1] == 1) canvas.drawBitmap(bitmap[4], ALynxSetting.pad[pad_no].key_opt1.x, ALynxSetting.pad[pad_no].key_opt1.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT2] == 1) canvas.drawBitmap(bitmap[5], ALynxSetting.pad[pad_no].key_opt2.x, ALynxSetting.pad[pad_no].key_opt2.y, paint);
		}
		else
		{
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_DPAD] == 1) canvas.drawBitmap(bitmap_big[0], ALynxSetting.pad[pad_no].dpad.x, ALynxSetting.pad[pad_no].dpad.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_A] == 1) canvas.drawBitmap(bitmap_big[1], ALynxSetting.pad[pad_no].key_a.x, ALynxSetting.pad[pad_no].key_a.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_B] == 1) canvas.drawBitmap(bitmap_big[2], ALynxSetting.pad[pad_no].key_b.x, ALynxSetting.pad[pad_no].key_b.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_START] == 1) canvas.drawBitmap(bitmap_big[3], ALynxSetting.pad[pad_no].key_start.x, ALynxSetting.pad[pad_no].key_start.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT1] == 1) canvas.drawBitmap(bitmap_big[4], ALynxSetting.pad[pad_no].key_opt1.x, ALynxSetting.pad[pad_no].key_opt1.y, paint);
			if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT2] == 1) canvas.drawBitmap(bitmap_big[5], ALynxSetting.pad[pad_no].key_opt2.x, ALynxSetting.pad[pad_no].key_opt2.y, paint);			
		}
		
		paint.setAlpha(255);
		
		paint.setARGB(255, 255, 255, 255);
		//!canvas.drawText("FPS: "+ALynxEmuProxy.AL_Emu_Get_FPS()+"/"+fps_print, 0, 10, paint);
    	}
    	
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
	}
	
	@Override 
    public boolean onTouchEvent(MotionEvent event) {
		return ALynxInput.onMultiTouchEvent(pad_no, event);
    	//return ALynxInput.onSingleTouchEvent(pad_no, event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_MENU) ALynxEmuProxy.AL_Emu_Pause();
		if(ALynxInput.KeyDown(keyCode, event)) return true;
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		if(ALynxInput.KeyUp(keyCode, event)) return true;
		return super.onKeyUp(keyCode, event);
	}

}
