package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Intents;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.BaseActivity;
import it.opencontent.android.ocparchitn.activities.CameraActivity;
import it.opencontent.android.ocparchitn.activities.SynchroSoapActivity;
import it.opencontent.android.ocparchitn.utils.DrawableOverlayWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainFragment extends Fragment {

	private static final String TAG = MainFragment.class.getSimpleName();
	
	private static Bitmap mImageBitmap;

	private static Bitmap[] snapshots = new Bitmap[Intents.MAX_SNAPSHOTS_AMOUNT];
	
	private static HashMap<String, Object> serviceInfo;
	
	private BaseActivity baseActivity;


	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.activity_main,container,false);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}



	@Override
	public void onResume(){
		super.onResume();
		setupSnapshots();
	}

	

	public void takeSnapshot(View button) {
		Intent customCamera = new Intent(Intents.TAKE_SNAPSHOT);
		int whichOne = (Integer) button.getTag();
		customCamera.putExtra(Intents.EXTRAKEY_FOTO_NUMBER, whichOne);
		customCamera.setClass(getActivity().getApplicationContext(), CameraActivity.class);
		Log.d(TAG, customCamera.getAction());
		startActivityForResult(customCamera, BaseActivity.FOTO_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int returnCode,
			Intent intent) {
		switch (requestCode) {
		case BaseActivity.SOAP_GET_GIOCO_REQUEST_CODE:
			HashMap<String, Object> res = SynchroSoapActivity.getRes();

			LinearLayout externalData = (LinearLayout) getActivity().findViewById(R.id.external_data_out);
			externalData.removeAllViews();
			if (res != null && res.size() > 0) {
				Iterator<Entry<String, Object>> i = res.entrySet().iterator();
				while (i.hasNext()) {
					Entry<String, Object> e = i.next();
					TextView key = new TextView(getActivity().getApplicationContext());
					key.setText(e.getKey());
					externalData.addView(key);

					if (e.getValue() != null) {
						if (e.getValue().getClass().equals(String.class)) {
							TextView val = new TextView(getActivity().getApplicationContext());
							val.setText(e.getKey());
							externalData.addView(val);
						} else if (e.getValue().getClass().equals(Button.class)) {
							externalData.addView((Button) e.getValue());
						}

					}

				}
			} else {
				TextView generic = new TextView(getActivity().getApplicationContext());
				generic.setText(getString(R.string.errore_generico_soap)
						+ baseActivity.getCurrentRfid());
				externalData.addView(generic);

			}

			break;
		case BaseActivity.SOAP_SERVICE_INFO_REQUEST_CODE:
			serviceInfo = SynchroSoapActivity.getRes();
			break;
		case BaseActivity.FOTO_REQUEST_CODE:
			try {
				mImageBitmap = CameraActivity.getImage();

				int whichOne = intent.getExtras().getInt(
						Intents.EXTRAKEY_FOTO_NUMBER);
				ImageView mImageView =(ImageView) getActivity().findViewById(whichOne);

				if (mImageBitmap != null && mImageView != null) {
					snapshots[whichOne] = mImageBitmap;
					mImageView.setImageBitmap(mImageBitmap);
				}
			} catch (NullPointerException e) {
				Log.d(TAG, "Immagine nulla");
			}
			break;
		default:
			break;
		}
	}


	
	private void setupSnapshots(){
		
		LayoutInflater inflater =  (LayoutInflater)getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
		
		
		LinearLayout wrapper = (LinearLayout) getActivity().findViewById(R.id.snapshot_wrapper);
		wrapper.removeAllViews();
		
		for(int i =0 ; i < Intents.MAX_SNAPSHOTS_AMOUNT; i++){
			ImageButton img = (ImageButton) inflater.inflate(R.layout.snapshot_holder, null);
			img.setTag(i);
			img.setId(i);
			
			img.setLayoutParams(new LayoutParams (200, 200));
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					takeSnapshot(v);
				}
			});
			if(snapshots[i]!=null){
				img.setImageBitmap(snapshots[i]);
			} else {
				int j = i+1;
				String text = "Foto "+j;
				Bitmap bmp = new DrawableOverlayWriter().writeOnDrawable(getResources(), R.drawable.snapshot_teaser, text).getBitmap();
				img.setImageBitmap(bmp);
			}
			wrapper.addView(img);
		}
	}

}