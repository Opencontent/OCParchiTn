package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends Activity {

	public static final int FOTO_REQUEST_CODE = 1;
	public static final int SOAP_GET_GIOCO_REQUEST_CODE = 2;
	public static final int SOAP_GET_GIOCO_REQUEST_CODE_BY_ID = 101;
	public static final int SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID = 102;
	public static final int SOAP_GET_GIOCO_FOTO_REQUEST_CODE = 3;
	public static final int SOAP_SINCRONIZZA_TUTTO_REQUEST_CODE = 90;
	public static final int SOAP_SERVICE_INFO_REQUEST_CODE = 100;

	public static final int SETUP_NETWORK = 100;

	public static boolean networkIsAvailable = false;

	protected static int currentRFID = 0;

	private final static String TAG = BaseActivity.class.getSimpleName();


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.menu_item_manage_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedinstance) {
		super.onCreate(savedinstance);
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
				.getSystemService(CONNECTIVITY_SERVICE);
		networkIsAvailable = cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();

		

	}



	public boolean getNetworkIsAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
				.getSystemService(CONNECTIVITY_SERVICE);
		networkIsAvailable = cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
		return networkIsAvailable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public int getCurrentRfid() {
		return currentRFID;
	}



}
