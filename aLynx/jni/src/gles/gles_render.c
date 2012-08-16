/*
	aLynx - Atari Lynx emulator for Android OS
	by rock88
	e-mail: rock88a@gmail.com
	web: http://rock88dev.blogspot.com
*/

#include "com_rock88dev_alynx_ALynxEmuProxy.h"
#include "Utils.h"
#include "gles_render.h"
#include "importgl.h"
#include "font.h"

extern char *mainSurface;

static int screen_x = 0;
static int screen_y = 0;
static int screen_w = 160;
static int screen_h = 102;

static GLuint s_texture[8];

int s_w = 0;
int s_h = 0;

char* button[6];

int button_x[6];
int button_y[6];
int button_w[6];
int button_h[6];
int button_show[] = {0,0,0,0,0,0};
int opacity = 0x10000;
static int size = 1;

#define SHOW_FPS 0
#define FPS_WIDTH 64
#define FPS_HEIGHT 8

Uint16 fps_text[FPS_WIDTH*FPS_HEIGHT];
char text[20];
extern int native_fps;
int fps_update = 200;

int TEXTURE_WIDTH = 160;
int TEXTURE_HEIGHT = 102;

int pwr_vr = 0;
char *pwr_vr_tex;
int offset=0;

void DisplayChar(Uint16* Screen, char c);
void DisplayString(const char *string);
unsigned short*  rgba8888_to_rgba4444(void* src, int cb);

static void check_gl_error(const char* op)
{
	GLint error;
	for (error = glGetError(); error; error = glGetError())
		a_printf("after %s() glError (0x%x)\n", op, error);
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeInit(JNIEnv *env, jclass c)
{
	importGLInit();
	memset(button_show,0x00,sizeof(button_show));
	glEnable(GL_TEXTURE_2D);
	glGenTextures(8, s_texture);
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeDeinit(JNIEnv *env, jclass c)
{
	glDeleteTextures(8, s_texture);
	importGLDeinit();
	
	if(pwr_vr==1)
	{
		free(pwr_vr_tex);
	}
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeRender(JNIEnv *env, jclass c)
{
	//glClearColorx(0x10000, 0x10000, 0x10000, 0x10000);
	glDisable(GL_BLEND);
	glClear(GL_COLOR_BUFFER_BIT);
	glBindTexture(GL_TEXTURE_2D, s_texture[0]);
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	if(pwr_vr==1)
	{
		// fast 8byte copy
		long long *src = (long long*)mainSurface;
		long long *dst = (long long*)pwr_vr_tex;
		int i=0;
		while(i<102)
		{
			*(dst++) = *(src++);
			offset+=8;
			if(offset==320)
			{
				dst+=24;
				offset=0;
				i++;
			}
		}
		//a_printf("NativeRender: %i %i",screen_w, screen_h);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, pwr_vr_tex);
		glDrawTexiOES(screen_x, s_h-screen_h-screen_y, 0, screen_w, screen_h);	
	}
	else
	{
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, mainSurface);
		glDrawTexiOES(screen_x, s_h-screen_h-screen_y, 0, screen_w, screen_h);	
	}

#if 0
	glBindTexture(GL_TEXTURE_2D, s_texture[7]);
	glDrawTexiOES(0, s_h-FPS_HEIGHT, 0, FPS_WIDTH, FPS_HEIGHT);
#endif	
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	//glBlendFunc(GL_ONE,GL_ONE_MINUS_SRC_ALPHA);
	glColor4x(0x10000, 0x10000, 0x10000, opacity);
	
	int i=0;
	
	for(i=0; i<6; i++)
	{
		if(button_show[i]==1)
		{
			glBindTexture(GL_TEXTURE_2D, s_texture[i+1]);
			glDrawTexiOES(button_x[i], s_h-button_h[i]-button_y[i], 0, button_w[i], button_h[i]);
		}
	}
#if 0
	fps_update++;
	if(fps_update>60)
	{
		fps_update=0;
		sprintf(text,"FPS: %i",native_fps);
		memset(fps_text,0x00,sizeof(fps_text));
		DisplayString(&text);
		glBindTexture(GL_TEXTURE_2D, s_texture[7]);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, FPS_WIDTH, FPS_HEIGHT, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, fps_text);
	}
#endif
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeSetRenderRect(JNIEnv *env, jclass c, jint x, jint y, jint w, jint h)
{
	a_printf("native_gl_resize %d %d", w, h);
	//a_printf("sizeof(long long) = %i", sizeof(long long));
	if(pwr_vr==1)
	{
		a_printf("PowerVR On");
		TEXTURE_WIDTH = 256;
		TEXTURE_HEIGHT = 128;
		pwr_vr_tex = (char*)malloc(TEXTURE_WIDTH*TEXTURE_HEIGHT*2);
		memset(pwr_vr_tex,0x00,TEXTURE_WIDTH*TEXTURE_HEIGHT*2);
		float w1 = (float)screen_w/160;
		float h1 = (float)screen_h/102;
		w1*=TEXTURE_WIDTH;
		h1*=TEXTURE_HEIGHT;
		screen_w=(int)w1;
		screen_h=(int)h1;
	}
	else
	{
		a_printf("PowerVR Off");
		TEXTURE_WIDTH = 160;
		TEXTURE_HEIGHT = 102;
		//screen_w = TEXTURE_WIDTH;
		//screen_h = TEXTURE_HEIGHT;
	}
	
	//glEnable(GL_TEXTURE_2D);
	//glGenTextures(8, s_texture);
	glBindTexture(GL_TEXTURE_2D, s_texture[0]);
	//glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	//glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
	
	//glShadeModel(GL_FLAT);
	//check_gl_error("glShadeModel");
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	check_gl_error("glColor4x");
	int rect[4] = {0, TEXTURE_HEIGHT-1, TEXTURE_WIDTH-1, -TEXTURE_HEIGHT+1};
	glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, rect);
	check_gl_error("glTexParameteriv");
	s_w = w;
	s_h = h;
	
#if SHOW_FPS
	glBindTexture(GL_TEXTURE_2D, s_texture[7]);
	glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, FPS_WIDTH, FPS_HEIGHT, 0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, fps_text);
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
	
	int rect2[4] = {0, FPS_HEIGHT, FPS_WIDTH, -FPS_HEIGHT};
	glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, rect2);
#endif
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeSetScreenRect(JNIEnv *env, jclass c, jint x, jint y, jint w, jint h)
{
	screen_x = x;
	screen_y = y;
	screen_w = w;
	screen_h = h;
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeLoadTex(JNIEnv *env , jclass c, jint no, jint w, jint h, jobject array, jint size)
{
	//a_printf("NativeLoadTex: no=%i w=%i h=%i w*h=%i size=%i\n",no,w,h,w*h,size);
	button_show[no] = 1;
	char *scr = (char*)(*env)->GetDirectBufferAddress(env, array);
#if 0
	button[no] = malloc(size/2);
	void* mem = rgba8888_to_rgba4444(scr,size);
	memcpy(button[no], mem, size/2);
#else
	button[no] = rgba8888_to_rgba4444(scr,size);
#endif	
	glEnable(GL_TEXTURE_2D);
	
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glEnable(GL_BLEND);
	
	glBindTexture(GL_TEXTURE_2D, s_texture[no+1]);
	
	glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	/*glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);*/
	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE );
	
	//glShadeModel(GL_FLAT);
	//glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
	
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4, button[no]);
			
	int rect[4] = {0, h, w, -h};
	glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, rect);
	//free(mem);
	free(button[no]);
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeSetButton(JNIEnv *env, jclass c, jint no, jint x, jint y, jint w, jint h)
{
	int dpadsize = 128;
	int buttonsize = 64;
	switch(size)
	{
		case 2:
			dpadsize = 256;
			buttonsize = 128;
			break;
		case 3: // 1.5x Size
			dpadsize = 192;
			buttonsize = 96;			
			break;
	}
	button_x[no] = x;
	button_y[no] = y;
	if (no == 0) button_w[no] = button_h[no] = dpadsize;
		else button_w[no] = button_h[no] = buttonsize;
	//button_w[no] = w*size;
	//button_h[no] = h*size;
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1GL_1NativeSetConfig(JNIEnv *env, jclass c, jint vr, jint jopacity, jint jsize)
{
	pwr_vr = vr;
	size = jsize;
	
	switch(jopacity)
	{
		case 0:
			opacity = 0x10000;
			break;
		case 1:
			opacity = 0x9500;
			break;
		case 2:
			opacity = 0x8000;
			break;
		case 3:
			opacity = 0x7000;
			break;
	}
}

unsigned short*  rgba8888_to_rgba4444(void* src, int cb)
{
	int i, cpel = cb/4;
	unsigned long* psrc = (unsigned long*)src;
	unsigned short* pdst = (unsigned short*)malloc(cpel*2);

	for(i=0;i<cpel; i++)
	{
		unsigned pel = psrc[i];

		unsigned r = pel & 0xff;
		unsigned g = (pel >> 8) & 0xff;
		unsigned b = (pel >> 16) & 0xff; 
		unsigned a = (pel >> 24) & 0xff;

		pdst[i] = ((r >> 4) << 12) | ((g>>4) << 8) | ((b>>4) << 4) | ((a>>4));
		
	}
	return pdst;
} 

void DisplayChar(Uint16 *Screen, char c)
{
    int line = (((c & 0x7f) - 32) >> 4) * font_height;
    int offset = (((c & 0x7f) - 32) & 15) * font_width;
	int PPL = FPS_WIDTH;
	int h, w;
	Uint16 *s = Screen;
	for (h = 0; h < font_height; h++, line++, s += PPL - font_width)
	{
		for (w = 0; w < font_width; w++, s++)
		{
			Uint8 p = font [line][offset + w];
			
			if (p == '#') *s=0xFFFF;
				else
					if (p == '.') *s = 0x0000;
		}
	}
}

void DisplayString(const char *string)
{
	int len = strlen(string);
	int i;
	Uint16 *s = &fps_text;
	
	for (i = 0; i < len; i++)
	{
		if (string [i] < 32) continue;
		DisplayChar(s, string [i]);
		s += (font_width - 0)*1;
	}
}
