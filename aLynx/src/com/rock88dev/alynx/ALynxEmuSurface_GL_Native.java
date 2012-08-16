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

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ALynxEmuSurface_GL_Native extends GLSurfaceView implements android.opengl.GLSurfaceView.Renderer{
	
	private ALynxSetting set;
	private Context mContext = null;
	private int pad_no = 1;
	private long fps=0, tick=0;
	private int size = 1;
	
	public ALynxEmuSurface_GL_Native(Context context, ALynxSetting set, String rom) {
		super(context);
		mContext = context;
		this.set=set;
		this.set.loadSettings();
		
		setRenderer(this);
		
		if(set.orientation_opt==0) pad_no=0;
		size=ALynxSetting.pad[pad_no].size;
        ALynxInput.Init(size, pad_no);
        
		ALynxEmuProxy.AL_GL_NativeSetConfig(set.powervr_fix, set.opacity, size);
		
		ALynxEmuProxy.AL_Set_Rom_Path(rom);
		ALynxEmuProxy.AL_Check_BIOS(set.bios_path);
		ALynxEmuProxy.AL_Emu_Set_Config(1, 0, set.fskip_opt, set.sound_opt, set.fps_limit);
		ALynxEmuProxy.AL_Emu_Run();
	}

	public void onDrawFrame(GL10 gl) {
		ALynxEmuProxy.AL_GL_NativeRender();
		/*
	    fps++;
	    if ((System.currentTimeMillis() - tick) > 1000){
	    	Log.d("ALYNX","FPS: "+ALynxEmuProxy.AL_Emu_Get_FPS()+"/"+fps);
	    	fps=0;
	    	tick = System.currentTimeMillis();
	    }*/
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		LoadTextures();
		ALynxEmuProxy.AL_GL_NativeSetScreenRect(ALynxSetting.pad[pad_no].screen.x, ALynxSetting.pad[pad_no].screen.y, ALynxSetting.pad[pad_no].screen.w, ALynxSetting.pad[pad_no].screen.h);
		ALynxEmuProxy.AL_GL_NativeSetRenderRect(0, 0, w, h);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		ALynxEmuProxy.AL_GL_NativeInit();
		
	}
	
    private void LoadTextures(){
    	if(ALynxSetting.button_show[ALynxSetting.BUTTON_DPAD] == 1) LoadTextures(0,"pad/ngl_dpad.png");
    	if(ALynxSetting.button_show[ALynxSetting.BUTTON_A] == 1) LoadTextures(1,"pad/ngl_button_a.png");
    	if(ALynxSetting.button_show[ALynxSetting.BUTTON_B] == 1) LoadTextures(2,"pad/ngl_button_b.png");
    	if(ALynxSetting.button_show[ALynxSetting.BUTTON_START] == 1) LoadTextures(3,"pad/ngl_button_start.png");
    	if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT1] == 1) LoadTextures(4,"pad/ngl_button_opt1.png");
    	if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT2] == 1) LoadTextures(5,"pad/ngl_button_opt2.png");
        
        ALynxEmuProxy.AL_GL_NativeSetButton(0, ALynxSetting.pad[pad_no].dpad.x, ALynxSetting.pad[pad_no].dpad.y, ALynxSetting.pad[pad_no].dpad.w, ALynxSetting.pad[pad_no].dpad.h);
        ALynxEmuProxy.AL_GL_NativeSetButton(1, ALynxSetting.pad[pad_no].key_a.x, ALynxSetting.pad[pad_no].key_a.y, ALynxSetting.pad[pad_no].key_a.w, ALynxSetting.pad[pad_no].key_a.h);
        ALynxEmuProxy.AL_GL_NativeSetButton(2, ALynxSetting.pad[pad_no].key_b.x, ALynxSetting.pad[pad_no].key_b.y, ALynxSetting.pad[pad_no].key_b.w, ALynxSetting.pad[pad_no].key_b.h);
        ALynxEmuProxy.AL_GL_NativeSetButton(3, ALynxSetting.pad[pad_no].key_start.x, ALynxSetting.pad[pad_no].key_start.y, ALynxSetting.pad[pad_no].key_start.w, ALynxSetting.pad[pad_no].key_start.h);
        ALynxEmuProxy.AL_GL_NativeSetButton(4, ALynxSetting.pad[pad_no].key_opt1.x, ALynxSetting.pad[pad_no].key_opt1.y, ALynxSetting.pad[pad_no].key_opt1.w, ALynxSetting.pad[pad_no].key_opt1.h);
        ALynxEmuProxy.AL_GL_NativeSetButton(5, ALynxSetting.pad[pad_no].key_opt2.x, ALynxSetting.pad[pad_no].key_opt2.y, ALynxSetting.pad[pad_no].key_opt2.w, ALynxSetting.pad[pad_no].key_opt2.h);
    }
    
    private void LoadTextures(int no, String name) {
        Bitmap bitmap = ALynxUtils.getTextureFromBitmapResource(mContext, name);
        Buffer buffer = ByteBuffer.allocateDirect(bitmap.getWidth()*bitmap.getHeight()*4);
        bitmap.copyPixelsToBuffer(buffer);
        
        ALynxEmuProxy.AL_GL_NativeLoadTex(no, bitmap.getWidth(), bitmap.getHeight(), buffer, buffer.capacity());
        
	    bitmap.recycle();
    }
    
    public void Stop() {
    	setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        ALynxEmuProxy.AL_Emu_Stop();
        ALynxEmuProxy.AL_GL_NativeDeinit();
        ALynxEmuProxy.AudioStop();
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
