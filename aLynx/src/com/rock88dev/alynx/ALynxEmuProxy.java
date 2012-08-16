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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class ALynxEmuProxy {
	
    static {
    	System.loadLibrary("alynx");
    }
    
	public native static int AL_Check_BIOS(String s);
	public native static int AL_Set_Rom_Path(String s);
	public native static int AL_Rotation_Get();
	
	public native static int AL_Emu_Run();
	public native static int AL_Emu_Stop();
	
	public native static void AL_Emu_Pause();
	public native static void AL_Emu_Resume();	
	
	public native static int AL_Image_Get_Width();
	public native static int AL_Image_Get_Height();
	public native static int AL_Image_Get_Buffer(Buffer buffer);
	
	public native static void AL_Key_Down(int key);
	public native static void AL_Key_Up(int key);
	
	public native static int AL_Save_State(String path);
	public native static int AL_Load_State(String path);
	
	public native static void AL_Emu_Set_Config(int scale, int filter, int frameskip, int audio, int limit);
	public native static int AL_Emu_Get_FPS();
	
	public native static void AL_GL_NativeInit();
	public native static void AL_GL_NativeDeinit();
	
	public native static void AL_GL_NativeRender();
	public native static void AL_GL_NativeSetConfig(int power_vr, int opacity, int size);
	public native static void AL_GL_NativeSetScreenRect(int x, int y, int w, int h);
	public native static void AL_GL_NativeSetRenderRect(int x, int y, int w, int h);
	public native static void AL_GL_NativeLoadTex(int no, int w, int h, Buffer buffer, int size);
	public native static void AL_GL_NativeSetButton(int no, int x, int y, int w, int h);
	
	ALynxEmuProxy(){
		
	}
	
    static AudioTrack Aud=null;
    public static int alynxInitAudio(int freq, int channels)
    {
    	int BuffSize = ALynxSetting.st_sample_size;
    	int i = AudioTrack.getMinBufferSize(freq, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT);
    	
    	Log.d("ALYNX","AudioTrack.getMinBufferSize = "+i);
    	
    	if (BuffSize<i) BuffSize=i;
    	Aud=new AudioTrack(AudioManager.STREAM_MUSIC,freq, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_8BIT,BuffSize,AudioTrack.MODE_STREAM);
		return BuffSize;
    }    
    
    public static void alynxWriteAudio(short[] audioData, int sizeInBytes)
    {
    	if(Aud!=null)
    	{
    		Aud.play();
    		Aud.write(audioData, 0, sizeInBytes/2);
    	}
    }
    
    public static void AudioStop()
    {
    	if(Aud!=null)
    	{
    		Aud.stop();
    	}
    }
}
