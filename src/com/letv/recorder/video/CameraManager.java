package com.letv.recorder.video;

import java.io.IOException;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;

public class CameraManager {
	private final static String TAG = "CameraManager";
	private Camera camera;
	private final static int camWidth = 640;
	private final static int camHeight = 480;
	private int cameraId = 0;
	private byte[] data;
	private boolean isRun = false;
	
	public static CameraManager cManager;
	public synchronized static CameraManager getCManager(){
		if(cManager == null){
			synchronized (CameraManager.class) {
				cManager = new CameraManager();
			}
		}
		return cManager;
	}
	private CameraManager() {
	}
	public int init(){
		if(camera == null){
			camera = Camera.open(cameraId);
			Camera.Parameters parameters = camera.getParameters();
			parameters.setFlashMode("off"); // 无闪光灯
			parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			parameters.setPreviewFormat(ImageFormat.YV12);
			parameters.setPictureSize(camWidth, camHeight);
			parameters.setPreviewSize(camWidth, camHeight);
			camera.setParameters(parameters);
			data = new byte[camWidth * camHeight *3/2];
			Log.d(TAG, "初始化Camera");
			return 0;
		}else{
			Log.e(TAG, "Camera还没有关闭！所以无法打开");
		}
		return -1;
	}
	public int open(SurfaceTexture surfaceTexture,boolean isLandscape){
		camera.addCallbackBuffer(data);
		setCameraDisplayOrientation(isLandscape);
		try {
			camera.setPreviewTexture(surfaceTexture);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		camera.setPreviewCallbackWithBuffer(new PreviewCallback() {
			
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				camera.addCallbackBuffer(data);
			}
		});
		camera.startPreview();
		isRun = true;
		Log.d(TAG, "打开Camera");
		return 0;
	}
	
	public int close(){
		if(camera ==  null) return 1;
		try {
			camera.setPreviewTexture(null);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		camera.release();
		camera = null;
		isRun = false;
		Log.d(TAG, "关闭并销毁Camera");
		return 0;
		
	}

	private void setCameraDisplayOrientation(boolean  isLandscape) {
		CameraInfo info = new CameraInfo();
        camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        int result;
        if (!isLandscape) {
            degrees = -180;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            camera.setDisplayOrientation((360 - result) % 360);
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
            camera.setDisplayOrientation(result);
        }
 }
	
}
