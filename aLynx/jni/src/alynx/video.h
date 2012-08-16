/*
	aLynx - Atari Lynx emulator for Android OS
	by rock88
	e-mail: rock88a@gmail.com
	web: http://rock88dev.blogspot.com
*/

#ifndef __VIDEO_H__
#define __VIDEO_H__

//typedef unsigned char UBYTE;
//typedef unsigned long ULONG;

extern int start_render;

int Video_Setup(int scale);
void Video_Init();
//UBYTE *Video_CallBack(ULONG objref);
inline void Video_DrawFilter(int filtertype, char *src, char *dst, Uint8 *delta);
inline void Video_DrawGraphics(void);
void Video_RenderBuffer(void);

#endif
