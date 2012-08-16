/*
	Some stuff from SDL
 */

#ifndef __UTILS_H__
#define __UTILS_H__

#include <stdlib.h>
#include <stdio.h>

#define LIL_ENDIAN	1234
#define BIG_ENDIAN	4321
#define BYTEORDER	LIL_ENDIAN

typedef int8_t		Sint8;
typedef uint8_t		Uint8;
typedef int16_t		Sint16;
typedef uint16_t	Uint16;
typedef int32_t		Sint32;
typedef uint32_t	Uint32;

#ifdef __cplusplus
extern "C"{
#endif

int a_printf(const char *fmt, ...);
void StartTicks(void);
Uint32 GetTicks();
void Delay(Uint32 ms);

#ifdef __cplusplus
}
#endif

#endif