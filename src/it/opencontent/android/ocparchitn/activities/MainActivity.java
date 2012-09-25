package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	private static PendingIntent pi;
	private static NfcAdapter nfca;
	private static IntentFilter[] ifa;
	private static String[][] techListsArray;
	private static Bitmap mImageBitmap; 
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfca = NfcAdapter.getDefaultAdapter(this);
        pi = PendingIntent.getActivity(
        	    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        
        
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                           You should specify only the ones that you need. */
        }
        catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        ifa = new IntentFilter[] {ndef, };
        techListsArray = new String[][] { new String[] { NfcF.class.getName() } };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
        if(mImageBitmap!=null){
	    ImageView mImageView = (ImageView) findViewById(R.id.snapshot);
	    mImageView.setImageBitmap(mImageBitmap);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
    }

    public void takeSnapshot(View button) {
    	int actionCode=0;
    	if(button.getTag(0) != null){
    		actionCode = Integer.parseInt((String) button.getTag(0));
    	
	    	if(actionCode < 0 ){
	    		actionCode = 0;
	    	}
    	} 
    	if(PlatformChecks.isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)){
    		
    	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(takePictureIntent, actionCode);
    	} else {
    		Log.d(TAG,"Nessuna App disponibile per fare fotografie");
    	}
    }
    
    @Override 
    protected void onActivityResult(int requestCode,int returnCode, Intent intent ){
    	    Bundle extras = intent.getExtras();
    	    mImageBitmap = (Bitmap) extras.get("data");
    	    ImageView mImageView = (ImageView) findViewById(R.id.snapshot);
    	    mImageView.setImageBitmap(mImageBitmap);
    }
    
}