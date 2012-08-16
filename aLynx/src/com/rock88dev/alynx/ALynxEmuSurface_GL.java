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
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import com.rock88dev.alynx.ALynxSetting.Rect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ALynxEmuSurface_GL extends GLSurfaceView implements android.opengl.GLSurfaceView.Renderer {
	
	private Context mContext = null;
	private int[] textures = new int[7];
	private ALynxSetting set;
	
	static Bitmap ScreenBitmap = null, bitmap = null;
	static private ByteBuffer ScreenBuffer;
	
	long tick = 0, fps = 0;
	private int pad_no = 1;
	private int s_height;
	private int powervr = 0;
	private float opacity = 1.0f;
	private int size = 1;
	private Rect[] rect = new Rect[6];
	
	public ALynxEmuSurface_GL(Context context, ALynxSetting set, String rom) {
		super(context);
		mContext = context;
		this.set=set;
		this.set.loadSettings();
		powervr = this.set.powervr_fix;
		setOpacity(this.set.opacity);
		
		setEGLConfigChooser(8, 8, 8, 8, 0, 0); 
        getHolder().setFormat(PixelFormat.RGBA_8888);
		
        setRenderer(this);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        if(set.orientation_opt==0) pad_no=0;
        
        size=ALynxSetting.pad[pad_no].size;
        ALynxInput.Init(size, pad_no);
        
        
        int width = 160;//ALynxEmuProxy.AL_Image_Get_Width();
        int height = 102;//ALynxEmuProxy.AL_Image_Get_Height();
		ScreenBuffer=ByteBuffer.allocateDirect(width*height*2);
		//Log.d("ALYNX",width+"x"+height);
		ScreenBitmap=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		
		ALynxEmuProxy.AL_Set_Rom_Path(rom);
		ALynxEmuProxy.AL_Check_BIOS(set.bios_path);
		ALynxEmuProxy.AL_Emu_Set_Config(1, 0, set.fskip_opt, set.sound_opt, set.fps_limit);
		ALynxEmuProxy.AL_Emu_Run();
        
	}
	
    public void Stop() {
        ALynxEmuProxy.AL_Emu_Stop();
        ALynxEmuProxy.AudioStop();
    }
    
	public void onDrawFrame(GL10 gl) {
		draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		s_height = height;
		
		switch(size)
		{
			case 1:
	        {
	        	int dpadsize = 128;
	        	int buttonsize = 64;
	        	rect[0] = new Rect(ALynxSetting.pad[pad_no].dpad.x, s_height - dpadsize - ALynxSetting.pad[pad_no].dpad.y, dpadsize, dpadsize);
	        	rect[1] = new Rect(ALynxSetting.pad[pad_no].key_a.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_a.y, buttonsize, buttonsize);
	        	rect[2] = new Rect(ALynxSetting.pad[pad_no].key_b.x, s_height - ALynxSetting.pad[pad_no].key_b.h - ALynxSetting.pad[pad_no].key_b.y, buttonsize, buttonsize);
	        	rect[3] = new Rect(ALynxSetting.pad[pad_no].key_start.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_start.y, buttonsize, buttonsize);
	        	rect[4] = new Rect(ALynxSetting.pad[pad_no].key_opt1.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_opt1.y, buttonsize, buttonsize);
	        	rect[5] = new Rect(ALynxSetting.pad[pad_no].key_opt2.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_opt2.y, buttonsize, buttonsize);
	        	break;
	        }
			case 2:
	        {
	        	int dpadsize = 256;
	        	int buttonsize = 128;
	        	rect[0] = new Rect(ALynxSetting.pad[pad_no].dpad.x, s_height - dpadsize - ALynxSetting.pad[pad_no].dpad.y, dpadsize, dpadsize);
	        	rect[1] = new Rect(ALynxSetting.pad[pad_no].key_a.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_a.y, buttonsize, buttonsize);
	        	rect[2] = new Rect(ALynxSetting.pad[pad_no].key_b.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_b.y, buttonsize, buttonsize);
	        	rect[3] = new Rect(ALynxSetting.pad[pad_no].key_start.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_start.y, buttonsize, buttonsize);
	        	rect[4] = new Rect(ALynxSetting.pad[pad_no].key_opt1.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_opt1.y, buttonsize, buttonsize);
	        	rect[5] = new Rect(ALynxSetting.pad[pad_no].key_opt2.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_opt2.y, buttonsize, buttonsize);        	
	        	break;
	        }
			case 3: // 1.5x Size
	        {
	        	//Log.d("ALYNX","SIZE 3! (1.5x)");
	        	int dpadsize = 192;
	        	int buttonsize = 96;
	        	rect[0] = new Rect(ALynxSetting.pad[pad_no].dpad.x, s_height - dpadsize - ALynxSetting.pad[pad_no].dpad.y, dpadsize, dpadsize);
	        	rect[1] = new Rect(ALynxSetting.pad[pad_no].key_a.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_a.y, buttonsize, buttonsize);
	        	rect[2] = new Rect(ALynxSetting.pad[pad_no].key_b.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_b.y, buttonsize, buttonsize);
	        	rect[3] = new Rect(ALynxSetting.pad[pad_no].key_start.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_start.y, buttonsize, buttonsize);
	        	rect[4] = new Rect(ALynxSetting.pad[pad_no].key_opt1.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_opt1.y, buttonsize, buttonsize);
	        	rect[5] = new Rect(ALynxSetting.pad[pad_no].key_opt2.x, s_height - buttonsize - ALynxSetting.pad[pad_no].key_opt2.y, buttonsize, buttonsize);        	
	        	break;
	        }
		}
        gl.glViewport(0, 0, width, height);
        // setup projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 1.0f, 100.0f);
        gl.glDisable(GL10.GL_DEPTH_TEST);
		//if (first){
			LoadTextures(gl);
			//first=false;
		//}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
	}
    
    private void LoadTextures(GL10 gl){
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glGenTextures(7, textures, 0);
        
        LoadTextures(gl,0,"pad/dpad.png");
        LoadTextures(gl,1,"pad/button_a.png");
        LoadTextures(gl,2,"pad/button_b.png");
        LoadTextures(gl,3,"pad/button_start.png");
        LoadTextures(gl,4,"pad/button_opt1.png");
        LoadTextures(gl,5,"pad/button_opt2.png");
    }
    
    private void LoadTextures(GL10 gl, int no, String name) {
    	if(ALynxSetting.button_show[no] == 0) return; 
        Bitmap bitmap = ALynxUtils.getTextureFromBitmapResource(mContext, name);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[no]);
	    
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE );
	    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

	    int[] mCropWorkspace = {0,bitmap.getHeight()-1,bitmap.getWidth()-1,-bitmap.getHeight()+1};
	    
	    ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);
	    
	    bitmap.recycle();    	
    }
  
    private void createImage(GL10 gl){
    	ALynxEmuProxy.AL_Image_Get_Buffer(ScreenBuffer);
		ScreenBuffer.position(0);
		ScreenBitmap.copyPixelsFromBuffer(ScreenBuffer);
		
        gl.glEnable(GL10.GL_TEXTURE_2D);
        //gl.glGenTextures(1, textures, 0);
        
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[6]);
        
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	    gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE );
	    
	    if(powervr==0)
	    {
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, ScreenBitmap, 0);
	    	
	    	int[] mCropWorkspace = {0,ScreenBitmap.getHeight()-1,ScreenBitmap.getWidth()-1,-ScreenBitmap.getHeight()+1};
	    	((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);
	    }
	    else
	    {
	    	bitmap = Bitmap.createScaledBitmap(ScreenBitmap, 256, 128, false);
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    	bitmap.recycle();
	    	
	    	int[] mCropWorkspace = {0,bitmap.getHeight()-1,bitmap.getWidth()-1,-bitmap.getHeight()+1};
	    	((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);
	    }  
    }
	
    private void draw(GL10 gl)
	{
        gl.glClearColor(0, 0, 0, 1);
        gl.glClearDepthf(1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	    gl.glEnable(GL10.GL_TEXTURE_2D);
    	createImage(gl);
    	
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[6]); // s_height-rect[0].h -rect[0].y
	    ((GL11Ext) gl).glDrawTexfOES((float)(ALynxSetting.pad[pad_no].screen.x + 0.5), (float)(s_height - ALynxSetting.pad[pad_no].screen.h - ALynxSetting.pad[pad_no].screen.y + 0.5), 0, ALynxSetting.pad[pad_no].screen.w, ALynxSetting.pad[pad_no].screen.h);
	    
	    
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glColor4f(1.0f, 1.0f, 1.0f, opacity);//(float)(100-set.pad[pad_no].opacity)/100.0f);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
	    if(ALynxSetting.button_show[ALynxSetting.BUTTON_DPAD] == 1)
	    {
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
	    	((GL11Ext) gl).glDrawTexfOES((float)(rect[0].x + 0.5), (float)(rect[0].y + 0.5), 0, rect[0].w, rect[0].h);
	    }
	    
	    if(ALynxSetting.button_show[ALynxSetting.BUTTON_A] == 1)
	    {
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
	    	((GL11Ext) gl).glDrawTexfOES((float)(rect[1].x + 0.5), (float)(rect[1].y + 0.5), 0, rect[1].w, rect[1].h);
	    }
	    
	    if(ALynxSetting.button_show[ALynxSetting.BUTTON_A] == 1)
	    {
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
	    	((GL11Ext) gl).glDrawTexfOES((float)(rect[2].x + 0.5), (float)(rect[2].y + 0.5), 0, rect[2].w, rect[2].h);
	    }
	    
	    if(ALynxSetting.button_show[ALynxSetting.BUTTON_START] == 1)
	    {
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[3]);
	    	((GL11Ext) gl).glDrawTexfOES((float)(rect[3].x + 0.5), (float)(rect[3].y + 0.5), 0, rect[3].w, rect[3].h);
	    }
	    
	    if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT1] == 1)
	    {
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[4]);
	    	((GL11Ext) gl).glDrawTexfOES((float)(rect[4].x + 0.5), (float)(rect[4].y + 0.5), 0, rect[4].w, rect[4].h);
	    }
	    
	    if(ALynxSetting.button_show[ALynxSetting.BUTTON_OPT2] == 1)
	    {
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[5]);
	    	((GL11Ext) gl).glDrawTexfOES((float)(rect[5].x + 0.5), (float)(rect[5].y + 0.5), 0, rect[5].w, rect[5].h);
	    }
	    //gl.glFlush();
	    /*
	    fps++;
	    if ((System.currentTimeMillis() - tick) > 1000){
	    	Log.d("ALYNX","FPS: "+ALynxEmuProxy.AL_Emu_Get_FPS()+"/"+fps);
	    	fps=0;
	    	tick = System.currentTimeMillis();
	    }
	    */
	    
	}
    
	public void setOpacity(int i){
		switch(i)
		{
			case ALynxSetting.OPASITY_NONE:
				opacity = 1.0f;
				break;
			case ALynxSetting.OPASITY_LOW:
				opacity = 0.7f;
				break;
			case ALynxSetting.OPASITY_MEDIUM:
				opacity = 0.5f;
				break;
			case ALynxSetting.OPASITY_HIGHT:
				opacity = 0.25f;
				break;
			default:
				opacity = 1.0f;
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
