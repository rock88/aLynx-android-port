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

import com.rock88dev.alynx.ALynxSetting.Rect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class ALynxInput {
	public static Vibrator vibrator;
	public static int vibrator_enable = 0;
	public static final int ACTION_POINTER_INDEX_MASK  = 0xff00;
    public static final int ACTION_POINTER_INDEX_SHIFT = 8;
	public static int size = 1, pad_no = 1;
	
	private static final int 	PAD_UP=0,
								PAD_DOWN=1,
								PAD_LEFT=2,
								PAD_RIGHT=3,
								PAD_A=4,
								PAD_B=5,
								PAD_OPT1=6,
								PAD_OPT2=7,
								PAD_PAUSE=8,
								PAD_UP_LEFT=9,
								PAD_UP_RIGHT=10,
								PAD_DOWN_LEFT=11,
								PAD_DOWN_RIGHT=12;
						
	
	private static Rect[] rect = new Rect[13];
	
	private static int[] keys = {0,0,0,0,0,0,0,0,0,0,0};
	
	public static void Init(int size, int pad_no)
	{
		Log.d("ALYNX","size = "+size);
		switch(size)
		{
			case 1:
			{
				Log.d("ALYNX","size: 1");
				rect[PAD_UP] = new Rect(45,-1,85,38);
				rect[PAD_DOWN] = new Rect(45,89,85,126);
				rect[PAD_LEFT] = new Rect(0,44,38,83);
				rect[PAD_RIGHT] = new Rect(87,44,124,83);
				rect[PAD_A] = new Rect(ALynxSetting.pad[pad_no].key_a.x,ALynxSetting.pad[pad_no].key_a.y,ALynxSetting.pad[pad_no].key_a.x+ALynxSetting.pad[pad_no].key_a.w,ALynxSetting.pad[pad_no].key_a.y+ALynxSetting.pad[pad_no].key_a.h);
				rect[PAD_B] = new Rect(ALynxSetting.pad[pad_no].key_b.x,ALynxSetting.pad[pad_no].key_b.y,ALynxSetting.pad[pad_no].key_b.x+ALynxSetting.pad[pad_no].key_b.w,ALynxSetting.pad[pad_no].key_b.y+ALynxSetting.pad[pad_no].key_b.h);
				rect[PAD_OPT1] = new Rect(ALynxSetting.pad[pad_no].key_opt1.x,ALynxSetting.pad[pad_no].key_opt1.y,ALynxSetting.pad[pad_no].key_opt1.x+ALynxSetting.pad[pad_no].key_opt1.w,ALynxSetting.pad[pad_no].key_opt1.y+ALynxSetting.pad[pad_no].key_opt1.h);
				rect[PAD_OPT2] = new Rect(ALynxSetting.pad[pad_no].key_opt2.x,ALynxSetting.pad[pad_no].key_opt2.y,ALynxSetting.pad[pad_no].key_opt2.x+ALynxSetting.pad[pad_no].key_opt2.w,ALynxSetting.pad[pad_no].key_opt2.y+ALynxSetting.pad[pad_no].key_opt2.h);
				rect[PAD_PAUSE] = new Rect(ALynxSetting.pad[pad_no].key_start.x,ALynxSetting.pad[pad_no].key_start.y,ALynxSetting.pad[pad_no].key_start.x+ALynxSetting.pad[pad_no].key_start.w,ALynxSetting.pad[pad_no].key_start.y+ALynxSetting.pad[pad_no].key_start.h);
				rect[PAD_UP_LEFT] = new Rect(2,1,36,38);
				rect[PAD_UP_RIGHT] = new Rect(91,0,123,39);
				rect[PAD_DOWN_LEFT] = new Rect(5,92,35,125);
				rect[PAD_DOWN_RIGHT] = new Rect(91,91,119,124);
				break;
			}
			case 2:
			{
				Log.d("ALYNX","size: 2");
				rect[PAD_UP] = new Rect(85,2,170,79);
				rect[PAD_DOWN] = new Rect(87,161,168,235);
				rect[PAD_LEFT] = new Rect(3,86,85,159);
				rect[PAD_RIGHT] = new Rect(169,84,245,164);
				rect[PAD_A] = new Rect(ALynxSetting.pad[pad_no].key_a.x,ALynxSetting.pad[pad_no].key_a.y,ALynxSetting.pad[pad_no].key_a.x+ALynxSetting.pad[pad_no].key_a.w,ALynxSetting.pad[pad_no].key_a.y+ALynxSetting.pad[pad_no].key_a.h);
				rect[PAD_B] = new Rect(ALynxSetting.pad[pad_no].key_b.x,ALynxSetting.pad[pad_no].key_b.y,ALynxSetting.pad[pad_no].key_b.x+ALynxSetting.pad[pad_no].key_b.w,ALynxSetting.pad[pad_no].key_b.y+ALynxSetting.pad[pad_no].key_b.h);
				rect[PAD_OPT1] = new Rect(ALynxSetting.pad[pad_no].key_opt1.x,ALynxSetting.pad[pad_no].key_opt1.y,ALynxSetting.pad[pad_no].key_opt1.x+ALynxSetting.pad[pad_no].key_opt1.w,ALynxSetting.pad[pad_no].key_opt1.y+ALynxSetting.pad[pad_no].key_opt1.h);
				rect[PAD_OPT2] = new Rect(ALynxSetting.pad[pad_no].key_opt2.x,ALynxSetting.pad[pad_no].key_opt2.y,ALynxSetting.pad[pad_no].key_opt2.x+ALynxSetting.pad[pad_no].key_opt2.w,ALynxSetting.pad[pad_no].key_opt2.y+ALynxSetting.pad[pad_no].key_opt2.h);
				rect[PAD_PAUSE] = new Rect(ALynxSetting.pad[pad_no].key_start.x,ALynxSetting.pad[pad_no].key_start.y,ALynxSetting.pad[pad_no].key_start.x+ALynxSetting.pad[pad_no].key_start.w,ALynxSetting.pad[pad_no].key_start.y+ALynxSetting.pad[pad_no].key_start.h);		
				rect[PAD_UP_LEFT] = new Rect(8,6,75,78);
				rect[PAD_UP_RIGHT] = new Rect(179,7,246,74);
				rect[PAD_DOWN_LEFT] = new Rect(4,182,73,248);
				rect[PAD_DOWN_RIGHT] = new Rect(180,180,234,244);
				break;
			}
			case 3: // 1.5x Size
			{
				Log.d("ALYNX","size: 3");
				rect[PAD_UP] = new Rect(64,2,123,60);
				rect[PAD_DOWN] = new Rect(64,122,126,188);
				rect[PAD_LEFT] = new Rect(2,67,63,119);
				rect[PAD_RIGHT] = new Rect(127,66,184,121);
				rect[PAD_A] = new Rect(ALynxSetting.pad[pad_no].key_a.x,ALynxSetting.pad[pad_no].key_a.y,ALynxSetting.pad[pad_no].key_a.x+ALynxSetting.pad[pad_no].key_a.w,ALynxSetting.pad[pad_no].key_a.y+ALynxSetting.pad[pad_no].key_a.h);
				rect[PAD_B] = new Rect(ALynxSetting.pad[pad_no].key_b.x,ALynxSetting.pad[pad_no].key_b.y,ALynxSetting.pad[pad_no].key_b.x+ALynxSetting.pad[pad_no].key_b.w,ALynxSetting.pad[pad_no].key_b.y+ALynxSetting.pad[pad_no].key_b.h);
				rect[PAD_OPT1] = new Rect(ALynxSetting.pad[pad_no].key_opt1.x,ALynxSetting.pad[pad_no].key_opt1.y,ALynxSetting.pad[pad_no].key_opt1.x+ALynxSetting.pad[pad_no].key_opt1.w,ALynxSetting.pad[pad_no].key_opt1.y+ALynxSetting.pad[pad_no].key_opt1.h);
				rect[PAD_OPT2] = new Rect(ALynxSetting.pad[pad_no].key_opt2.x,ALynxSetting.pad[pad_no].key_opt2.y,ALynxSetting.pad[pad_no].key_opt2.x+ALynxSetting.pad[pad_no].key_opt2.w,ALynxSetting.pad[pad_no].key_opt2.y+ALynxSetting.pad[pad_no].key_opt2.h);
				rect[PAD_PAUSE] = new Rect(ALynxSetting.pad[pad_no].key_start.x,ALynxSetting.pad[pad_no].key_start.y,ALynxSetting.pad[pad_no].key_start.x+ALynxSetting.pad[pad_no].key_start.w,ALynxSetting.pad[pad_no].key_start.y+ALynxSetting.pad[pad_no].key_start.h);			
				rect[PAD_UP_LEFT] = new Rect(4,4,60,61);
				rect[PAD_UP_RIGHT] = new Rect(130,6,186,61);
				rect[PAD_DOWN_LEFT] = new Rect(8,131,60,185);
				rect[PAD_DOWN_RIGHT] = new Rect(130,132,178,184);
				break;
			}
		}
	}
	
	public static boolean KeyDown(int keyCode, KeyEvent event)
	{
		boolean result = false;
		if(keyCode==KeyEvent.KEYCODE_MENU) return false;
		if(keyCode==KeyEvent.KEYCODE_BACK) result=true;
		if(ALynxSetting.st_hard_key[0]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_UP);
			result=true;
		}
		if(ALynxSetting.st_hard_key[1]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_DOWN);
			result=true;
		}
		if(ALynxSetting.st_hard_key[2]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_LEFT);
			result=true;
		}
		if(ALynxSetting.st_hard_key[3]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_RIGHT);
			result=true;
		}
		if(ALynxSetting.st_hard_key[4]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_A);
			result=true;
		}
		if(ALynxSetting.st_hard_key[5]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_B);
			result=true;
		}
		if(ALynxSetting.st_hard_key[6]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_PAUSE);
			result=true;
		}
		if(ALynxSetting.st_hard_key[7]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_OPT1);
			result=true;
		}
		if(ALynxSetting.st_hard_key[8]==keyCode){
			ALynxEmuProxy.AL_Key_Down(PAD_OPT2);
			result=true;
		}
		return result;
	}
	
	public static boolean KeyUp(int keyCode, KeyEvent event)
	{
		boolean result = false;
		if(keyCode==KeyEvent.KEYCODE_MENU) return false;
		if(keyCode==KeyEvent.KEYCODE_BACK) result=true;
		if(ALynxSetting.st_hard_key[0]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_UP);
			result=true;
		}
		if(ALynxSetting.st_hard_key[1]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_DOWN);
			result=true;
		}
		if(ALynxSetting.st_hard_key[2]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_LEFT);
			result=true;
		}
		if(ALynxSetting.st_hard_key[3]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_RIGHT);
			result=true;
		}
		if(ALynxSetting.st_hard_key[4]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_A);
			result=true;
		}
		if(ALynxSetting.st_hard_key[5]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_B);
			result=true;
		}
		if(ALynxSetting.st_hard_key[6]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_PAUSE);
			result=true;
		}
		if(ALynxSetting.st_hard_key[7]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_OPT1);
			result=true;
		}
		if(ALynxSetting.st_hard_key[8]==keyCode){
			ALynxEmuProxy.AL_Key_Up(PAD_OPT2);
			result=true;
		}
		return result;
	}
	
	private static int getButtonFromXY(int pad_no, int x, int y){
		if((rect[PAD_PAUSE].x<=x && x<=rect[PAD_PAUSE].w) && (rect[PAD_PAUSE].y<=y && y<rect[PAD_PAUSE].h))
		{
			return PAD_PAUSE;
		}
		if((rect[PAD_A].x<=x && x<=rect[PAD_A].w) && (rect[PAD_A].y<=y && y<rect[PAD_A].h))
		{
			return PAD_A;
		}
		if((rect[PAD_B].x<=x && x<=rect[PAD_B].w) && (rect[PAD_B].y<=y && y<rect[PAD_B].h))
		{
			return PAD_B;
		}
		if((rect[PAD_OPT1].x<=x && x<=rect[PAD_OPT1].w) && (rect[PAD_OPT1].y<=y && y<rect[PAD_OPT1].h))
		{
			return PAD_OPT1;
		}
		if((rect[PAD_OPT2].x<=x && x<=rect[PAD_OPT2].w) && (rect[PAD_OPT2].y<=y && y<rect[PAD_OPT2].h))
		{
			return PAD_OPT2;
		}
		
		int xp = x-ALynxSetting.pad[pad_no].dpad.x;
		int yp = y-ALynxSetting.pad[pad_no].dpad.y;
		
		//Log.d("ALYNX",(x-ALynxSetting.pad[pad_no].dpad.x)+":"+(y-ALynxSetting.pad[pad_no].dpad.y));
		//Log.d("ALYNX",rect[PAD_UP].x+":"+rect[PAD_UP].w+":"+rect[PAD_UP].y+":"+rect[PAD_UP].h);
		
		//Log.d("ALYNX",xp+":"+yp);
		
		if((rect[PAD_UP].x<=xp && xp<=rect[PAD_UP].w) && (rect[PAD_UP].y<=yp && yp<rect[PAD_UP].h))
		{
			return PAD_UP;
		}
		if((rect[PAD_DOWN].x<=xp && xp<=rect[PAD_DOWN].w) && (rect[PAD_DOWN].y<=yp && yp<rect[PAD_DOWN].h))
		{
			return PAD_DOWN;
		}
		if((rect[PAD_LEFT].x<=xp && xp<=rect[PAD_LEFT].w) && (rect[PAD_LEFT].y<=yp && yp<rect[PAD_LEFT].h))
		{
			return PAD_LEFT;
		}
		if((rect[PAD_RIGHT].x<=xp && xp<=rect[PAD_RIGHT].w) && (rect[PAD_RIGHT].y<=yp && yp<rect[PAD_RIGHT].h))
		{
			return PAD_RIGHT;
		}
		
		if((rect[PAD_UP_LEFT].x<=xp && xp<=rect[PAD_UP_LEFT].w) && (rect[PAD_UP_LEFT].y<=yp && yp<rect[PAD_UP_LEFT].h))
		{
			return PAD_UP_LEFT;
		}
		if((rect[PAD_UP_RIGHT].x<=xp && xp<=rect[PAD_UP_RIGHT].w) && (rect[PAD_UP_RIGHT].y<=yp && yp<rect[PAD_UP_RIGHT].h))
		{
			return PAD_UP_RIGHT;
		}
		if((rect[PAD_DOWN_LEFT].x<=xp && xp<=rect[PAD_DOWN_LEFT].w) && (rect[PAD_DOWN_LEFT].y<=yp && yp<rect[PAD_DOWN_LEFT].h))
		{
			return PAD_DOWN_LEFT;
		}
		if((rect[PAD_DOWN_RIGHT].x<=xp && xp<=rect[PAD_DOWN_RIGHT].w) && (rect[PAD_DOWN_RIGHT].y<=yp && yp<rect[PAD_DOWN_RIGHT].h))
		{
			return PAD_DOWN_RIGHT;
		}

		return -1;
	}
	
	public static boolean onSingleTouchEvent(int pad_no, MotionEvent event)
	{
		int x = (int)event.getX();
		int y = (int)event.getY();
	
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
			{
				int button = getButtonFromXY(pad_no, x,y);
				if (button!=-1) ALynxEmuProxy.AL_Key_Down(button);
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				int button = getButtonFromXY(pad_no, x,y);
				if (button!=-1) ALynxEmuProxy.AL_Key_Up(button);
				break;
			}
		}
		return true;
	}
	
	public static boolean onMultiTouchEvent(int pad_no, MotionEvent event)
	{
	    int ptrId = -1;
	    int action = event.getAction();
	    switch (action & MotionEvent.ACTION_MASK)
	    {
	        case MotionEvent.ACTION_DOWN:
	        {
	            down(pad_no, event.getPointerId(0), (int)event.getX(), (int)event.getY());
	            break;
	        }
	        case MotionEvent.ACTION_UP:
	        {
	            up(pad_no, event.getPointerId(0));
	            break;
	        }
	        case MotionEvent.ACTION_POINTER_DOWN:
	        {
	            ptrId = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
	            //int ptrIdx = event.findPointerIndex(ptrId);
	            int ptrIdx = event.getPointerId(ptrId);
	            if (ptrIdx < 0) return false;
	            down(pad_no, ptrId, (int)event.getX(ptrIdx), (int)event.getY(ptrIdx));
	            break;
	        }
	        case MotionEvent.ACTION_POINTER_UP:
	        {
	            ptrId = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
	            up(pad_no, ptrId);
	            break;
	        }
	        case MotionEvent.ACTION_MOVE:
	        {
	            ptrId = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
				//int ptrIdx = event.findPointerIndex(ptrId);
				int ptrIdx = event.getPointerId(ptrId);
				if (ptrIdx < 0) return false;
				//Log.d("ALYNX","ptrIdx = "+ptrIdx+" event.getPointerCount() = "+event.getPointerCount());
				if (ptrIdx < event.getPointerCount())
				{
					move(pad_no, ptrId, (int)event.getX(ptrIdx), (int)event.getY(ptrIdx));
				}
	        	break;
	        }

	    }
	    return true;
	}
	
	private static void down(int pad_no, int pointerId, int x, int y)
	{
		int button = getButtonFromXY(pad_no, x, y);
		if (button!=-1)
		{
			//Log.d("ALYNX","button = "+button);
			keys[pointerId] = button;
			if(button<9)
			{
				ALynxEmuProxy.AL_Key_Down(button);
			}
			else
			{
				switch(button)
				{
					case PAD_UP_LEFT:
						ALynxEmuProxy.AL_Key_Down(PAD_UP);
						ALynxEmuProxy.AL_Key_Down(PAD_LEFT);
						break;
					case PAD_UP_RIGHT:
						ALynxEmuProxy.AL_Key_Down(PAD_UP);
						ALynxEmuProxy.AL_Key_Down(PAD_RIGHT);
						break;
					case PAD_DOWN_LEFT:
						ALynxEmuProxy.AL_Key_Down(PAD_DOWN);
						ALynxEmuProxy.AL_Key_Down(PAD_LEFT);
						break;
					case PAD_DOWN_RIGHT:
						ALynxEmuProxy.AL_Key_Down(PAD_DOWN);
						ALynxEmuProxy.AL_Key_Down(PAD_RIGHT);
						break;
				}
			}
			//Log.d("ALYNX","DOWN: pointerId = "+pointerId);
			if(vibrator_enable==1)
			{
				vibrator.vibrate(25);
				//Log.d("ALYNX","VJJVJVJVJVJVJVJVJVV");
			}
		}
	}
	
	private static void up(int pad_no, int pointerId)
	{
		if(keys[pointerId]<9)
		{
			ALynxEmuProxy.AL_Key_Up(keys[pointerId]);
		}
		else
		{
			switch(keys[pointerId])
			{
				case PAD_UP_LEFT:
					ALynxEmuProxy.AL_Key_Up(PAD_UP);
					ALynxEmuProxy.AL_Key_Up(PAD_LEFT);
					break;
				case PAD_UP_RIGHT:
					ALynxEmuProxy.AL_Key_Up(PAD_UP);
					ALynxEmuProxy.AL_Key_Up(PAD_RIGHT);
					break;
				case PAD_DOWN_LEFT:
					ALynxEmuProxy.AL_Key_Up(PAD_DOWN);
					ALynxEmuProxy.AL_Key_Up(PAD_LEFT);
					break;
				case PAD_DOWN_RIGHT:
					ALynxEmuProxy.AL_Key_Up(PAD_DOWN);
					ALynxEmuProxy.AL_Key_Up(PAD_RIGHT);
					break;
			}			
		}
		//Log.d("ALYNX","UP: pointerId = "+pointerId);
	}
	
	private static void move(int pad_no, int pointerId, int x, int y)
	{
		int button = getButtonFromXY(pad_no, x, y);
		if((button!=-1)&&((button<4)||(button>8)))
		{
			//Log.d("ALYNX","button = "+button);
			
			ALynxEmuProxy.AL_Key_Up(PAD_UP);
			ALynxEmuProxy.AL_Key_Up(PAD_DOWN);
			ALynxEmuProxy.AL_Key_Up(PAD_LEFT);
			ALynxEmuProxy.AL_Key_Up(PAD_RIGHT);
			
			keys[pointerId] = button;
			
			if(button<9)
			{
				ALynxEmuProxy.AL_Key_Down(button);
			}
			else
			{
				switch(button)
				{
					case PAD_UP_LEFT:
						ALynxEmuProxy.AL_Key_Down(PAD_UP);
						ALynxEmuProxy.AL_Key_Down(PAD_LEFT);
						break;
					case PAD_UP_RIGHT:
						ALynxEmuProxy.AL_Key_Down(PAD_UP);
						ALynxEmuProxy.AL_Key_Down(PAD_RIGHT);
						break;
					case PAD_DOWN_LEFT:
						ALynxEmuProxy.AL_Key_Down(PAD_DOWN);
						ALynxEmuProxy.AL_Key_Down(PAD_LEFT);
						break;
					case PAD_DOWN_RIGHT:
						ALynxEmuProxy.AL_Key_Down(PAD_DOWN);
						ALynxEmuProxy.AL_Key_Down(PAD_RIGHT);
						break;
				}
			}
		}
		else
		{
			ALynxEmuProxy.AL_Key_Up(PAD_UP);
			ALynxEmuProxy.AL_Key_Up(PAD_DOWN);
			ALynxEmuProxy.AL_Key_Up(PAD_LEFT);
			ALynxEmuProxy.AL_Key_Up(PAD_RIGHT);
		}
	}
}
