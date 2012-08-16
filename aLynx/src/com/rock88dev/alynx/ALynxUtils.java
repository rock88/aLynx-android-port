/** aLynx - Atari Lynx emulator for Android OS
 * 
 * Copyright (C) 2012
 * @author: rock88
 * 
 * e-mail: rock88a@gmail.com
 * 
 * http://rock88dev.blogspot.com
 * 
 */

package com.rock88dev.alynx;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;


public final class ALynxUtils {

        public static Bitmap getTextureFromBitmapResource(Context context, String resourceId)
        {
            Bitmap bitmap = null;
	
    		try {
				InputStream is = context.getAssets().open(resourceId);
				bitmap = BitmapFactory.decodeStream(is);
			} catch (IOException e) {
				Log.d("ALYNX","can`t open "+resourceId);
			}

            return bitmap;     
        }       

        public static void generateMipmapsForBoundTexture(Bitmap texture)
        {
                // generate the full texture (mipmap level 0)
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
                
                Bitmap currentMipmap = texture;
                
                int width = texture.getWidth();
                int height = texture.getHeight();
                int level = 0;
                
                boolean reachedLastLevel;
                do {
                        
                        // go to next mipmap level
                        if (width > 1) width /= 2;
                        if (height > 1) height /= 2;
                        level++;
                        reachedLastLevel = (width == 1 && height == 1);
                        
                        // generate next mipmap
                        Bitmap mipmap = Bitmap.createScaledBitmap(currentMipmap, width, height, true);
                        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, mipmap, 0);
                        
                        // recycle last mipmap (but don't recycle original texture)
                        if (currentMipmap != texture)
                        {
                                currentMipmap.recycle();
                        }
                        
                        // remember last generated mipmap
                        currentMipmap = mipmap;
                        
                } while (!reachedLastLevel);
                
                // once again, recycle last mipmap (but don't recycle original texture)
                if (currentMipmap != texture)
                {
                        currentMipmap.recycle();
                }
        }
        
}
