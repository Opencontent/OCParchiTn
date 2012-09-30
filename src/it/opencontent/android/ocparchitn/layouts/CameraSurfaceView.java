package it.opencontent.android.ocparchitn.layouts;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	
	private SurfaceHolder holder;
	private Camera camera;

	public Camera getCamera(){
		return camera;
	}
	public CameraSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//Initiate the Surface Holder properly
        this.holder = this.getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		// Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(width, height);
        camera.setParameters(parameters);
        camera.startPreview();
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try
        {
                //Open the Camera in preview mode
                this.camera = Camera.open();
                this.camera.setPreviewDisplay(this.holder);
        }
        catch(IOException ioe)
        {
                ioe.printStackTrace(System.out);
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
        camera.release();
        camera = null;
	}

	

}
