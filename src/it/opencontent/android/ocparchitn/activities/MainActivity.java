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
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static PendingIntent pi;
	private static NfcAdapter nfca;
	private static IntentFilter[] ifa;
	private static String[][] techListsArray;
	private static Bitmap mImageBitmap;

	private OCParchiDB db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		parseIntent(intent);
		db = new OCParchiDB(getApplicationContext());
		int pending = db.getPendingSynchronizations();

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> locationProviders = locationManager.getProviders(true);

		setContentView(R.layout.activity_main);
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
		techListsArray = new String[][] { new String[] { NfcF.class.getName(),
				MifareUltralight.class.getName(), MifareClassic.class.getName() } };

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

		if (mImageBitmap != null) {
			ImageView mImageView = (ImageView) findViewById(R.id.snapshot);
			mImageView.setImageBitmap(mImageBitmap);
		}
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
			int id = Integer.parseInt(actualValues[1]);

			getStructureData(id);

			String name = "Id: " + id;
			String ser = "Originale: " + out;

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
		Intent customCamera = new Intent(
				"it.opencontent.android.ocparchitn.Intents.TAKE_SNAPSHOT");
		customCamera.setClass(getApplicationContext(), CameraActivity.class);
		Log.d(TAG, customCamera.getAction());
		startActivityForResult(customCamera, FOTO_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int returnCode,
			Intent intent) {
		switch (requestCode) {
		case SOAP_REQUEST_CODE:
			HashMap<String, Object> res = SynchroSoapActivity.getRes();
			TextView externalData = (TextView) findViewById(R.id.external_data_out);
			externalData.setText("");
			if (res != null) {
				Iterator<Entry<String, Object>> i = res.entrySet().iterator();
				while (i.hasNext()) {
					Entry<String, Object> e = i.next();
					if (e.getValue() != null) {
						externalData.append("\n" + e.getKey() + " "
								+ e.getValue().toString());
					} else {
						externalData.append("\n" + e.getKey());
					}

				}
			}

			break;
		case FOTO_REQUEST_CODE:
			try {
				mImageBitmap = CameraActivity.getImage();
				if (mImageBitmap != null) {
					ImageView mImageView = (ImageView) findViewById(R.id.snapshot);
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

	public void getStructureData(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Intents.EXTRAKEY_METHOD_NAME, "getGioco");
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rfid", id);
		serviceIntent.putExtra(Intents.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, SOAP_REQUEST_CODE);
	}

}