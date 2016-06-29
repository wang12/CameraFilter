package com.letv.recorder.video;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

import com.letv.filter.common.base.GPUImageFilter;
import com.letv.filter.helper.MagicFilterAdjuster;
import com.letv.filter.helper.MagicFilterFactory;
import com.letv.filter.helper.MagicFilterType;
import com.letv.filter.utils.OpenGLUtils;
import com.letv.filter.utils.TextureRotationUtil;

public  abstract class MagicDisplay implements Renderer{
	/**
	 * 所选择的滤镜，类型为MagicBaseGroupFilter
	 * 1.mCameraInputFilter将SurfaceTexture中YUV数据绘制到FrameBuffer
	 * 2.mFilters将FrameBuffer中的纹理绘制到屏幕中
	 */
	protected GPUImageFilter mFilters;
	
	/**
	 * 所有预览数据绘制画面
	 */
	protected final GLSurfaceView mGLSurfaceView;
	
	/**
	 * SurfaceTexure纹理id
	 */
	protected int mTextureId = OpenGLUtils.NO_TEXTURE;
	
	/**
	 * 顶点坐标
	 */
	protected final FloatBuffer mGLCubeBuffer;
	
	/**
	 * 纹理坐标
	 */
	protected final FloatBuffer mGLTextureBuffer;
	
	/**
	 * GLSurfaceView的宽高
	 */
	protected int mSurfaceWidth, mSurfaceHeight;
	
	/**
	 * 图像宽高
	 */
	protected int mImageWidth, mImageHeight;
	
	protected Context mContext;
	
	private MagicFilterAdjuster mFilterAdjust;
	
	public MagicDisplay(Context context, GLSurfaceView glSurfaceView){
		mContext = context;
		mGLSurfaceView = glSurfaceView;  
		
		mFilters = MagicFilterFactory.getFilters(MagicFilterType.NONE, context);
		mFilterAdjust = new MagicFilterAdjuster(mFilters);
		
		mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

		mGLSurfaceView.setEGLContextClientVersion(2);
		mGLSurfaceView.setRenderer(this);
		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	/**
	 * 设置滤镜
	 * @param 参数类型
	 */
	public void setFilter(final int filterType) {
		mGLSurfaceView.queueEvent(new Runnable() {
       		
            @Override
            public void run() {
            	if(mFilters != null)
            		mFilters.destroy();
            	mFilters = null;
            	mFilters = MagicFilterFactory.getFilters(filterType, mContext);
//            	if(mFilters != null){
//            		mFilters.setOnOpenGlPreviewCallback(opck);
//            	}
            	if(mFilters != null)
	            	mFilters.init();
            	onFilterChanged();
            	mFilterAdjust = new MagicFilterAdjuster(mFilters);
            }
        });
		mGLSurfaceView.requestRender();
    }
	protected void onFilterChanged(){
		if(mFilters == null)
			return;
		mFilters.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
		mFilters.onOutputSizeChanged(mImageWidth, mImageHeight);
	}
	
	protected void deleteTextures() {
		if(mTextureId != OpenGLUtils.NO_TEXTURE)
			mGLSurfaceView.queueEvent(new Runnable() {
				@Override
				public void run() {
	                GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
	                mTextureId = OpenGLUtils.NO_TEXTURE;
	                if(OpenGLUtils.mFrameBuffers != null){
		                GLES20.glDeleteFramebuffers(1, OpenGLUtils.mFrameBuffers, 0);
	                }
	                if(OpenGLUtils.mRendererBuffers != null){
	                	GLES20.glDeleteRenderbuffers(1, OpenGLUtils.mRendererBuffers,0);
	                }
	            }
	        });
    }
	
	public void adjustFilter(int percentage){
		if(mFilterAdjust != null && mFilterAdjust.canAdjust()){
			mFilterAdjust.adjust(percentage);
			mGLSurfaceView.requestRender();
		}
	}
	public int getTextureId(){
		return mTextureId;
	}
	public void setTextureId(int textureId){
		this.mTextureId = textureId;
	}
	public interface OnPreviewCallback {
		public void onPreviewFrame(byte[] data);
	}
	public interface OnOpenGlPreviewCallback {
		public void onRead(int textureId);
	}
	protected Callback callback;
	public void setCallback(Callback callback){
		this.callback = callback;
	}
	public interface Callback{
		public void onSurfaceCreated(GL10 gl, EGLConfig config);
		public void onSurfaceChanged(GL10 gl, int width, int height);
		public void onDrawFrame(GL10 gl);
	}
}
