/*
	aLynx - Atari Lynx emulator for Android OS
	by rock88
	e-mail: rock88a@gmail.com
	web: http://rock88dev.blogspot.com
*/

#ifndef __ALYNX_H__
#define __ALYNX_H__

#ifdef __cplusplus
extern CSystem *mpLynx;
#endif

extern char *HandyBuffer;
extern char *mainSurface;

extern Uint32 *mpLynxBuffer;
extern int mFrameSkip;

extern int frskip;
extern int frameskip;
extern int LynxWidth;
extern int LynxHeight;
extern int LynxFormat;
extern int LynxRotate;
extern int LynxScale;
extern int LynxLCD;

extern int rendertype;
extern int stype;
extern int filter;
extern Uint32 overlay_format;
extern Uint8 *delta;

extern int Throttle;
extern int KeyMask;
extern int native_fps;
extern volatile int emulation;
extern volatile int pause;

#endif
