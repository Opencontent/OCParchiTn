package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.services.SOAPService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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
	private Pattern p = Pattern.compile("ID:\\d+");
	private Pattern p2 = Pattern.compile("SER:[\\p{L}0-9-_ :]+");

	private OCParchiDB db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new OCParchiDB(getApplicationContext());
		int pending = db.getPendingSynchronizations();

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> locationProviders = locationManager.getProviders(true);

		setContentView(R.layout.activity_main);
		nfca = NfcAdapter.getDefaultAdapter(this);
		pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataType("*/*"); /*
									 * Handles all MIME based dispatches. You
									 * should specify only the ones that you
									 * need.
									 */
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		ifa = new IntentFilter[] { ndef, };
		techListsArray = new String[][] { new String[] { NfcF.class.getName(),
				MifareUltralight.class.getName(), MifareClassic.class.getName() } };

//		getTestSOAPRequest();

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
		String res = "";
		MifareUltralight mifare = MifareUltralight.get((Tag) intent
				.getParcelableExtra(NfcAdapter.EXTRA_TAG));
		byte[] payload;
		byte[] payload2;
		byte[] payload3;

		try {
			mifare.connect();
			payload = mifare.readPages(4);
			payload2 = mifare.readPages(8);
			payload3 = mifare.readPages(12);

			// return new String(payload, Charset.forName("US-ASCII"));
			res = new String(payload, Charset.forName("UTF-8"));
			res += new String(payload2, Charset.forName("UTF-8"));
			res += new String(payload3, Charset.forName("UTF-8"));
		} catch (IOException e) {
			Log.e(TAG, "IOException while writing MifareUltralight message...",
					e);
		} finally {
			if (mifare != null) {
				try {
					mifare.close();
				} catch (IOException e) {
					Log.e(TAG, "Error closing tag...", e);
				}
			}
		}

		Matcher m = p.matcher(res);
		Matcher m2 = p2.matcher(res);
		String name = "";
		String ser = "";
		while (m.find()) { // Find each match in turn; String can't do this.
			name = m.group(); // Access a submatch group; String can't do this.
		}
		while (m2.find()) { // Find each match in turn; String can't do this.
			ser = m2.group(); // Access a submatch group; String can't do this.
		}

		TextView giocoId = (TextView) findViewById(R.id.display_gioco_id);
		giocoId.setText(name);
		TextView giocoSeriale = (TextView) findViewById(R.id.display_gioco_seriale);
		giocoSeriale.setText(ser);

		Log.d(TAG, "Qualcosa è successo " + name + " " + res);
		// do something with tagFromIntent
	}

	public void takeSnapshot(View button) {
		Intent customCamera = new Intent(
				"it.opencontent.android.ocparchitn.Intents.TAKE_SNAPSHOT");
		customCamera.setClass(getApplicationContext(), CameraActivity.class);
		Log.d(TAG, customCamera.getAction());
		startActivityForResult(customCamera, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int returnCode,
			Intent intent) {
		try {
			mImageBitmap = CameraActivity.getImage();
			if (mImageBitmap != null) {
				ImageView mImageView = (ImageView) findViewById(R.id.snapshot);
				mImageView.setImageBitmap(mImageBitmap);
			}
		} catch (NullPointerException e) {
			Log.d(TAG, "Immagine nulla");
		}
	}

	public void getTestSOAPRequest() {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(), SOAPService.class);
		startService(serviceIntent);
	}

}