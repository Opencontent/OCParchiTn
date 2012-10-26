package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.layouts.CameraSurfaceView;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

public class CameraActivity extends BaseActivity {

	private static final String TAG = CameraActivity.class.getSimpleName();

	private static Bitmap mImageBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.customcamera_main);

		// Setup the FrameLayout with the Camera Preview Screen
		final CameraSurfaceView cameraSurfaceView = new CameraSurfaceView(this);
		FrameLayout preview = (FrameLayout) findViewById(R.id.preview);
		preview.addView(cameraSurfaceView);
		preview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "CustomCamera surfaceview cliccata");
				Camera camera = cameraSurfaceView.getCamera();
				if (camera != null) {
					camera.takePicture(null, null,
							new Camera.PictureCallback() {

								@Override
								public void onPictureTaken(byte[] data,
										Camera camera) {
									System.out.println("data length="
											+ data.length);

									BitmapFactory.Options options = new BitmapFactory.Options();
									options.inSampleSize = 4; // 1/4 of the
																// original
																// image
									mImageBitmap = BitmapFactory
											.decodeByteArray(data, 0,
													data.length, options);
									int tempW = mImageBitmap.getWidth();
									int tempH = mImageBitmap.getHeight();

									Matrix mtx = new Matrix();
									mtx.postRotate(cameraSurfaceView
											.getOrientation() - 90);
									mImageBitmap = Bitmap.createBitmap(
											mImageBitmap, 0, 0, tempW, tempH,
											mtx, true);
									// mImageBitmap =
									// BitmapFactory.decodeByteArray(data, 0,
									// data.length);

									sendBackSnapshot();
								}
							});
				}

			}
		});
	}

	public void sendBackSnapshot() {
		Intent result = new Intent();
		setResult(RESULT_OK, result);
		finish();
	}

	public static Bitmap getImage() {
		return mImageBitmap;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}