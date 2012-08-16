/*
	aLynx - Atari Lynx emulator for Android OS
	by rock88
	e-mail: rock88a@gmail.com
	web: http://rock88dev.blogspot.com
*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <dirent.h>
#include <jni.h>
#include <pthread.h>

#include "Utils.h"
#include "alynx.h"
#include "video.h"
#include "com_rock88dev_alynx_ALynxEmuProxy.h"

#define B_UP		0
#define B_DOWN		1
#define B_LEFT		2
#define B_RIGHT		3
#define B_A			4
#define B_B			5
#define B_OPT1		6
#define B_OPT2		7
#define B_PAUSE		8

#define BUTTON_A		0x0001
#define BUTTON_B		0x0002
#define BUTTON_OPT2		0x0004
#define BUTTON_OPT1		0x0008
#define BUTTON_LEFT		0x0010
#define BUTTON_RIGHT	0x0020
#define BUTTON_UP		0x0040
#define BUTTON_DOWN		0x0080
#define BUTTON_PAUSE	0x0100

int keys[] = {BUTTON_UP,BUTTON_DOWN,BUTTON_LEFT,BUTTON_RIGHT,BUTTON_A,BUTTON_B,BUTTON_OPT1,BUTTON_OPT2,BUTTON_PAUSE};

typedef unsigned long ULONG;
extern ULONG gAudioEnabled;

static pthread_t __threadp = 0;
JavaVM *jniVM = NULL;
jobject ALynxEmuProxy=0;

char *rom;
char *bios;

const int screen_w = 160;
const int screen_h = 102;
int size = 0;
int emu_run = 0;

static void *ThreadProc(void* Param)
{
	Alynx_Init(rom, bios);
	pthread_exit(0);
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Emu_1Run(JNIEnv *env, jclass c)
{
	if (emu_run==0)
	{
		pthread_attr_t type;
	    pthread_attr_init(&type);
	    pthread_attr_setdetachstate(&type, PTHREAD_CREATE_JOINABLE);    
	    pthread_create (&__threadp, &type, ThreadProc, (void*)0);
		emu_run=1;
	}
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Emu_1Stop(JNIEnv *env, jclass c)
{
	Alynx_Quit();
	emu_run=0;
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Emu_1Set_1Config(JNIEnv *env, jclass c, jint scale, jint Jfilter , jint fskip, jint audio, jint limit)
{
	frskip=fskip;
	filter=Jfilter;
	Throttle = limit;
	gAudioEnabled=audio;
	
	if(filter!=0) LynxScale=2;
		else LynxScale=1;
	
	size=screen_w*LynxScale*screen_h*LynxScale*2;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    jniVM = vm;
	a_printf("JNI_OnLoad\n");
	
	JNIEnv *env;
	if ((*vm)->GetEnv(vm,(void**) &env, JNI_VERSION_1_2) != JNI_OK)
	{
		a_printf("Failed to get the environment using GetEnv()\n");
		return -1;
    }
	
	jclass cls = (*env)->FindClass(env,"com/rock88dev/alynx/ALynxEmuProxy");
    if(!cls) {
		a_printf("Failed to get ALynxEmuProxy class reference");
		return;
    }
	
	jmethodID constr = (*env)->GetMethodID(env,cls, "<init>", "()V");
		if(!constr)
		{
			a_printf("Failed to get ALynxEmuProxy constructor");
			return;
		}
		
	jobject obj = (*env)->NewObject(env,cls, constr);
	if(!obj)
	{
		a_printf("Failed to create a ALynxEmuProxy object");
		return;
	}
	
	ALynxEmuProxy = (*env)->NewGlobalRef(env,obj);
	
	return JNI_VERSION_1_2;
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Image_1Get_1Width(JNIEnv *env, jobject obj)
{
	return screen_w*LynxScale;
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Image_1Get_1Height(JNIEnv *env, jobject obj)
{
	return screen_h*LynxScale;
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Image_1Get_1Buffer(JNIEnv *env, jobject obj, jobject array)
{
	if(start_render)
	{
		char *scr = (char*)(*env)->GetDirectBufferAddress(env, array);
		memcpy(scr,mainSurface,size);
	}
	return 1;
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Set_1Rom_1Path(JNIEnv *env, jclass cl, jstring s)
{
	jboolean iscopy;
	rom = (*env)->GetStringUTFChars(env, s, &iscopy);
	return 1;
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Check_1BIOS(JNIEnv *env, jclass cl, jstring s)
{
	jboolean iscopy;
	unsigned char mRomData[0x200];
	unsigned char mRomCheck[16]={0x38,0x80,0x0A,0x90,0x04,0x8E,0x8B,0xFD,0x18,0xE8,0x8E,0x87,0xFD,0xA2,0x02,0x8E};
	
	//if(Alynx_checkDate() == 0) return 2;
	
	bios = (*env)->GetStringUTFChars(env, s, &iscopy);
	
	FILE *fp = fopen(bios,"rb");
	if (!fp) return 0;
	
	if(fread(mRomData,sizeof(char),0x200,fp)!=0x200) return 0;
	
	int loop;
	for(loop=0;loop<16;loop++)
	{
		if(mRomCheck[loop]!=mRomData[loop])
		{
			fclose(fp);
			return 0;
		}
	}
	
	fclose(fp);
	return 1;
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Key_1Down(JNIEnv *env, jclass c, jint key)
{
	KeyMask|=keys[key];
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Key_1Up(JNIEnv *env, jclass c, jint key)
{
	KeyMask&= ~keys[key];
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Emu_1Get_1FPS(JNIEnv *env, jobject c)
{
	return native_fps;
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Emu_1Pause(JNIEnv *env, jclass c)
{
	if(emulation==0) pause = 1;
}

JNIEXPORT void JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Emu_1Resume(JNIEnv *env, jclass c)
{
	pause = 0;
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Save_1State(JNIEnv *env, jclass c, jstring jpath)
{
	jboolean iscopy;
	char *path = (*env)->GetStringUTFChars(env, jpath, &iscopy);
	return Alynx_SaveState(path);
}

JNIEXPORT jint JNICALL Java_com_rock88dev_alynx_ALynxEmuProxy_AL_1Load_1State(JNIEnv *env, jclass c, jstring jpath)
{
	jboolean iscopy;
	char *path = (*env)->GetStringUTFChars(env, jpath, &iscopy);
	return Alynx_LoadState(path);
}
