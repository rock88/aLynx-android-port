/*
	aLynx - Atari Lynx emulator for Android OS
	by rock88
	e-mail: rock88a@gmail.com
	web: http://rock88dev.blogspot.com
*/

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

typedef unsigned char u8;
typedef unsigned short u16;
typedef unsigned int u32;

extern void InitLUTs(void);
extern void hq2x_16( uint16_t * sp, uint16_t * dp, int Xres, int Yres );

extern int Init_2xSaI();
extern void _2xSaI(u8 *srcPtr, u32 srcPitch, u8 *deltaPtr, u8 *dstPtr, u32 dstPitch, int width, int height);

int mainSurface_pitch, mainSurface_w, mainSurface_h, HandyBuffer_pitch, HandyBuffer_w, HandyBuffer_h;
int start_render = 0;

UBYTE *Video_CallBack(ULONG objref);

int Video_Setup(int scale)
{
	int surfacewidth;
	int surfaceheight;

	LynxWidth  = 160;
	LynxHeight = 102;
	surfacewidth  = LynxWidth * scale;
	surfaceheight = LynxHeight * scale;

	mainSurface_pitch = surfacewidth*2;
	mainSurface_w = surfacewidth;
	mainSurface_h = surfaceheight;
	a_printf("mainSurface: %dx%d - %d\n",mainSurface_w,mainSurface_h,filter);
	mainSurface = (char*)malloc(surfacewidth*surfaceheight*2);

	if (mainSurface == NULL)
	{
		a_printf("Could not allocate memory for mainSurface\n");
		return 0;
	}
	
	HandyBuffer_pitch = LynxWidth*2;
	HandyBuffer_w = LynxWidth;
	HandyBuffer_h = LynxHeight;
	HandyBuffer = (char*)malloc(LynxWidth*LynxHeight*2);

	if (HandyBuffer == NULL)
	{
		a_printf("Could not allocate memory for HandyBuffer\n");
		return 0;
	}

    delta = (Uint8*)malloc(LynxWidth*LynxHeight*sizeof(Uint32)*4);
    memset(delta, 255, LynxWidth*LynxHeight*sizeof(Uint32)*4);
	
	switch(filter)
	{
		case 1:
			InitLUTs();
			break;
		case 2:
			Init_2xSaI();
			break;
	}
	return 1;
}

void Video_Init()
{
	mpLynxBuffer = (Uint32 *)malloc(LynxWidth*LynxHeight*sizeof(Uint32)*4);
	mpLynx->DisplaySetAttributes( LynxRotate, MIKIE_PIXEL_FORMAT_16BPP_565, (ULONG)HandyBuffer_pitch, Video_CallBack, (ULONG)mpLynxBuffer);
}

UBYTE *Video_CallBack(ULONG objref)
{
	if(frameskip>=frskip)
	{
		Video_RenderBuffer();
		Video_DrawGraphics();
		frameskip=0;
		start_render=1;
	}
	frameskip++;
	return (UBYTE *)mpLynxBuffer;
}

inline void Video_DrawFilter(int filtertype, char *src, char *dst, Uint8 *delta)
{
	switch(filter)
	{
		case 0:
			break;
		case 1:
			hq2x_16( (uint16_t *)src, (uint16_t *)dst, 160, 102);
			break;
		case 2:
			_2xSaI((Uint8 *)src, HandyBuffer_pitch, delta, (Uint8 *)dst, mainSurface_pitch, HandyBuffer_w, HandyBuffer_h);
			break;
	}
}

inline void Video_DrawGraphics(void)
{
	if(filter >= 1)
	{
		Video_DrawFilter(filter, HandyBuffer, mainSurface, delta);
	}
	else
	{
		if (LynxScale == 1)
		{
			memcpy(mainSurface, HandyBuffer, LynxWidth * LynxHeight* 2);
		}
	}
}

void Video_RenderBuffer(void)
{
	memcpy(HandyBuffer, mpLynxBuffer, LynxWidth * LynxHeight * 2);
}


