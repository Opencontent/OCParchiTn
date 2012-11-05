package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Intents;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.db.OCParchiDB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static PendingIntent pi;
	private static NfcAdapter nfca;
	private static IntentFilter[] ifa;
	private static String[][] techListsArray;
	private static Bitmap mImageBitmap;
	private static int currentRFID = 0;
	private static Bitmap[] snapshots = new Bitmap[Intents.MAX_SNAPSHOTS_AMOUNT];
	
	private static boolean serviceInfoTaken = false;
	private static HashMap<String, Object> serviceInfo;
	

	private OCParchiDB db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = new OCParchiDB(getApplicationContext());

		Intent intent = getIntent();
		parseIntent(intent);


		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> locationProviders = locationManager.getProviders(true);

		
		int pending = db.getPendingSynchronizations();
		TextView pending_text = (TextView) findViewById(R.id.pending_synchronizations);
		pending_text.setText(pending+" pending");		
		
		
		nfca = NfcAdapter.getDefaultAdapter(this);
		pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
				PendingIntent.FLAG_CANCEL_CURRENT);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataScheme(getString(R.string.schema_struttura));
			ndef.addDataAuthority(getString(R.string.host_struttura), null);
		} catch (Exception e) {
			throw new RuntimeException("fail", e);
		}
		ifa = new IntentFilter[] { ndef, };
		
		
		if(!serviceInfoTaken){
			getServiceInfo();
			serviceInfoTaken = true;
		}
		// La techListArray per il momento la tengo vuota, così filtro per
		// qualsiasi
		// TODO: definire un set di techList specifiche e corrette per il
		// progetto
	}

	@Override
	public void onPause() {
		super.onPause();
		nfca.disableForegroundDispatch(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		nfca.enableForegroundDispatch(this, pi, ifa, techListsArray);
		setupSnapshots();
	}

	@Override
	public void onNewIntent(Intent intent) {
		parseIntent(intent);
		// do something with tagFromIntent
	}

	private void parseIntent(Intent intent) {
		try {
			NdefMessage rawMsg = (NdefMessage) intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];

			int length = rawMsg.toByteArray().length;
			byte[] res = new byte[length - 5];
			for (int i = 5; i < length; i++) {
				res[i - 5] = rawMsg.toByteArray()[i];
			}
			String out = new String(res);
			String[] pieces = out.split("://");

			String[] actualValues = pieces[1].split("/");
			currentRFID = Integer.parseInt(actualValues[1]);

			getStructureData(currentRFID);

			String name = getString(R.string.display_gioco_id) + currentRFID;
			String ser = getString(R.string.display_gioco_seriale) + out;

			TextView giocoId = (TextView) findViewById(R.id.display_gioco_id);
			giocoId.setText(name);
			TextView giocoSeriale = (TextView) findViewById(R.id.display_gioco_seriale);
			giocoSeriale.setText(ser);

			Log.d(TAG, "Qualcosa è successo " + name + " " + res);
		} catch (Exception e) {
			// Non è un intent che ci interessa in questo caso
		}
	}

	public void takeSnapshot(View button) {
		Intent customCamera = new Intent(Intents.TAKE_SNAPSHOT);
		int whichOne = (Integer) button.getTag();
		customCamera.putExtra(Intents.EXTRAKEY_FOTO_NUMBER, whichOne);
		customCamera.setClass(getApplicationContext(), CameraActivity.class);
		Log.d(TAG, customCamera.getAction());
		startActivityForResult(customCamera, FOTO_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int returnCode,
			Intent intent) {
		switch (requestCode) {
		case SOAP_GET_GIOCO_REQUEST_CODE:
			HashMap<String, Object> res = SynchroSoapActivity.getRes();

			LinearLayout externalData = (LinearLayout) findViewById(R.id.external_data_out);
			externalData.removeAllViews();
			if (res != null && res.size() > 0) {
				Iterator<Entry<String, Object>> i = res.entrySet().iterator();
				while (i.hasNext()) {
					Entry<String, Object> e = i.next();
					TextView key = new TextView(getApplicationContext());
					key.setText(e.getKey());
					externalData.addView(key);

					if (e.getValue() != null) {
						if (e.getValue().getClass().equals(String.class)) {
							TextView val = new TextView(getApplicationContext());
							val.setText(e.getKey());
							externalData.addView(val);
						} else if (e.getValue().getClass().equals(Button.class)) {
							externalData.addView((Button) e.getValue());
						}

					}

				}
			} else {
				TextView generic = new TextView(getApplicationContext());
				generic.setText(getString(R.string.errore_generico_soap)
						+ currentRFID);
				externalData.addView(generic);

			}

			break;
		case SOAP_SERVICE_INFO_REQUEST_CODE:
			serviceInfo = SynchroSoapActivity.getRes();
			break;
		case FOTO_REQUEST_CODE:
			try {
				mImageBitmap = CameraActivity.getImage();

				int whichOne = intent.getExtras().getInt(
						Intents.EXTRAKEY_FOTO_NUMBER);
				ImageView mImageView =(ImageView) findViewById(whichOne);

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

	private void getServiceInfo() {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Intents.EXTRAKEY_METHOD_NAME, "getInfo");
		startActivityForResult(serviceIntent, SOAP_SERVICE_INFO_REQUEST_CODE);
	}

	private void getStructureData(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Intents.EXTRAKEY_METHOD_NAME, "getGioco");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rfid", id);
		serviceIntent.putExtra(Intents.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, SOAP_GET_GIOCO_REQUEST_CODE);
	}
	
	private void setupSnapshots(){
		LinearLayout wrapper = (LinearLayout) findViewById(R.id.snapshot_wrapper);
		wrapper.removeAllViews();
		
		for(int i =0 ; i < Intents.MAX_SNAPSHOTS_AMOUNT; i++){
			ImageView img = new ImageView(getApplicationContext());
			img.setTag(i);
			img.setId(i);
			img.setLayoutParams(new LayoutParams (150, 150));
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					takeSnapshot(v);
				}
			});
			if(snapshots[i]!=null){
				img.setImageBitmap(snapshots[i]);
			}
			wrapper.addView(img);
		}
		
        /*<ImageView
        android:id="@+id/snapshot1"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:layout_gravity="right"
        android:layout_weight="1"
        android:background="#eeeeee"
        android:contentDescription="@string/snapshot_description"
        android:onClick="takeSnapshot"
        android:tag="1" />*/
		
	}

}