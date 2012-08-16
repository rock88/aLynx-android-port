/*
 * SDLEMU library - Free sdl related functions library
 * Copyrigh(c) 1999-2002 sdlemu development crew
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
typedef unsigned char u8;
typedef unsigned short u16;
typedef unsigned int u32;
typedef signed int s32;

static u32 colorMask = 0xF7DEF7DE;
static u32 lowPixelMask = 0x08210821;
static u32 qcolorMask = 0xE79CE79C;
static u32 qlowpixelMask = 0x18631863;
static u32 redblueMask = 0xF81F;
static u32 greenMask = 0x7E0;

int Init_2xSaI()
{
	colorMask = 0xF7DEF7DE;
	lowPixelMask = 0x08210821;
	qcolorMask = 0xE79CE79C;
	qlowpixelMask = 0x18631863;
	redblueMask = 0xF81F;
	greenMask = 0x7E0;
	return 1;
}

static inline int GetResult1 (u32 A, u32 B, u32 C, u32 D, u32 /* E */)
{
	int x = 0;
	int y = 0;
	int r = 0;

	if (A == C) x += 1;
		else if (B == C) y += 1;
	if (A == D) x += 1;
		else if (B == D) y += 1;
	if (x <= 1) r += 1;
	if (y <= 1) r -= 1;
	return r;
}

static inline int GetResult2 (u32 A, u32 B, u32 C, u32 D, u32 /* E */)
{
	int x = 0;
	int y = 0;
	int r = 0;

	if (A == C) x += 1;
		else if (B == C) y += 1;
	if (A == D) x += 1;
		else if (B == D) y += 1;
	if (x <= 1) r -= 1;
	if (y <= 1) r += 1;
	return r;
}

static inline u32 INTERPOLATE (u32 A, u32 B)
{
	if (A != B)
	{
		return (((A & colorMask) >> 1) + ((B & colorMask) >> 1) + (A & B & lowPixelMask));
	}
	else return A;
}

static inline u32 Q_INTERPOLATE (u32 A, u32 B, u32 C, u32 D)
{
	register u32 x = ((A & qcolorMask) >> 2) + ((B & qcolorMask) >> 2) + ((C & qcolorMask) >> 2) + ((D & qcolorMask) >> 2);
	register u32 y = (A & qlowpixelMask) + (B & qlowpixelMask) + (C & qlowpixelMask) + (D & qlowpixelMask);
	y = (y >> 2) & qlowpixelMask;
	return x + y;
}

void _2xSaI(u8 *srcPtr, u32 srcPitch, u8 *deltaPtr, u8 *dstPtr, u32 dstPitch, int width, int height)
{
	u8  *dP;
	u16 *bP;
	u32 inc_bP = 1;
	u32 Nextline = srcPitch >> 1;

	for (; height; height--)
	{
		bP = (u16 *) srcPtr;
		dP = dstPtr;

		for (u32 finish = width; finish; finish -= inc_bP)
		{
			register u32 colorA, colorB;
			u32 colorC, colorD, colorE, colorF, colorG, colorH, colorI, colorJ, colorK, colorL, colorM, colorN, colorO, colorP;
			u32 product, product1, product2;

			//---------------------------------------
			// Map of the pixels:                    I|E F|J
			//                                       G|A B|K
			//                                       H|C D|L
			//                                       M|N O|P
			colorI = *(bP - Nextline - 1);
			colorE = *(bP - Nextline);
			colorF = *(bP - Nextline + 1);
			colorJ = *(bP - Nextline + 2);

			colorG = *(bP - 1);
			colorA = *(bP);
			colorB = *(bP + 1);
			colorK = *(bP + 2);

			colorH = *(bP + Nextline - 1);
			colorC = *(bP + Nextline);
			colorD = *(bP + Nextline + 1);
			colorL = *(bP + Nextline + 2);

			colorM = *(bP + Nextline + Nextline - 1);
			colorN = *(bP + Nextline + Nextline);
			colorO = *(bP + Nextline + Nextline + 1);
			colorP = *(bP + Nextline + Nextline + 2);

			if ((colorA == colorD) && (colorB != colorC))
			{
				if (((colorA == colorE) && (colorB == colorL)) || ((colorA == colorC) && (colorA == colorF) && (colorB != colorE) && (colorB == colorJ)))
				{
					product = colorA;
				} else product = INTERPOLATE (colorA, colorB);
	
				if (((colorA == colorG) && (colorC == colorO)) || ((colorA == colorB) && (colorA == colorH) && (colorG != colorC) && (colorC == colorM)))
				{
					product1 = colorA;
				} else product1 = INTERPOLATE (colorA, colorC);

				product2 = colorA;
			} 
			else if ((colorB == colorC) && (colorA != colorD))
			{
				if (((colorB == colorF) && (colorA == colorH)) || ((colorB == colorE) && (colorB == colorD) && (colorA != colorF) && (colorA == colorI)))
				{
					product = colorB;
				} else product = INTERPOLATE (colorA, colorB);

				if (((colorC == colorH) && (colorA == colorF)) || ((colorC == colorG) && (colorC == colorD) && (colorA != colorH) && (colorA == colorI)))
				{
					product1 = colorC;
				} else product1 = INTERPOLATE (colorA, colorC);

				product2 = colorB;
			} 
			else if ((colorA == colorD) && (colorB == colorC))
			{
				if (colorA == colorB)
				{
					product = colorA;
					product1 = colorA;
					product2 = colorA;
				} else {
							register int r = 0;

							product1 = INTERPOLATE (colorA, colorC);
							product = INTERPOLATE (colorA, colorB);

							r += GetResult1 (colorA, colorB, colorG, colorE, colorI);
							r += GetResult2 (colorB, colorA, colorK, colorF, colorJ);
							r += GetResult2 (colorB, colorA, colorH, colorN, colorM);
							r += GetResult1 (colorA, colorB, colorL, colorO, colorP);

							if (r > 0) product2 = colorA;
							else if (r < 0) product2 = colorB;
							else product2 = Q_INTERPOLATE (colorA, colorB, colorC, colorD);
						}
			}
			else
			{
				product2 = Q_INTERPOLATE (colorA, colorB, colorC, colorD);

				if ((colorA == colorC) && (colorA == colorF) && (colorB != colorE) && (colorB == colorJ)) product = colorA;
				else if ((colorB == colorE) && (colorB == colorD) && (colorA != colorF) && (colorA == colorI)) product = colorB;
				else product = INTERPOLATE (colorA, colorB);

				if ((colorA == colorB) && (colorA == colorH) && (colorG != colorC) && (colorC == colorM)) product1 = colorA;
				else if ((colorC == colorG) && (colorC == colorD) && (colorA != colorH) && (colorA == colorI)) product1 = colorC;
				else product1 = INTERPOLATE (colorA, colorC);
			}

			product = colorA | (product << 16);
			product1 = product1 | (product2 << 16);
			*((s32 *) dP) = product;
			*((u32 *) (dP + dstPitch)) = product1;

			bP += inc_bP;
			dP += sizeof (u32);
		}

		srcPtr += srcPitch;
		dstPtr += dstPitch * 2;
		deltaPtr += srcPitch;
	}
}
