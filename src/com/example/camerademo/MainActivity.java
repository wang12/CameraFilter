package com.example.camerademo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.w3c.dom.Text;

import com.letv.recorder.video.CameraManager;
import com.letv.recorder.video.MagicCameraDisplay;
import com.letv.recorder.video.MagicDisplay.Callback;

import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements Callback{
	private GLSurfaceView glSurfaceView;
	private MagicCameraDisplay cameraDisplay;
	private final static String TAG = "MainActivity";
	private boolean isLandscape  =  false;
	private RelativeLayout glRoot;
	private TextView filterName;
	private int filter = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		glRoot = (RelativeLayout) findViewById(R.id.gl_root);
		filterName = (TextView) findViewById(R.id.filter_name);
		glSurfaceView = new GLSurfaceView(this);
		glSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		glRoot.addView(glSurfaceView);
		
		cameraDisplay =  new MagicCameraDisplay(this, glSurfaceView);
		cameraDisplay.setCallback(this);
		
		findViewById(R.id.add_filter).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cameraDisplay != null){
					cameraDisplay.setFilter(++filter);
					filterName.setText(""+filter);
				}
			}
		});
		findViewById(R.id.sub_filter).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cameraDisplay != null){
					cameraDisplay.setFilter(--filter);
					filterName.setText(""+filter);
				}
			}
		});
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
	}
	@Override
	public void onDrawFrame(GL10 gl) {
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ){
			isLandscape = true;
		}else{
			isLandscape = false;
		}
		CameraManager.getCManager().init();
		CameraManager.getCManager().open(cameraDisplay.getSurfaceTexture(),isLandscape);
	}
	@Override
	protected void onPause() {
		super.onPause();
		CameraManager.getCManager().close();
	}
}
