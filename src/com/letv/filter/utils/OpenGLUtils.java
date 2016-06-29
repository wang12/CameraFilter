package com.letv.filter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class OpenGLUtils {
	public static final int NO_TEXTURE = -1;
	public static final int NOT_INIT = -1;	
	public static final int ON_DRAWN = 1;
	public static int[] mFrameBuffers = new int[1];
	public static int[] mRendererBuffers = new int[1];
	
    public static int loadTexture(final Context context, final String name){
		final int[] textureHandle = new int[1];
		
		GLES20.glGenTextures(1, textureHandle, 0);
		
		if (textureHandle[0] != 0){

			// Read in the resource
			final Bitmap bitmap = getImageFromAssetsFile(context,name);
						
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();						
		}
		
		if (textureHandle[0] == 0){
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];
	}
	
	private static Bitmap getImageFromAssetsFile(Context context,String fileName){  
		Bitmap image = null;  
	    AssetManager am = context.getResources().getAssets();
	    try{  
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
          	}catch (IOException e){  
	          e.printStackTrace();  
	      }  	  
	      return image;  	  
	}  
    
	public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }
        // 创建一个着色器程序对象
        iProgId = GLES20.glCreateProgram();
        
        // 向着色器程序对象中附加两段程序
        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);
        
        // 连接着色器对象
        GLES20.glLinkProgram(iProgId);
        // 检测链接是否成功
        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        // 删除之前创建的着色器
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }
	
	private static int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        // 创建shader类型
        int iShader = GLES20.glCreateShader(iType);
        // 加载shader代码
        GLES20.glShaderSource(iShader, strSource);
        // 编译着色器代码
        GLES20.glCompileShader(iShader);
        // 判断编译是否成功
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }
	
	public static int getExternalOESTextureID(){		
		int[] texture = new int[1];

		GLES20.glGenTextures(1, texture, 0);
		 GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
		 GLES20.glGenRenderbuffers(1, mRendererBuffers, 0);
//		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
//		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);        
//		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		 GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
		  GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		  GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		  GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		  GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		  GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
		  GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRendererBuffers[0]);
		  GLES20.glRenderbufferStorage(GLES20.GL_FRAMEBUFFER, GLES20.GL_RGBA, 640, 480);
		  GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D, texture[0], 0);
		  GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, mRendererBuffers[0]);
		  //		  GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//		  GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		  
       return texture[0];
	}
	
	public static String readShaderFromRawResource(final Context context,final int resourceId){
		final InputStream inputStream = context.getResources().openRawResource(resourceId);
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String nextLine;
		final StringBuilder body = new StringBuilder();

		try{
			while ((nextLine = bufferedReader.readLine()) != null){
				body.append(nextLine);
				body.append('\n');
			}
		}
		catch (IOException e){
			return null;
		}
		return body.toString();
	}
}
