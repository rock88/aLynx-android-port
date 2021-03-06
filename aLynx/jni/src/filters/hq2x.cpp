//hq2x filter demo program
//----------------------------------------------------------
//Copyright (C) 2003 MaxSt ( maxst@hiend3d.com )

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA


#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "Utils.h"

typedef uint8_t uint24_t[3];

static int   RGBtoYUV[65536];
static int   YUV1, YUV2;

#define MASK_2     0x0000FF00
#define MASK_13    0x00FF00FF
#define MASK_RGB   0x00FFFFFF
#define MASK_ALPHA 0xFF000000

#define MASK16_2     0x07E0
#define MASK16_13    0xF81F
#define MASK16_RGB   0xFFFF

#define Ymask 0x00FF0000
#define Umask 0x0000FF00
#define Vmask 0x000000FF
#define trY   0x00300000
#define trU   0x00000700
#define trV   0x00000006

#define PIXEL00_0     *dp = w[5];
#define PIXEL00_10    *dp = Interp1_16(w[5], w[1]);
#define PIXEL00_11    *dp = Interp1_16(w[5], w[4]);
#define PIXEL00_12    *dp = Interp1_16(w[5], w[2]);
#define PIXEL00_20    *dp = Interp2_16(w[5], w[4], w[2]);
#define PIXEL00_21    *dp = Interp2_16(w[5], w[1], w[2]);
#define PIXEL00_22    *dp = Interp2_16(w[5], w[1], w[4]);
#define PIXEL00_60    *dp = Interp6_16(w[5], w[2], w[4]);
#define PIXEL00_61    *dp = Interp6_16(w[5], w[4], w[2]);
#define PIXEL00_70    *dp = Interp7_16(w[5], w[4], w[2]);
#define PIXEL00_90    *dp = Interp9_16(w[5], w[4], w[2]);
#define PIXEL00_100   *dp = Interp10_16(w[5], w[4], w[2]);
#define PIXEL01_0     *(dp+1) = w[5];
#define PIXEL01_10    *(dp+1) = Interp1_16(w[5], w[3]);
#define PIXEL01_11    *(dp+1) = Interp1_16(w[5], w[2]);
#define PIXEL01_12    *(dp+1) = Interp1_16(w[5], w[6]);
#define PIXEL01_20    *(dp+1) = Interp2_16(w[5], w[2], w[6]);
#define PIXEL01_21    *(dp+1) = Interp2_16(w[5], w[3], w[6]);
#define PIXEL01_22    *(dp+1) = Interp2_16(w[5], w[3], w[2]);
#define PIXEL01_60    *(dp+1) = Interp6_16(w[5], w[6], w[2]);
#define PIXEL01_61    *(dp+1) = Interp6_16(w[5], w[2], w[6]);
#define PIXEL01_70    *(dp+1) = Interp7_16(w[5], w[2], w[6]);
#define PIXEL01_90    *(dp+1) = Interp9_16(w[5], w[2], w[6]);
#define PIXEL01_100   *(dp+1) = Interp10_16(w[5], w[2], w[6]);
#define PIXEL10_0     *(dp+dpL) = w[5];
#define PIXEL10_10    *(dp+dpL) = Interp1_16(w[5], w[7]);
#define PIXEL10_11    *(dp+dpL) = Interp1_16(w[5], w[8]);
#define PIXEL10_12    *(dp+dpL) = Interp1_16(w[5], w[4]);
#define PIXEL10_20    *(dp+dpL) = Interp2_16(w[5], w[8], w[4]);
#define PIXEL10_21    *(dp+dpL) = Interp2_16(w[5], w[7], w[4]);
#define PIXEL10_22    *(dp+dpL) = Interp2_16(w[5], w[7], w[8]);
#define PIXEL10_60    *(dp+dpL) = Interp6_16(w[5], w[4], w[8]);
#define PIXEL10_61    *(dp+dpL) = Interp6_16(w[5], w[8], w[4]);
#define PIXEL10_70    *(dp+dpL) = Interp7_16(w[5], w[8], w[4]);
#define PIXEL10_90    *(dp+dpL) = Interp9_16(w[5], w[8], w[4]);
#define PIXEL10_100   *(dp+dpL) = Interp10_16(w[5], w[8], w[4]);
#define PIXEL11_0     *(dp+dpL+1) = w[5];
#define PIXEL11_10    *(dp+dpL+1) = Interp1_16(w[5], w[9]);
#define PIXEL11_11    *(dp+dpL+1) = Interp1_16(w[5], w[6]);
#define PIXEL11_12    *(dp+dpL+1) = Interp1_16(w[5], w[8]);
#define PIXEL11_20    *(dp+dpL+1) = Interp2_16(w[5], w[6], w[8]);
#define PIXEL11_21    *(dp+dpL+1) = Interp2_16(w[5], w[9], w[8]);
#define PIXEL11_22    *(dp+dpL+1) = Interp2_16(w[5], w[9], w[6]);
#define PIXEL11_60    *(dp+dpL+1) = Interp6_16(w[5], w[8], w[6]);
#define PIXEL11_61    *(dp+dpL+1) = Interp6_16(w[5], w[6], w[8]);
#define PIXEL11_70    *(dp+dpL+1) = Interp7_16(w[5], w[6], w[8]);
#define PIXEL11_90    *(dp+dpL+1) = Interp9_16(w[5], w[6], w[8]);
#define PIXEL11_100   *(dp+dpL+1) = Interp10_16(w[5], w[6], w[8]);

#define HQ2X_BITS 16
#define HQ2X_BYTES 2
#define HQ2X_CPY(to, from) (to) = (from)

#define HQ2X_FUNC hq2x_16
#define HQ2X_RB_FUNC hq2x_16_rb

#define RGB_TO_YUV_FUNC rgb16_to_yuv
#define DIFF_FUNC Diff16

static inline uint32_t rgb16_to_yuv(uint16_t c)
{
	//uint32_t i = (((c & 0xF800) << 8) | ((c & 0x07E0) << 5) | ((c & 0x001F) << 3));
	//a_printf("rgb16_to_yuv: i = %d",i);
    return 0xFFFFFFFF;/*RGBtoYUV[(((c & 0xF800) << 8) |
                     ((c & 0x07E0) << 5) |
                     ((c & 0x001F) << 3))];*/
}

static inline uint24_t *u24cpy(uint24_t *dst, const uint24_t src)
{
	memcpy(*dst, src, sizeof(*dst));
	return dst;
}

/* Test if there is difference in color */
static inline int yuv_diff(uint32_t yuv1, uint32_t yuv2) {
    return (( abs((yuv1 & Ymask) - (yuv2 & Ymask)) > trY ) ||
            ( abs((yuv1 & Umask) - (yuv2 & Umask)) > trU ) ||
            ( abs((yuv1 & Vmask) - (yuv2 & Vmask)) > trV ) );
}

static inline int Diff16(uint16_t c1, uint16_t c2)
{
#if 1
	YUV1 = RGBtoYUV[c1];
	YUV2 = RGBtoYUV[c2];
	return ( ( abs((YUV1 & Ymask) - (YUV2 & Ymask)) > trY ) ||
			( abs((YUV1 & Umask) - (YUV2 & Umask)) > trU ) ||
			( abs((YUV1 & Vmask) - (YUV2 & Vmask)) > trV ) );
#else
    return yuv_diff(rgb16_to_yuv(c1), rgb16_to_yuv(c2));
#endif
}

/* Interpolate functions */
static inline uint32_t Interpolate_2_32(uint32_t c1, int w1, uint32_t c2, int w2, int s)
{
    if (c1 == c2) {
        return c1;
    }
    return
        (((((c1 & MASK_ALPHA) >> 24) * w1 + ((c2 & MASK_ALPHA) >> 24) * w2) << (24-s)) & MASK_ALPHA) +
        ((((c1 & MASK_2) * w1 + (c2 & MASK_2) * w2) >> s) & MASK_2)	+
        ((((c1 & MASK_13) * w1 + (c2 & MASK_13) * w2) >> s) & MASK_13);
}

static inline uint32_t Interpolate_3_32(uint32_t c1, int w1, uint32_t c2, int w2, uint32_t c3, int w3, int s)
{
    return
        (((((c1 & MASK_ALPHA) >> 24) * w1 + ((c2 & MASK_ALPHA) >> 24) * w2 + ((c3 & MASK_ALPHA) >> 24) * w3) << (24-s)) & MASK_ALPHA) +
        ((((c1 & MASK_2) * w1 + (c2 & MASK_2) * w2 + (c3 & MASK_2) * w3) >> s) & MASK_2) +
        ((((c1 & MASK_13) * w1 + (c2 & MASK_13) * w2 + (c3 & MASK_13) * w3) >> s) & MASK_13);
}

static inline uint32_t Interp1_32(uint32_t c1, uint32_t c2)
{
    //(c1*3+c2) >> 2;
    return Interpolate_2_32(c1, 3, c2, 1, 2);
}

static inline uint32_t Interp2_32(uint32_t c1, uint32_t c2, uint32_t c3)
{
    //(c1*2+c2+c3) >> 2;
    return Interpolate_3_32(c1, 2, c2, 1, c3, 1, 2);
}

static inline uint32_t Interp3_32(uint32_t c1, uint32_t c2)
{
    //(c1*7+c2)/8;
    return Interpolate_2_32(c1, 7, c2, 1, 3);
}

static inline uint32_t Interp4_32(uint32_t c1, uint32_t c2, uint32_t c3)
{
    //(c1*2+(c2+c3)*7)/16;
    return Interpolate_3_32(c1, 2, c2, 7, c3, 7, 4);
}

static inline uint32_t Interp5_32(uint32_t c1, uint32_t c2)
{
    //(c1+c2) >> 1;
    return Interpolate_2_32(c1, 1, c2, 1, 1);
}

static inline uint32_t Interp6_32(uint32_t c1, uint32_t c2, uint32_t c3)
{
    //(c1*5+c2*2+c3)/8;
    return Interpolate_3_32(c1, 5, c2, 2, c3, 1, 3);
}

static inline uint32_t Interp7_32(uint32_t c1, uint32_t c2, uint32_t c3)
{
    //(c1*6+c2+c3)/8;
    return Interpolate_3_32(c1, 6, c2, 1, c3, 1, 3);
}

static inline uint32_t Interp8_32(uint32_t c1, uint32_t c2)
{
    //(c1*5+c2*3)/8;
    return Interpolate_2_32(c1, 5, c2, 3, 3);
}

static inline uint32_t Interp9_32(uint32_t c1, uint32_t c2, uint32_t c3)
{
    //(c1*2+(c2+c3)*3)/8;
    return Interpolate_3_32(c1, 2, c2, 3, c3, 3, 3);
}

static inline uint32_t Interp10_32(uint32_t c1, uint32_t c2, uint32_t c3)
{
    //(c1*14+c2+c3)/16;
    return Interpolate_3_32(c1, 14, c2, 1, c3, 1, 4);
}

/* Interpolate functions (16 bit, 565) */
static inline uint16_t Interpolate_2_16(uint16_t c1, int w1, uint16_t c2, int w2, int s)
{
    if (c1 == c2) {
        return c1;
    }
    return
        ((((c1 & MASK16_2) * w1 + (c2 & MASK16_2) * w2) >> s) & MASK16_2) +
        ((((c1 & MASK16_13) * w1 + (c2 & MASK16_13) * w2) >> s) & MASK16_13);
}

static inline uint16_t Interpolate_3_16(uint16_t c1, int w1, uint16_t c2, int w2, uint16_t c3, int w3, int s)
{
    return
        ((((c1 & MASK16_2) * w1 + (c2 & MASK16_2) * w2 + (c3 & MASK16_2) * w3) >> s) & MASK16_2) +
        ((((c1 & MASK16_13) * w1 + (c2 & MASK16_13) * w2 + (c3 & MASK16_13) * w3) >> s) & MASK16_13);
}

static inline uint16_t Interp1_16(uint16_t c1, uint16_t c2)
{
    //(c1*3+c2) >> 2;
    return Interpolate_2_16(c1, 3, c2, 1, 2);
}

static inline uint16_t Interp2_16(uint16_t c1, uint16_t c2, uint16_t c3)
{
    //(c1*2+c2+c3) >> 2;
    return Interpolate_3_16(c1, 2, c2, 1, c3, 1, 2);
}

static inline uint16_t Interp3_16(uint16_t c1, uint16_t c2)
{
    //(c1*7+c2)/8;
    return Interpolate_2_16(c1, 7, c2, 1, 3);
}

static inline uint16_t Interp4_16(uint16_t c1, uint16_t c2, uint16_t c3)
{
    //(c1*2+(c2+c3)*7)/16;
    return Interpolate_3_16(c1, 2, c2, 7, c3, 7, 4);
}

static inline uint16_t Interp5_16(uint16_t c1, uint16_t c2)
{
    //(c1+c2) >> 1;
    return Interpolate_2_16(c1, 1, c2, 1, 1);
}

static inline uint16_t Interp6_16(uint16_t c1, uint16_t c2, uint16_t c3)
{
    //(c1*5+c2*2+c3)/8;
    return Interpolate_3_16(c1, 5, c2, 2, c3, 1, 3);
}

static inline uint16_t Interp7_16(uint16_t c1, uint16_t c2, uint16_t c3)
{
    //(c1*6+c2+c3)/8;
    return Interpolate_3_16(c1, 6, c2, 1, c3, 1, 3);
}

static inline uint16_t Interp8_16(uint16_t c1, uint16_t c2)
{
    //(c1*5+c2*3)/8;
    return Interpolate_2_16(c1, 5, c2, 3, 3);
}

static inline uint16_t Interp9_16(uint16_t c1, uint16_t c2, uint16_t c3)
{
    //(c1*2+(c2+c3)*3)/8;
    return Interpolate_3_16(c1, 2, c2, 3, c3, 3, 3);
}

static inline uint16_t Interp10_16(uint16_t c1, uint16_t c2, uint16_t c3)
{
    //(c1*14+c2+c3)/16;
    return Interpolate_3_16(c1, 14, c2, 1, c3, 1, 4);
}

/* Interpolate functions (24 bit, 888) */
static inline void Interpolate_2_24(uint24_t *ret, uint24_t c1, int w1, uint24_t c2, int w2, int s)
{
    if (!memcmp(c1, c2, 3)) {
        u24cpy(ret, c1);
        return;
    }
    (*ret)[0] = (((c1[0] * w1) + (c2[0] * w2)) >> s);
    (*ret)[1] = (((c1[1] * w1) + (c2[1] * w2)) >> s);
    (*ret)[2] = (((c1[2] * w1) + (c2[2] * w2)) >> s);
}

static inline void Interpolate_3_24(uint24_t *ret, uint24_t c1, int w1, uint24_t c2, int w2, uint24_t c3, int w3, int s)
{
    (*ret)[0] = (((c1[0] * w1) + (c2[0] * w2) + (c3[0] * w3)) >> s);
    (*ret)[1] = (((c1[1] * w1) + (c2[1] * w2) + (c3[1] * w3)) >> s);
    (*ret)[2] = (((c1[2] * w1) + (c2[2] * w2) + (c3[2] * w3)) >> s);
}

static inline void Interp1_24(uint24_t *ret, uint24_t c1, uint24_t c2)
{
    //(c1*3+c2) >> 2;
    Interpolate_2_24(ret, c1, 3, c2, 1, 2);
}

static inline void Interp2_24(uint24_t *ret, uint24_t c1, uint24_t c2, uint24_t c3)
{
    //(c1*2+c2+c3) >> 2;
    Interpolate_3_24(ret, c1, 2, c2, 1, c3, 1, 2);
}

static inline void Interp3_24(uint24_t *ret, uint24_t c1, uint24_t c2)
{
    //(c1*7+c2)/8;
    Interpolate_2_24(ret, c1, 7, c2, 1, 3);
}

static inline void Interp4_24(uint24_t *ret, uint24_t c1, uint24_t c2, uint24_t c3)
{
    //(c1*2+(c2+c3)*7)/16;
    Interpolate_3_24(ret, c1, 2, c2, 7, c3, 7, 4);
}

static inline void Interp5_24(uint24_t *ret, uint24_t c1, uint24_t c2)
{
    //(c1+c2) >> 1;
    Interpolate_2_24(ret, c1, 1, c2, 1, 1);
}

static inline void Interp6_24(uint24_t *ret, uint24_t c1, uint24_t c2, uint24_t c3)
{
    //(c1*5+c2*2+c3)/8;
    Interpolate_3_24(ret, c1, 5, c2, 2, c3, 1, 3);
}

static inline void Interp7_24(uint24_t *ret, uint24_t c1, uint24_t c2, uint24_t c3)
{
    //(c1*6+c2+c3)/8;
    Interpolate_3_24(ret, c1, 6, c2, 1, c3, 1, 3);
}

static inline void Interp8_24(uint24_t *ret, uint24_t c1, uint24_t c2)
{
    //(c1*5+c2*3)/8;
    Interpolate_2_24(ret, c1, 5, c2, 3, 3);
}

static inline void Interp9_24(uint24_t *ret, uint24_t c1, uint24_t c2, uint24_t c3)
{
    //(c1*2+(c2+c3)*3)/8;
    Interpolate_3_24(ret, c1, 2, c2, 3, c3, 3, 3);
}

static inline void Interp10_24(uint24_t *ret, uint24_t c1, uint24_t c2, uint24_t c3)
{
    //(c1*14+c2+c3)/16;
    Interpolate_3_24(ret, c1, 14, c2, 1, c3, 1, 4);
}

void hq2x_16_rb( uint16_t * sp, uint32_t srb, uint16_t * dp, uint32_t drb, int Xres, int Yres )
{
    int  i, j, k;
    int  prevline, nextline;
    uint16_t  w[10];
    int dpL = (drb / HQ2X_BYTES);
    int spL = (srb / HQ2X_BYTES);
    uint8_t *sRowP = (uint8_t *) sp;
    uint8_t *dRowP = (uint8_t *) dp;
    uint32_t yuv1, yuv2;

    //   +----+----+----+
    //   |    |    |    |
    //   | w1 | w2 | w3 |
    //   +----+----+----+
    //   |    |    |    |
    //   | w4 | w5 | w6 |
    //   +----+----+----+
    //   |    |    |    |
    //   | w7 | w8 | w9 |
    //   +----+----+----+

    for (j=0; j<Yres; j++)
    {
        if (j>0)      prevline = -spL; else prevline = 0;
        if (j<Yres-1) nextline =  spL; else nextline = 0;

        for (i=0; i<Xres; i++)
        {
			//a_printf("j = %d",j);
            HQ2X_CPY(w[2], *(sp + prevline));
            HQ2X_CPY(w[5], *sp);
            HQ2X_CPY(w[8], *(sp + nextline));

            if (i>0)
            {
                HQ2X_CPY(w[1], *(sp + prevline - 1));
                HQ2X_CPY(w[4], *(sp - 1));
                HQ2X_CPY(w[7], *(sp + nextline - 1));
            }
            else
            {
                HQ2X_CPY(w[1], w[2]);
                HQ2X_CPY(w[4], w[5]);
                HQ2X_CPY(w[7], w[8]);
            }

            if (i<Xres-1)
            {
                HQ2X_CPY(w[3], *(sp + prevline + 1));
                HQ2X_CPY(w[6], *(sp + 1));
                HQ2X_CPY(w[9], *(sp + nextline + 1));
            }
            else
            {
                HQ2X_CPY(w[3], w[2]);
                HQ2X_CPY(w[6], w[5]);
                HQ2X_CPY(w[9], w[8]);
            }

            int pattern = 0;
            int flag = 1;

            yuv1 = RGB_TO_YUV_FUNC(w[5]);

            for (k=1; k<=9; k++)
            {
                if (k==5) continue;

                if ( w[k] != w[5] )
                {
                    yuv2 = RGB_TO_YUV_FUNC(w[k]);
                    if (yuv_diff(yuv1, yuv2))
                        pattern |= flag;
                }
                flag <<= 1;
            }

            switch (pattern)
            {
                case 0:
                case 1:
                case 4:
                case 32:
                case 128:
                case 5:
                case 132:
                case 160:
                case 33:
                case 129:
                case 36:
                case 133:
                case 164:
                case 161:
                case 37:
                case 165:
                    {
                        PIXEL00_20
                        PIXEL01_20
                        PIXEL10_20
                        PIXEL11_20
                        break;
                    }
                case 2:
                case 34:
                case 130:
                case 162:
                    {
                        PIXEL00_22
                        PIXEL01_21
                        PIXEL10_20
                        PIXEL11_20
                        break;
                    }
                case 16:
                case 17:
                case 48:
                case 49:
                    {
                        PIXEL00_20
                        PIXEL01_22
                        PIXEL10_20
                        PIXEL11_21
                        break;
                    }
                case 64:
                case 65:
                case 68:
                case 69:
                    {
                        PIXEL00_20
                        PIXEL01_20
                        PIXEL10_21
                        PIXEL11_22
                        break;
                    }
                case 8:
                case 12:
                case 136:
                case 140:
                    {
                        PIXEL00_21
                        PIXEL01_20
                        PIXEL10_22
                        PIXEL11_20
                        break;
                    }
                case 3:
                case 35:
                case 131:
                case 163:
                    {
                        PIXEL00_11
                        PIXEL01_21
                        PIXEL10_20
                        PIXEL11_20
                        break;
                    }
                case 6:
                case 38:
                case 134:
                case 166:
                    {
                        PIXEL00_22
                        PIXEL01_12
                        PIXEL10_20
                        PIXEL11_20
                        break;
                    }
                case 20:
                case 21:
                case 52:
                case 53:
                    {
                        PIXEL00_20
                        PIXEL01_11
                        PIXEL10_20
                        PIXEL11_21
                        break;
                    }
                case 144:
                case 145:
                case 176:
                case 177:
                    {
                        PIXEL00_20
                        PIXEL01_22
                        PIXEL10_20
                        PIXEL11_12
                        break;
                    }
                case 192:
                case 193:
                case 196:
                case 197:
                    {
                        PIXEL00_20
                        PIXEL01_20
                        PIXEL10_21
                        PIXEL11_11
                        break;
                    }
                case 96:
                case 97:
                case 100:
                case 101:
                    {
                        PIXEL00_20
                        PIXEL01_20
                        PIXEL10_12
                        PIXEL11_22
                        break;
                    }
                case 40:
                case 44:
                case 168:
                case 172:
                    {
                        PIXEL00_21
                        PIXEL01_20
                        PIXEL10_11
                        PIXEL11_20
                        break;
                    }
                case 9:
                case 13:
                case 137:
                case 141:
                    {
                        PIXEL00_12
                        PIXEL01_20
                        PIXEL10_22
                        PIXEL11_20
                        break;
                    }
                case 18:
                case 50:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_20
                        PIXEL11_21
                        break;
                    }
                case 80:
                case 81:
                    {
                        PIXEL00_20
                        PIXEL01_22
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 72:
                case 76:
                    {
                        PIXEL00_21
                        PIXEL01_20
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_22
                        break;
                    }
                case 10:
                case 138:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_21
                        PIXEL10_22
                        PIXEL11_20
                        break;
                    }
                case 66:
                    {
                        PIXEL00_22
                        PIXEL01_21
                        PIXEL10_21
                        PIXEL11_22
                        break;
                    }
                case 24:
                    {
                        PIXEL00_21
                        PIXEL01_22
                        PIXEL10_22
                        PIXEL11_21
                        break;
                    }
                case 7:
                case 39:
                case 135:
                    {
                        PIXEL00_11
                        PIXEL01_12
                        PIXEL10_20
                        PIXEL11_20
                        break;
                    }
                case 148:
                case 149:
                case 180:
                    {
                        PIXEL00_20
                        PIXEL01_11
                        PIXEL10_20
                        PIXEL11_12
                        break;
                    }
                case 224:
                case 228:
                case 225:
                    {
                        PIXEL00_20
                        PIXEL01_20
                        PIXEL10_12
                        PIXEL11_11
                        break;
                    }
                case 41:
                case 169:
                case 45:
                    {
                        PIXEL00_12
                        PIXEL01_20
                        PIXEL10_11
                        PIXEL11_20
                        break;
                    }
                case 22:
                case 54:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_20
                        PIXEL11_21
                        break;
                    }
                case 208:
                case 209:
                    {
                        PIXEL00_20
                        PIXEL01_22
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 104:
                case 108:
                    {
                        PIXEL00_21
                        PIXEL01_20
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_22
                        break;
                    }
                case 11:
                case 139:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_21
                        PIXEL10_22
                        PIXEL11_20
                        break;
                    }
                case 19:
                case 51:
                    {
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL00_11
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL00_60
                            PIXEL01_90
                        }
                        PIXEL10_20
                        PIXEL11_21
                        break;
                    }
                case 146:
                case 178:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                            PIXEL11_12
                        }
                        else
                        {
                            PIXEL01_90
                            PIXEL11_61
                        }
                        PIXEL10_20
                        break;
                    }
                case 84:
                case 85:
                    {
                        PIXEL00_20
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL01_11
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL01_60
                            PIXEL11_90
                        }
                        PIXEL10_21
                        break;
                    }
                case 112:
                case 113:
                    {
                        PIXEL00_20
                        PIXEL01_22
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL10_12
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL10_61
                            PIXEL11_90
                        }
                        break;
                    }
                case 200:
                case 204:
                    {
                        PIXEL00_21
                        PIXEL01_20
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                            PIXEL11_11
                        }
                        else
                        {
                            PIXEL10_90
                            PIXEL11_60
                        }
                        break;
                    }
                case 73:
                case 77:
                    {
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL00_12
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL00_61
                            PIXEL10_90
                        }
                        PIXEL01_20
                        PIXEL11_22
                        break;
                    }
                case 42:
                case 170:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                            PIXEL10_11
                        }
                        else
                        {
                            PIXEL00_90
                            PIXEL10_60
                        }
                        PIXEL01_21
                        PIXEL11_20
                        break;
                    }
                case 14:
                case 142:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                            PIXEL01_12
                        }
                        else
                        {
                            PIXEL00_90
                            PIXEL01_61
                        }
                        PIXEL10_22
                        PIXEL11_20
                        break;
                    }
                case 67:
                    {
                        PIXEL00_11
                        PIXEL01_21
                        PIXEL10_21
                        PIXEL11_22
                        break;
                    }
                case 70:
                    {
                        PIXEL00_22
                        PIXEL01_12
                        PIXEL10_21
                        PIXEL11_22
                        break;
                    }
                case 28:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        PIXEL10_22
                        PIXEL11_21
                        break;
                    }
                case 152:
                    {
                        PIXEL00_21
                        PIXEL01_22
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 194:
                    {
                        PIXEL00_22
                        PIXEL01_21
                        PIXEL10_21
                        PIXEL11_11
                        break;
                    }
                case 98:
                    {
                        PIXEL00_22
                        PIXEL01_21
                        PIXEL10_12
                        PIXEL11_22
                        break;
                    }
                case 56:
                    {
                        PIXEL00_21
                        PIXEL01_22
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 25:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        PIXEL10_22
                        PIXEL11_21
                        break;
                    }
                case 26:
                case 31:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_22
                        PIXEL11_21
                        break;
                    }
                case 82:
                case 214:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 88:
                case 248:
                    {
                        PIXEL00_21
                        PIXEL01_22
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 74:
                case 107:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_21
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_22
                        break;
                    }
                case 27:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_10
                        PIXEL10_22
                        PIXEL11_21
                        break;
                    }
                case 86:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_21
                        PIXEL11_10
                        break;
                    }
                case 216:
                    {
                        PIXEL00_21
                        PIXEL01_22
                        PIXEL10_10
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 106:
                    {
                        PIXEL00_10
                        PIXEL01_21
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_22
                        break;
                    }
                case 30:
                    {
                        PIXEL00_10
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_22
                        PIXEL11_21
                        break;
                    }
                case 210:
                    {
                        PIXEL00_22
                        PIXEL01_10
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 120:
                    {
                        PIXEL00_21
                        PIXEL01_22
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_10
                        break;
                    }
                case 75:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_21
                        PIXEL10_10
                        PIXEL11_22
                        break;
                    }
                case 29:
                    {
                        PIXEL00_12
                        PIXEL01_11
                        PIXEL10_22
                        PIXEL11_21
                        break;
                    }
                case 198:
                    {
                        PIXEL00_22
                        PIXEL01_12
                        PIXEL10_21
                        PIXEL11_11
                        break;
                    }
                case 184:
                    {
                        PIXEL00_21
                        PIXEL01_22
                        PIXEL10_11
                        PIXEL11_12
                        break;
                    }
                case 99:
                    {
                        PIXEL00_11
                        PIXEL01_21
                        PIXEL10_12
                        PIXEL11_22
                        break;
                    }
                case 57:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 71:
                    {
                        PIXEL00_11
                        PIXEL01_12
                        PIXEL10_21
                        PIXEL11_22
                        break;
                    }
                case 156:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 226:
                    {
                        PIXEL00_22
                        PIXEL01_21
                        PIXEL10_12
                        PIXEL11_11
                        break;
                    }
                case 60:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 195:
                    {
                        PIXEL00_11
                        PIXEL01_21
                        PIXEL10_21
                        PIXEL11_11
                        break;
                    }
                case 102:
                    {
                        PIXEL00_22
                        PIXEL01_12
                        PIXEL10_12
                        PIXEL11_22
                        break;
                    }
                case 153:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 58:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 83:
                    {
                        PIXEL00_11
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 92:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 202:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        PIXEL01_21
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        PIXEL11_11
                        break;
                    }
                case 78:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        PIXEL01_12
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        PIXEL11_22
                        break;
                    }
                case 154:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 114:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 89:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 90:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 55:
                case 23:
                    {
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL00_11
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL00_60
                            PIXEL01_90
                        }
                        PIXEL10_20
                        PIXEL11_21
                        break;
                    }
                case 182:
                case 150:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                            PIXEL11_12
                        }
                        else
                        {
                            PIXEL01_90
                            PIXEL11_61
                        }
                        PIXEL10_20
                        break;
                    }
                case 213:
                case 212:
                    {
                        PIXEL00_20
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL01_11
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL01_60
                            PIXEL11_90
                        }
                        PIXEL10_21
                        break;
                    }
                case 241:
                case 240:
                    {
                        PIXEL00_20
                        PIXEL01_22
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL10_12
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL10_61
                            PIXEL11_90
                        }
                        break;
                    }
                case 236:
                case 232:
                    {
                        PIXEL00_21
                        PIXEL01_20
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                            PIXEL11_11
                        }
                        else
                        {
                            PIXEL10_90
                            PIXEL11_60
                        }
                        break;
                    }
                case 109:
                case 105:
                    {
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL00_12
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL00_61
                            PIXEL10_90
                        }
                        PIXEL01_20
                        PIXEL11_22
                        break;
                    }
                case 171:
                case 43:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                            PIXEL10_11
                        }
                        else
                        {
                            PIXEL00_90
                            PIXEL10_60
                        }
                        PIXEL01_21
                        PIXEL11_20
                        break;
                    }
                case 143:
                case 15:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                            PIXEL01_12
                        }
                        else
                        {
                            PIXEL00_90
                            PIXEL01_61
                        }
                        PIXEL10_22
                        PIXEL11_20
                        break;
                    }
                case 124:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_10
                        break;
                    }
                case 203:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_21
                        PIXEL10_10
                        PIXEL11_11
                        break;
                    }
                case 62:
                    {
                        PIXEL00_10
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 211:
                    {
                        PIXEL00_11
                        PIXEL01_10
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 118:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_12
                        PIXEL11_10
                        break;
                    }
                case 217:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        PIXEL10_10
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 110:
                    {
                        PIXEL00_10
                        PIXEL01_12
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_22
                        break;
                    }
                case 155:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_10
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 188:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        PIXEL10_11
                        PIXEL11_12
                        break;
                    }
                case 185:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        PIXEL10_11
                        PIXEL11_12
                        break;
                    }
                case 61:
                    {
                        PIXEL00_12
                        PIXEL01_11
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 157:
                    {
                        PIXEL00_12
                        PIXEL01_11
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 103:
                    {
                        PIXEL00_11
                        PIXEL01_12
                        PIXEL10_12
                        PIXEL11_22
                        break;
                    }
                case 227:
                    {
                        PIXEL00_11
                        PIXEL01_21
                        PIXEL10_12
                        PIXEL11_11
                        break;
                    }
                case 230:
                    {
                        PIXEL00_22
                        PIXEL01_12
                        PIXEL10_12
                        PIXEL11_11
                        break;
                    }
                case 199:
                    {
                        PIXEL00_11
                        PIXEL01_12
                        PIXEL10_21
                        PIXEL11_11
                        break;
                    }
                case 220:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 158:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 234:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        PIXEL01_21
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_11
                        break;
                    }
                case 242:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 59:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 121:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 87:
                    {
                        PIXEL00_11
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 79:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_12
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        PIXEL11_22
                        break;
                    }
                case 122:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 94:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 218:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 91:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 229:
                    {
                        PIXEL00_20
                        PIXEL01_20
                        PIXEL10_12
                        PIXEL11_11
                        break;
                    }
                case 167:
                    {
                        PIXEL00_11
                        PIXEL01_12
                        PIXEL10_20
                        PIXEL11_20
                        break;
                    }
                case 173:
                    {
                        PIXEL00_12
                        PIXEL01_20
                        PIXEL10_11
                        PIXEL11_20
                        break;
                    }
                case 181:
                    {
                        PIXEL00_20
                        PIXEL01_11
                        PIXEL10_20
                        PIXEL11_12
                        break;
                    }
                case 186:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_11
                        PIXEL11_12
                        break;
                    }
                case 115:
                    {
                        PIXEL00_11
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 93:
                    {
                        PIXEL00_12
                        PIXEL01_11
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 206:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        PIXEL01_12
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        PIXEL11_11
                        break;
                    }
                case 205:
                case 201:
                    {
                        PIXEL00_12
                        PIXEL01_20
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_10
                        }
                        else
                        {
                            PIXEL10_70
                        }
                        PIXEL11_11
                        break;
                    }
                case 174:
                case 46:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_10
                        }
                        else
                        {
                            PIXEL00_70
                        }
                        PIXEL01_12
                        PIXEL10_11
                        PIXEL11_20
                        break;
                    }
                case 179:
                case 147:
                    {
                        PIXEL00_11
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_10
                        }
                        else
                        {
                            PIXEL01_70
                        }
                        PIXEL10_20
                        PIXEL11_12
                        break;
                    }
                case 117:
                case 116:
                    {
                        PIXEL00_20
                        PIXEL01_11
                        PIXEL10_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_10
                        }
                        else
                        {
                            PIXEL11_70
                        }
                        break;
                    }
                case 189:
                    {
                        PIXEL00_12
                        PIXEL01_11
                        PIXEL10_11
                        PIXEL11_12
                        break;
                    }
                case 231:
                    {
                        PIXEL00_11
                        PIXEL01_12
                        PIXEL10_12
                        PIXEL11_11
                        break;
                    }
                case 126:
                    {
                        PIXEL00_10
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_10
                        break;
                    }
                case 219:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_10
                        PIXEL10_10
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 125:
                    {
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL00_12
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL00_61
                            PIXEL10_90
                        }
                        PIXEL01_11
                        PIXEL11_10
                        break;
                    }
                case 221:
                    {
                        PIXEL00_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL01_11
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL01_60
                            PIXEL11_90
                        }
                        PIXEL10_10
                        break;
                    }
                case 207:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                            PIXEL01_12
                        }
                        else
                        {
                            PIXEL00_90
                            PIXEL01_61
                        }
                        PIXEL10_10
                        PIXEL11_11
                        break;
                    }
                case 238:
                    {
                        PIXEL00_10
                        PIXEL01_12
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                            PIXEL11_11
                        }
                        else
                        {
                            PIXEL10_90
                            PIXEL11_60
                        }
                        break;
                    }
                case 190:
                    {
                        PIXEL00_10
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                            PIXEL11_12
                        }
                        else
                        {
                            PIXEL01_90
                            PIXEL11_61
                        }
                        PIXEL10_11
                        break;
                    }
                case 187:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                            PIXEL10_11
                        }
                        else
                        {
                            PIXEL00_90
                            PIXEL10_60
                        }
                        PIXEL01_10
                        PIXEL11_12
                        break;
                    }
                case 243:
                    {
                        PIXEL00_11
                        PIXEL01_10
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL10_12
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL10_61
                            PIXEL11_90
                        }
                        break;
                    }
                case 119:
                    {
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL00_11
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL00_60
                            PIXEL01_90
                        }
                        PIXEL10_12
                        PIXEL11_10
                        break;
                    }
                case 237:
                case 233:
                    {
                        PIXEL00_12
                        PIXEL01_20
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_100
                        }
                        PIXEL11_11
                        break;
                    }
                case 175:
                case 47:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_100
                        }
                        PIXEL01_12
                        PIXEL10_11
                        PIXEL11_20
                        break;
                    }
                case 183:
                case 151:
                    {
                        PIXEL00_11
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_100
                        }
                        PIXEL10_20
                        PIXEL11_12
                        break;
                    }
                case 245:
                case 244:
                    {
                        PIXEL00_20
                        PIXEL01_11
                        PIXEL10_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_100
                        }
                        break;
                    }
                case 250:
                    {
                        PIXEL00_10
                        PIXEL01_10
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 123:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_10
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_10
                        break;
                    }
                case 95:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_10
                        PIXEL11_10
                        break;
                    }
                case 222:
                    {
                        PIXEL00_10
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_10
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 252:
                    {
                        PIXEL00_21
                        PIXEL01_11
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_100
                        }
                        break;
                    }
                case 249:
                    {
                        PIXEL00_12
                        PIXEL01_22
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_100
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 235:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_21
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_100
                        }
                        PIXEL11_11
                        break;
                    }
                case 111:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_100
                        }
                        PIXEL01_12
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_22
                        break;
                    }
                case 63:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_100
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_11
                        PIXEL11_21
                        break;
                    }
                case 159:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_100
                        }
                        PIXEL10_22
                        PIXEL11_12
                        break;
                    }
                case 215:
                    {
                        PIXEL00_11
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_100
                        }
                        PIXEL10_21
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 246:
                    {
                        PIXEL00_22
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        PIXEL10_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_100
                        }
                        break;
                    }
                case 254:
                    {
                        PIXEL00_10
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_100
                        }
                        break;
                    }
                case 253:
                    {
                        PIXEL00_12
                        PIXEL01_11
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_100
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_100
                        }
                        break;
                    }
                case 251:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        PIXEL01_10
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_100
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 239:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_100
                        }
                        PIXEL01_12
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_100
                        }
                        PIXEL11_11
                        break;
                    }
                case 127:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_100
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_20
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_20
                        }
                        PIXEL11_10
                        break;
                    }
                case 191:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_100
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_100
                        }
                        PIXEL10_11
                        PIXEL11_12
                        break;
                    }
                case 223:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_20
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_100
                        }
                        PIXEL10_10
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_20
                        }
                        break;
                    }
                case 247:
                    {
                        PIXEL00_11
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_100
                        }
                        PIXEL10_12
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_100
                        }
                        break;
                    }
                case 255:
                    {
                        if (DIFF_FUNC(w[4], w[2]))
                        {
                            PIXEL00_0
                        }
                        else
                        {
                            PIXEL00_100
                        }
                        if (DIFF_FUNC(w[2], w[6]))
                        {
                            PIXEL01_0
                        }
                        else
                        {
                            PIXEL01_100
                        }
                        if (DIFF_FUNC(w[8], w[4]))
                        {
                            PIXEL10_0
                        }
                        else
                        {
                            PIXEL10_100
                        }
                        if (DIFF_FUNC(w[6], w[8]))
                        {
                            PIXEL11_0
                        }
                        else
                        {
                            PIXEL11_100
                        }
                        break;
                    }
            }
            sp++;
            dp += 2;
        }

        sRowP += srb;
        sp = (uint16_t *) sRowP;

        dRowP += drb * 2;
        dp = (uint16_t *) dRowP;
    }
}

void hq2x_16( uint16_t * sp, uint16_t * dp, int Xres, int Yres )
{
    uint32_t rowBytesL = Xres * HQ2X_BYTES;
    hq2x_16_rb(sp, rowBytesL, dp, rowBytesL * 2, Xres, Yres);
}

void InitLUTs(void)
{
	int i, j, k, r, g, b, Y, u, v;
	for (i=0; i<32; i++)
		for (j=0; j<64; j++)
			for (k=0; k<32; k++)
			{
				r = i << 3;
				g = j << 2;
				b = k << 3;
				Y = (r + g + b) >> 2;
				u = 128 + ((r - b) >> 2);
				v = 128 + ((-r + 2*g -b)>>3);
				RGBtoYUV[ (i << 11) + (j << 5) + k ] = (Y<<16) + (u<<8) + v;
			}
}