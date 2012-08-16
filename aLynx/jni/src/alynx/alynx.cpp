/*
	aLynx - Atari Lynx emulator for Android OS
	by rock88
	e-mail: rock88a@gmail.com
	web: http://rock88dev.blogspot.com
*/

#include <stdlib.h>
#include <cstdio>
#include <cstring>
#include <ctime>
#include <cctype>

#include "Utils.h"
#include "System.h"
#include "pixblend.h"
#include "ErrorHandler.h"
#include "alynx.h"
#include "video.h"
#include "audio.h"

#define HANDY_BASE_FPS 60

char *HandyBuffer;
char *mainSurface;

Uint32 *mpLynxBuffer;
CSystem *mpLynx;
int mFrameSkip = 0;

int LynxWidth;
int LynxHeight;
int LynxScale = 1;
int LynxLCD = 1;
int LynxRotate = MIKIE_NO_ROTATE;

Uint8 *delta;

int rendertype = 1;
int stype = 1;
int filter = 0;
int frskip = 0;
int frameskip = 0;
int Throttle = 1;  // Throttle to 60FPS

volatile int pause = 0;
volatile int emulation = 0;

float fps_counter;
Uint32 start_time;
Uint32 this_time;
	
int KeyMask = 0;
int native_fps = 0;

inline int Alynx_Update(void)
{
	if(gSystemCycleCount>gThrottleNextCycleCheckpoint)
	{
		static int limiter=0;
		static int flipflop=0;
		int overrun=gSystemCycleCount-gThrottleNextCycleCheckpoint;
		int nextstep=(((HANDY_SYSTEM_FREQ/HANDY_BASE_FPS)*gThrottleMaxPercentage)/100);

		if(gThrottleLastTimerCount==gTimerCount)
		{
			if(limiter<0) limiter=0; else limiter++;
			if(limiter>40 && mFrameSkip>0)
			{
				mFrameSkip--;
				limiter=0;
			}
			flipflop=1;
			return 0;
		}

		if(!flipflop)
		{
			if(limiter>0) limiter=0; else limiter--;
			if(limiter<-7 && mFrameSkip<10)
			{
				mFrameSkip++;
				limiter=0;
			}
		}

		flipflop=0;
		gThrottleNextCycleCheckpoint+=nextstep;
		gThrottleLastTimerCount=gTimerCount;

		if(overrun>nextstep) return 0;
	}
	return 1;

}

extern "C" void Alynx_Quit(void)
{
	emulation = -1;
	pause = 0;
}

extern "C" int Alynx_Init(char* rom, char* bios)
{
	StartTicks();
	
	mpLynx = new CSystem(rom, bios);

	if( !Video_Setup(LynxScale) ) return 0;
	
	if(gAudioEnabled)
	{
		if(Audio_Init())
		{
			gAudioEnabled = TRUE;
		}
	}
	
	Video_Init();

	start_time = GetTicks();
	KeyMask = mpLynx->GetButtonData();
	
	a_printf("Starting Lynx Emulation...\n");
	
	emulation=0;
	pause=0;
	
	while(!emulation)
	{	
		mpLynx->SetButtonData(KeyMask);

		// Update TimerCount
		gTimerCount++;

		while( Alynx_Update()  )
		{
			if(!gSystemHalt)
			{
				for(ULONG loop=1024;loop;loop--) mpLynx->Update();
			}
			else gTimerCount++;
		}
		
		if(gAudioEnabled) Audio_CallBack();
		
		this_time = GetTicks();

		fps_counter = (((float)gTimerCount/(this_time-start_time))*1000.0);

		if( (Throttle) && (fps_counter > 59.99) ) Delay( (Uint32)fps_counter );
		native_fps = static_cast<int>(fps_counter);

		while(pause==1)
		{
			Delay(20);
		}
	}
	
	free(mpLynxBuffer);
	free(HandyBuffer);
	free(mainSurface);
	start_render=0;
	delete mpLynx;
	
	a_printf("Stoping...\n");
	return 0;
}

extern "C" int Alynx_SaveState(char* path)
{
	return mpLynx->ContextSave(path);
}

extern "C" int Alynx_LoadState(char* path)
{
	int result = mpLynx->ContextLoad(path);
	fps_counter = gTimerCount = 0;
	this_time = GetTicks();
	return result;
}

extern "C" int Alynx_CheckDate()
{
	int result = 0;
	time_t seconds = time(NULL);
	tm* timeinfo = localtime(&seconds);
	if (timeinfo->tm_year == 112)
	{
		if (timeinfo->tm_mon == 3) result=1;
	}
	return result;
}
