package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.services.SOAPService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
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
    
    private NdefMessage[] msgs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		nfca = NfcAdapter.getDefaultAdapter(this);
		pi = PendingIntent.getActivity (this, 0, new Intent(this, getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_CANCEL_CURRENT);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndef.addDataScheme(getString(R.string.schema_struttura));
			ndef.addDataAuthority(getString(R.string.host_struttura), null);
		} catch (Exception e) {
			throw new RuntimeException("fail", e);
		}
		ifa = new IntentFilter[] { ndef, };
		techListsArray = new String[][] { new String[] { NfcF.class.getName(), MifareUltralight.class.getName(), MifareClassic.class.getName() } };

		getTestSOAPRequest();
		
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
		NdefMessage rawMsg = (NdefMessage) intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];
		int length = rawMsg.toByteArray().length;
		byte[] res = new byte[length-5];
		for(int i = 5; i <  length; i++){
			res[i-5] =  rawMsg.toByteArray()[i];
		}
		String out = new String(res);
		String[] pieces = out.split("://");
		
		String[] actualValues = pieces[1].split("/");
		String id = actualValues[1];
			
		String name = "Id: "+id;
		String ser = "Originale: "+out;
        
        TextView giocoId = (TextView) findViewById(R.id.display_gioco_id);
        giocoId.setText(name);
        TextView giocoSeriale = (TextView) findViewById(R.id.display_gioco_seriale);
        giocoSeriale.setText(ser);
        
	}

	public void takeSnapshot(View button) {
		Intent customCamera = new Intent("it.opencontent.android.ocparchitn.Intents.TAKE_SNAPSHOT");
		customCamera.setClass(getApplicationContext(), CameraActivity.class);
		Log.d(TAG,customCamera.getAction());
		startActivityForResult(customCamera,0);
	}

	@Override
	protected void onActivityResult(int requestCode, int returnCode,
			Intent intent) {
		try {
			mImageBitmap = CameraActivity.getImage();
			if(mImageBitmap != null){
			ImageView mImageView = (ImageView) findViewById(R.id.snapshot);
			mImageView.setImageBitmap(mImageBitmap);
			}
		} catch (NullPointerException e) {
			Log.d(TAG, "Immagine nulla");
		}
	}





	
	public void getTestSOAPRequest() {
                Intent serviceIntent=new Intent();
                serviceIntent.setClass(getApplicationContext(), SOAPService.class);
                startService(serviceIntent);
	}

}