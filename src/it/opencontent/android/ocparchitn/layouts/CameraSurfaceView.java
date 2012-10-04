package it.opencontent.android.ocparchitn.layouts;

import it.opencontent.android.ocparchitn.R;

import java.io.IOException;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = CameraSurfaceView.class.getSimpleName();
	private SurfaceHolder holder;
	private Camera camera;
	private int numberOfCameras;

	public Camera getCamera() {
		return camera;
	}

	public CameraSurfaceView(Context context) {
		super(context);
		numberOfCameras = Camera.getNumberOfCameras();
		Log.d(TAG,"Trovate: "+numberOfCameras+" fotocamere");
		this.holder = this.getHolder();
		this.holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if(camera != null){

			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(numberOfCameras - 1, cameraInfo);
		     int degrees = 0;
		     int orientation = ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
		     switch (orientation) {
		         case Surface.ROTATION_0: degrees = 0; break;
		         case Surface.ROTATION_90: degrees = 90; break;
		         case Surface.ROTATION_180: degrees = 180; break;
		         case Surface.ROTATION_270: degrees = 270; break;

		     }

		     int result;
		     if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
		         result = (cameraInfo.orientation + degrees) % 360;
		         result = (360 - result) % 360;  // compensate the mirror
		     } else {  // back-facing
		         result = (cameraInfo.orientation - degrees + 360) % 360;
		     }
		     camera.setDisplayOrientation(result);
		     
			Camera.Parameters parameters = camera.getParameters();
			
			int cameraPictureHeight =parameters.getPictureSize().height;  
			int cameraPictureWidth =parameters.getPictureSize().width;  
			
			if(cameraPictureHeight < height){
				height = cameraPictureHeight; 
			}
			if(cameraPictureWidth < width){
				width = cameraPictureWidth; 
			}
			parameters.setPreviewSize(width, height);
			// camera.setParameters(parameters);
			camera.startPreview();
		} else {
			warnCameraNotAvailable();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		// Open the Camera in preview mode

		enablePreview();

	}

	private void enablePreview() {
		try {
			this.camera = Camera.open(numberOfCameras - 1);
			this.camera.setPreviewDisplay(this.holder);
		} catch (IOException ioe) {
			ioe.printStackTrace(System.out);
		} catch (RuntimeException e) {
			// Camera occupata
			Log.d(TAG, "camera occupata causa crash precedente");
			warnCameraNotAvailable();
		}
	}

	private void warnCameraNotAvailable(){
		Toast toast = Toast.makeText(getContext(), R.string.camera_unavailable, Toast.LENGTH_SHORT);
		toast.show();		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if(camera != null){
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		
	}

	public void takePictureAsRaw() {
		CustomCameraCallback raw = new CustomCameraCallback(); 
		camera.takePicture(null, null, raw);
		//return raw.getRawData();
	}
	
	class CustomCameraCallback implements Camera.PictureCallback{
		private byte[] rawdata;

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			rawdata = data;
			Intent result = new Intent();
			result.putExtra("snapshot", rawdata);
//			setResult(RESULT_OK, result);
//			finish();
		}
		public byte[] getRawData(){
			return rawdata;
		}
	}
	

}
