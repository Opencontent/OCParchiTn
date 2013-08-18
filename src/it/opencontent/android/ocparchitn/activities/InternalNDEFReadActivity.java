package it.opencontent.android.ocparchitn.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;

import it.opencontent.android.ocparchitn.R;

/**
 * Created by marco on 16/08/13.
 */
public class InternalNDEFReadActivity extends Activity {

    private final static String TAG = InternalNDEFReadActivity.class.getSimpleName();
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private int mCount = 0;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.ndefread_main);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            tech.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }


        mFilters = new IntentFilter[] {
                ndef, tech
        };

        // Setup a tech list for all NfcF tags
        mTechLists =  new String[][] { new String[] { NfcA.class.getName(),
            NfcB.class.getName(), NfcF.class.getName(),
            NfcV.class.getName(), IsoDep.class.getName(),
            MifareClassic.class.getName(),
            MifareUltralight.class.getName(), Ndef.class.getName() } };

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }
}
