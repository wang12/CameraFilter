package com.letv.recorder.video;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;

import com.letv.filter.common.base.MagicCameraInputFilter;
import com.letv.filter.helper.MagicFilterParam;
import com.letv.filter.utils.OpenGLUtils;
import com.letv.filter.utils.Rotation;
import com.letv.filter.utils.TextureRotationUtil;

public class MagicCameraDisplay extends MagicDisplay{
	/**
	 * 用于绘制相机预览数据，当无滤镜及mFilters为Null或者大小为0时，绘制到屏幕中，
	 * 否则，绘制到FrameBuffer中纹理
	 */
	private final MagicCameraInputFilter mCameraInputFilter;
	
	/**
	 * Camera预览数据接收层，必须和OpenGL绑定
	 * 过程见{@link OpenGLUtils.getExternalOESTextureID()};
	 */
	private SurfaceTexture mSurfaceTexture;
	private CircularEncoder mCircEncoder;
	private MainHandler mHandler;
	
	public MagicCameraDisplay(Context context, GLSurfaceView glSurfaceView){
		super(context, glSurfaceView);
		mCameraInputFilter = new MagicCameraInputFilter();
		 mHandler = new MainHandler();
	}

	public SurfaceTexture getSurfaceTexture() {
		if (mTextureId == OpenGLUtils.NO_TEXTURE) {
			mTextureId = OpenGLUtils.getExternalOESTextureID();
		}
		mSurfaceTexture = new SurfaceTexture(mTextureId);
		mSurfaceTexture.setOnFrameAvailableListener(new OnFrameAvailableListener() {

					@Override
					public void onFrameAvailable(SurfaceTexture surfaceTexture) {
						mGLSurfaceView.requestRender();
						mCircEncoder.frameAvailableSoon();
					}
				});
		return mSurfaceTexture;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// 禁用颜色抖动，据说会影响性能
		GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0,0,0);
        //打开忽略后面的设置
        GLES20.glEnable(GL10.GL_CULL_FACE);
        // 开启颜色深度模式，又A决定
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        // 只是选择了GPU类型
        MagicFilterParam.initMagicFilterParam(gl);
        //初始化滤镜
        mCameraInputFilter.init();
        if(callback != null){
        	callback.onSurfaceCreated(gl, config);
        }
        try {
            mCircEncoder = new CircularEncoder(mImageWidth, mImageHeight, 6000000, CameraManager.getCManager().chooseFixedPreviewFps(15 * 1000) / 1000, 7, mHandler);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
//        magicRGB.init();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// 改变显示窗口的大小
		GLES20.glViewport(0, 0, width, height);
		mSurfaceWidth = width;
		mSurfaceHeight = height;
		// 设置size大小
		onFilterChanged();
		if(callback != null){
			callback.onSurfaceChanged(gl, width, height);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
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
		if(callback != null){
			callback.onDrawFrame(gl);  
		}
	}
	
	
	
	public void setSizeOrOrientation(final Size size,final int orientation,final boolean isHorizontal){
		mGLSurfaceView.queueEvent(new Runnable() {
			
			@Override
			public void run() {
				if(orientation == 90 || orientation == 270){
    				mImageWidth = size.height;
    				mImageHeight = size.width;
    			}else{
    				mImageWidth = size.width;
    				mImageHeight = size.height;
    			} 
				mCameraInputFilter.onOutputSizeChanged(mImageWidth, mImageHeight);
				adjustPosition(orientation, isHorizontal);
			}
		});
	}
	
	protected void onFilterChanged(){
		//有滤镜情况下，对滤镜的输出size和显示size设置大小？？？ 暂时不看这部分内容
		super.onFilterChanged();
		// 对默认情况下的显示size 设置大小
		mCameraInputFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
		if(mFilters != null) // 有滤镜情况，暂时不看有滤镜情况
			mCameraInputFilter.initCameraFrameBuffer(mImageWidth, mImageHeight);
		else  // 无滤镜情况，下面代码都没有走
			mCameraInputFilter.destroyFramebuffers();
	}
	
	private void adjustPosition(int orientation, boolean flipHorizontal) {
        Rotation mRotation = Rotation.fromInt(orientation);
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, flipHorizontal, !flipHorizontal);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }	

	public void onPause(){
	    if (mCircEncoder != null) {
            mCircEncoder.shutdown();
            mCircEncoder = null;
        }
	}
	
	
	
	
	
	private static class MainHandler extends Handler implements CircularEncoder.Callback {
        public static final int MSG_BLINK_TEXT = 0;
        public static final int MSG_FRAME_AVAILABLE = 1;
        public static final int MSG_FILE_SAVE_COMPLETE = 2;
        public static final int MSG_BUFFER_STATUS = 3;

//        private WeakReference<ContinuousCaptureActivity> mWeakActivity;

        public MainHandler() {
//            mWeakActivity = new WeakReference<ContinuousCaptureActivity>(activity);
        }

        // CircularEncoder.Callback, called on encoder thread
        @Override
        public void fileSaveComplete(int status) {
            sendMessage(obtainMessage(MSG_FILE_SAVE_COMPLETE, status, 0, null));
        }

        // CircularEncoder.Callback, called on encoder thread
        @Override
        public void bufferStatus(long totalTimeMsec) {
            sendMessage(obtainMessage(MSG_BUFFER_STATUS,
                    (int) (totalTimeMsec >> 32), (int) totalTimeMsec));
        }


        @Override
        public void handleMessage(Message msg) {
//            ContinuousCaptureActivity activity = mWeakActivity.get();
//            if (activity == null) {
//                Log.d(TAG, "Got message for dead activity");
//                return;
//            }

//            switch (msg.what) {
//                case MSG_BLINK_TEXT: {
//                    TextView tv = (TextView) activity.findViewById(R.id.recording_text);
//
//                    // Attempting to make it blink by using setEnabled() doesn't work --
//                    // it just changes the color.  We want to change the visibility.
//                    int visibility = tv.getVisibility();
//                    if (visibility == View.VISIBLE) {
//                        visibility = View.INVISIBLE;
//                    } else {
//                        visibility = View.VISIBLE;
//                    }
//                    tv.setVisibility(visibility);
//
//                    int delay = (visibility == View.VISIBLE) ? 1000 : 200;
//                    sendEmptyMessageDelayed(MSG_BLINK_TEXT, delay);
//                    break;
//                }
//                case MSG_FRAME_AVAILABLE: {
//                    activity.drawFrame();
//                    break;
//                }
//                case MSG_FILE_SAVE_COMPLETE: {
//                    activity.fileSaveComplete(msg.arg1);
//                    break;
//                }
//                case MSG_BUFFER_STATUS: {
//                    long duration = (((long) msg.arg1) << 32) |
//                                    (((long) msg.arg2) & 0xffffffffL);
//                    activity.updateBufferStatus(duration);
//                    break;
//                }
//                default:
//                    throw new RuntimeException("Unknown message " + msg.what);
//            }
        }
    }
	
}
