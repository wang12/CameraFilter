package com.letv.recorder.video;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.letv.filter.common.base.GPUImageFilter;
import com.letv.filter.common.base.MagicCameraInputFilter;
import com.letv.filter.helper.MagicFilterAdjuster;
import com.letv.filter.helper.MagicFilterFactory;
import com.letv.filter.helper.MagicFilterParam;
import com.letv.filter.helper.MagicFilterType;
import com.letv.filter.utils.OpenGLUtils;
import com.letv.filter.utils.Rotation;
import com.letv.filter.utils.TextureRotationUtil;
import com.letv.recorder.video.MagicDisplay.Callback;

public class DisplaySurfaceView {
	private Context mContext;
	private SurfaceHolder surfaceHolder;
	protected GPUImageFilter mFilters;
	private MagicFilterAdjuster mFilterAdjust;
	protected final FloatBuffer mGLCubeBuffer;
	protected final FloatBuffer mGLTextureBuffer;
	private final MagicCameraInputFilter mCameraInputFilter;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private int mImageWidth;
	private int mImageHeight;
	protected int mTextureId = OpenGLUtils.NO_TEXTURE;
	private SurfaceTexture mSurfaceTexture;
	private CircularEncoder mCircEncoder;
	private WindowSurface mEncoderSurface;
//	private WindowSurface mDisplaySurface;
	private EglCore mEglCore;
	
	
	public DisplaySurfaceView(Context context,SurfaceHolder surfaceHolder){
		this.mContext = context;
		this.surfaceHolder = surfaceHolder;
		mFilters = MagicFilterFactory.getFilters(MagicFilterType.NONE, context);
		mFilterAdjust = new MagicFilterAdjuster(mFilters);
		
		mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        mCameraInputFilter = new MagicCameraInputFilter();
	}
	/**
	 * 设置滤镜
	 * @param 参数类型
	 */
	public void setFilter(final int filterType) {
		if (mFilters != null)
			mFilters.destroy();
		mFilters = MagicFilterFactory.getFilters(filterType, mContext);
		mFilters.init();
		mFilters.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
		mFilters.onOutputSizeChanged(mImageWidth, mImageHeight);
		mCameraInputFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
		mCameraInputFilter.initCameraFrameBuffer(mImageWidth, mImageHeight);
		mFilterAdjust = new MagicFilterAdjuster(mFilters);
	}
	
	protected void deleteTextures() {
		if (mTextureId != OpenGLUtils.NO_TEXTURE)
			GLES20.glDeleteTextures(1, new int[] { mTextureId }, 0);
		mTextureId = OpenGLUtils.NO_TEXTURE;
		if (OpenGLUtils.mFrameBuffers != null) {
			GLES20.glDeleteFramebuffers(1, OpenGLUtils.mFrameBuffers, 0);
		}
		if (OpenGLUtils.mRendererBuffers != null) {
			GLES20.glDeleteRenderbuffers(1, OpenGLUtils.mRendererBuffers, 0);
		}
	}
	
	public void adjustFilter(int percentage){
		if(mFilterAdjust != null && mFilterAdjust.canAdjust()){
			mFilterAdjust.adjust(percentage);
		}
	}
	public int getTextureId(){
		return mTextureId;
	}
	public void setTextureId(int textureId){
		this.mTextureId = textureId;
	}
	public SurfaceTexture getSurfaceTexture() {
		if (mTextureId == OpenGLUtils.NO_TEXTURE) {
			mTextureId = OpenGLUtils.getExternalOESTextureID();
		}
		mSurfaceTexture = new SurfaceTexture(mTextureId);
		mSurfaceTexture.setOnFrameAvailableListener(new OnFrameAvailableListener() {

					@Override
					public void onFrameAvailable(SurfaceTexture surfaceTexture) {
						onDrawFrame();
					}
				});
		return mSurfaceTexture;
	}
	
	public void onCreate(){
		GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0,0,0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
//        MagicFilterParam.initMagicFilterParam(gl);
        mCameraInputFilter.init();
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
        try {
            mCircEncoder = new CircularEncoder(mImageWidth, mImageHeight, 6000000, CameraManager.getCManager().chooseFixedPreviewFps(15 * 1000) / 1000, 7);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mEncoderSurface = new WindowSurface(mEglCore, mCircEncoder.getInputSurface(), true);
	}
	public void onSurfaceChanged(int width,int height){
		GLES20.glViewport(0, 0, width, height);
		mSurfaceWidth = width;
		mSurfaceHeight = height;
		mCameraInputFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
		if(mFilters != null){
			mFilters.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
			mFilters.onOutputSizeChanged(mImageWidth, mImageHeight);
			mCameraInputFilter.initCameraFrameBuffer(mImageWidth, mImageHeight);
		}
		else  // 无滤镜情况，下面代码都没有走
			mCameraInputFilter.destroyFramebuffers();
	}
	private void onDrawFrame(){
		Log.d("MagicDisplay", "绘制画面");
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);	
		mSurfaceTexture.updateTexImage();
		float[] mtx = new float[16];
		mSurfaceTexture.getTransformMatrix(mtx);
		mCameraInputFilter.setTextureTransformMatrix(mtx);
		if(mFilters == null){
			mCameraInputFilter.onDrawFrame(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
		}else{
			int textureID = mCameraInputFilter.onDrawToTexture(mTextureId);	
			mFilters.onDrawFrame(textureID, mGLCubeBuffer, mGLTextureBuffer);
		}
		runnable.run();
	}

	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			if (mEglCore == null) {
		        Log.d("MagicDisplay", "Skipping drawFrame after shutdown");
		        return;
		    }
			mEncoderSurface.makeCurrent();
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);	
			float[] mtx = new float[16];
			mCameraInputFilter.setTextureTransformMatrix(mtx);
			if(mFilters == null){
				mCameraInputFilter.onDrawFrame(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
			}else{
				int textureID = mCameraInputFilter.onDrawToTexture(mTextureId);	
				mFilters.onDrawFrame(textureID, mGLCubeBuffer, mGLTextureBuffer);
			}
			mCircEncoder.frameAvailableSoon();
			mEncoderSurface.setPresentationTime(mSurfaceTexture.getTimestamp());
			mEncoderSurface.swapBuffers();
	          
		}
	};
	
	
	public void setSizeOrOrientation(final Size size, final int orientation,final boolean isHorizontal) {
		if (orientation == 90 || orientation == 270) {
			mImageWidth = size.height;
			mImageHeight = size.width;
		} else {
			mImageWidth = size.width;
			mImageHeight = size.height;
		}
		mCameraInputFilter.onOutputSizeChanged(mImageWidth, mImageHeight);
		adjustPosition(orientation, isHorizontal);
	}
	
	private void adjustPosition(int orientation, boolean flipHorizontal) {
        Rotation mRotation = Rotation.fromInt(orientation);
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, flipHorizontal, !flipHorizontal);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }	

	private Handler handler = new Handler();
	public void onPause(){
	    if (mCircEncoder != null) {
            mCircEncoder.shutdown();
            mCircEncoder = null;
        }
	    if (mEncoderSurface != null) {
	    	mEncoderSurface.release();
	    	mEncoderSurface = null;
        }
	    if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
	}
	
	

}
