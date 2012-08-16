/*
	aLynx - Atari Lynx emulator for Android OS
	by rock88
	e-mail: rock88a@gmail.com
	web: http://rock88dev.blogspot.com
*/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <jni.h>
#include "Utils.h"

#define HANDY_AUDIO_BUFFER_SIZE	4096

static JNIEnv *env;
extern JavaVM *jniVM;
extern jobject ALynxEmuProxy;

typedef unsigned char UBYTE;
typedef unsigned long ULONG;

extern ULONG	gAudioBufferPointer;
extern ULONG	gSystemHalt;
extern UBYTE	gAudioBuffer[HANDY_AUDIO_BUFFER_SIZE];

int channels = 1;
int freq = 22050;
int mixlen = 0;
int len = 1024;

void Audio_CallBack(void)
{
	if( ( (int)gAudioBufferPointer >= len) && (gAudioBufferPointer != 0) && (!gSystemHalt) )
	{
		jclass cls = 0;
	    jmethodID midNotify = 0;
	    int status;
	    JNIEnv *env;
	    int isAttached = 0;
	    int ret=0;
	    jshortArray Buffer=0;
   
		status = (*jniVM)->GetEnv(jniVM,(void **) &env, JNI_VERSION_1_4);
		if(status < 0) 
		{
			status = (*jniVM)->AttachCurrentThread(jniVM,&env, NULL);
			if(status < 0) 
			{
				a_printf("Audio_CallBack() failed to attach current thread");
				return;
			}
			isAttached = 1;
		}

		if(cls==0)
		{
			cls=(*env)->GetObjectClass(env,ALynxEmuProxy);   
			if(cls==0)
			{
				a_printf("Can't find class ALynxEmuProxy");
				if(isAttached) (*jniVM)->DetachCurrentThread(jniVM);
				return;
			}
		}

		if(midNotify==0)
		{
			midNotify=(*env)->GetStaticMethodID(env,cls,"alynxWriteAudio","([SI)V");
			if(midNotify==0)
			{
				a_printf("Can't find alynxWriteAudio in ALynxEmuProxy");
				if(isAttached) (*jniVM)->DetachCurrentThread(jniVM);
				return;
			}
		}

		Buffer = (*env)->NewShortArray(env, mixlen/2);
		(*env)->SetShortArrayRegion(env, Buffer, 0, mixlen/2, (const jshort*)gAudioBuffer);
		(*env)->CallStaticVoidMethod(env,cls,midNotify,Buffer,(jint)mixlen,NULL);

		if(isAttached) (*jniVM)->DetachCurrentThread(jniVM);
		memmove(gAudioBuffer, gAudioBuffer+len, gAudioBufferPointer - len);
		gAudioBufferPointer = gAudioBufferPointer - len;
	}
}

static int Audio_GetMinSize(int freq, int channels)
{
    jclass cls = 0;
    jmethodID midNotify = 0;
    int status;
    JNIEnv *env;
    int isAttached = 0;
    int ret=0;
   
    status = (*jniVM)->GetEnv(jniVM,(void **) &env, JNI_VERSION_1_4);
    if(status < 0) 
    {
        status = (*jniVM)->AttachCurrentThread(jniVM,&env, NULL);
        if(status < 0) 
        {
            a_printf("callback_handler: failed to attach current thread");
            return 0;
		}
        isAttached = 1;
    }

    if(cls==0)
    {
        cls=(*env)->GetObjectClass(env,ALynxEmuProxy);   
        if(cls==0)
        {
            a_printf("Can't find class ALynxEmuProxy");
            if(isAttached) (*jniVM)->DetachCurrentThread(jniVM);
            return 0;
        }
    }

    if(midNotify==0)
    {
        midNotify=(*env)->GetStaticMethodID(env,cls,"alynxInitAudio","(II)I");
        if(midNotify==0)
        {
            a_printf("Can't find alynxInitAudio in ALynxEmuProxy");
            if(isAttached) (*jniVM)->DetachCurrentThread(jniVM);
            return 0;
        }
    }

    ret = (*env)->CallStaticIntMethod(env,cls,midNotify,(jint)freq,(jint)channels,NULL);
    if(isAttached) (*jniVM)->DetachCurrentThread(jniVM);
    return ret;
}

int Audio_Init(void)
{
	mixlen = len = Audio_GetMinSize(freq,channels);
	a_printf("mixlen = %i\n",mixlen);
	return 1;
}

