package it.opencontent.android.ocparchitn.utils.nfc;

import it.opencontent.android.ocparchitn.activities.NDEFReadActivity;

import java.util.List;

import org.nfctools.ndef.NdefOperations;
import org.nfctools.ndef.NdefOperationsListener;
import org.nfctools.ndef.Record;
import org.nfctools.ndef.wkt.records.UriRecord;

import android.app.Activity;
import android.util.Log;

public class LoggingNdefOperationsListener implements NdefOperationsListener {

	private static final String NFCTOOLS = "NFCTOOLS";
	private static NDEFReadActivity caller;
	
	
	public LoggingNdefOperationsListener(Activity activity){
		caller = (NDEFReadActivity) activity;
	}

	@Override
	public void onNdefOperations(NdefOperations ndefOperations) {
		try {
		if (ndefOperations.isFormatted()) {
			if (ndefOperations.hasNdefMessage()) {
				List<Record> messages = ndefOperations.readNdefMessage();
				Log.i(NFCTOOLS, "Found "+messages.size()+" NDEF records");
				for (Record record : messages) {
					Log.i(NFCTOOLS, "NDEF: " + record);
					final String recordOut = record+"";
					caller.runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				            caller.manageNDEF(recordOut);
				        }
				    });  
					
				}
			}
			else {
				Log.i(NFCTOOLS, "no messages on TAG");
			}
		}
		else{
			Log.i(NFCTOOLS, "not formatted");
		}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
