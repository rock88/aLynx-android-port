/*
	Some stuff from SDL
 */

#include <sys/time.h>
#include <unistd.h>
#include <errno.h>
#include <android/log.h>

#include "Utils.h"

static struct timeval start;

int a_printf(const char *fmt, ...)
{
    va_list ap;
    va_start(ap,fmt);
    return __android_log_vprint(ANDROID_LOG_WARN, "ALYNX", fmt, ap);
}

void StartTicks(void)
{
	gettimeofday(&start, NULL);
}

Uint32 GetTicks()
{
	struct timeval now;
	Uint32 ticks;

	gettimeofday(&now, NULL);
	ticks=(now.tv_sec-start.tv_sec)*1000+(now.tv_usec-start.tv_usec)/1000;
	return(ticks);
}

void Delay(Uint32 ms)
{
	int was_error;
	struct timeval tv;
	Uint32 then, now, elapsed;
	then = GetTicks();
	do {
		errno = 0;

		/* Calculate the time interval left (in case of interrupt) */
		now = GetTicks();
		elapsed = (now-then);
		then = now;
		if ( elapsed >= ms ) {
			break;
		}
		ms -= elapsed;
		tv.tv_sec = ms/1000;
		tv.tv_usec = (ms%1000)*1000;

		was_error = select(0, NULL, NULL, NULL, &tv);

	} while ( was_error && (errno == EINTR) );

}