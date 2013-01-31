package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAutGiochi;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPCodTabella;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPControllo;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkAutException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkGiochiException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkSrvException;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.fragments.AvailableFragment;
import it.opencontent.android.ocparchitn.fragments.ControlloFragment;
import it.opencontent.android.ocparchitn.fragments.ICustomFragment;
import it.opencontent.android.ocparchitn.utils.AuthCheck;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.ksoap2.serialization.KvmSerializable;
import org.kxml2.kdom.Element;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static boolean serviceInfoTaken = false;
	
	
	public static boolean tokenIsValid = false;

	private Bitmap snapshot;
	private OCParchiDB db;

	private static HashMap<String, Object> serviceInfo;

	private static Struttura currentStruttura;
	private static int currentSnapshotID;
	private static Bitmap[] snapshots = new Bitmap[Constants.MAX_SNAPSHOTS_AMOUNT];

	private static boolean partitiDaID = false;
	private static int currentQueriedId =0;
	private static boolean partitiDaRFID = false;
	private static ActionBar actionBar;

	private static NfcAdapter nfca;
	private static PendingIntent pi;
	private static IntentFilter[] ifa;
	private static String[][] techListsArray;

	private static float currentLat = 0;
	private static float currentLon = 0;

	private static HashMap<String, String> errorMessages = new HashMap<String, String>();
	
    public OnSharedPreferenceChangeListener mListener = new OnSharedPreferenceChangeListener() {        

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(getString(R.string.settings_key_username))
            		|| key.equals(getString(R.string.settings_key_password))){
            	tokenIsValid = false;
            }
        }
    };	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main_fragments);
		
		//Prima cosa controlliamo che il token sia attivo
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);    
        prefs.registerOnSharedPreferenceChangeListener(mListener);		

		db = new OCParchiDB(getApplicationContext());
		if (!serviceInfoTaken) {
			getServiceInfo();
			serviceInfoTaken = true;
		}
		if(!AuthCheck.getTokenValid() && PlatformChecks.siamoOnline(this)){
			renewAuthenticationToken();
		} else {
			setupActionBar();
			setupTabelleAppoggio();
		}
		
		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(
					"tab", 0));
		}
		
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

		// La techListArray per il momento la tengo vuota, così filtro per
		// qualsiasi
		// TODO: definire un set di techList specifiche e corrette per il
		// progetto

	}

	private void setupTabelleAppoggio(){
		//setup della tabella recordTabellaSupporto importandoli da remoto se vuota
		if(!db.tabelleSupportoPopolate() || db.tabelleSupportoScadute()){
			getTabellaSupporto();
		} else {
			Log.d(TAG,"Tabelle di supporto già popolate e aggiornate");
		}
	}
	
	private void setupActionBar(){
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		


		for(AvailableFragment f : AvailableFragment.values()){
			boolean displayMe = false;
			if(AuthCheck.siamoComune()){
				if( AuthCheck.getAutComune() >= f.minimumComuneAutLevel){
				displayMe = true;
				}
			} else if(AuthCheck.siamoCooperativa()){
				if( AuthCheck.getAutCooperativa() >= f.minimumCooperativaAutLevel){
					displayMe = true;
				}
			}
			if(displayMe){
			Class<ICustomFragment> specific = f.specificClass;
			
			
			
			Tab tab = actionBar
					.newTab()
					.setText(f.title)
					.setTag(f.label)
					.setTabListener(
							new CustomTabListener<ICustomFragment>(this,
									f.label, specific));
			boolean alreadyThere = false;
			for(int i = 0 ; i < actionBar.getTabCount(); i++){
				Tab t = actionBar.getTabAt(i);
				if(t.getTag().equals(tab.getTag())){
					alreadyThere = true;
				}
			}
			if(!alreadyThere){			
				actionBar.addTab(tab);
			}
			} else {
				Log.d(TAG,"Tab non mostrato causa permessi: "+f.label);
			}
		}
		updateCountDaSincronizzare();

		
	}

	@Override
	public void onPause() {
		super.onPause();
		if(nfca != null){
		nfca.disableForegroundDispatch(this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(nfca != null){
		nfca.enableForegroundDispatch(this, pi, ifa, techListsArray);
		}

//		showError(errorMessages);
		// nfca.enableForegroundDispatch(this, pi, null, null);
	}

	public void updateCountDaSincronizzare() {

		int pending = db.getPendingSynchronizations();
		actionBar.setTitle(getString(R.string.title_activity_main) + " ("
				+ pending + ")");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onStart() {
		
		super.onStart();
		if(!tokenIsValid){
			renewAuthenticationToken();
		}		
		
		// nfca.disableForegroundDispatch(this);
		LocationManager locationManager = (LocationManager) getSystemService(BaseActivity.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			locationManager.addGpsStatusListener(gpsListener);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					Constants.GPS_BEACON_INTERVAL,
					Constants.GPS_METER_THRESHOLD, locationListener);
			errorMessages.put(Constants.STATUS_MESSAGE_GPS_STATUS,
					Constants.STATUS_MESSAGE_GPS_STATUS_MESSAGE_OK);

		} else {
			errorMessages.put(Constants.STATUS_MESSAGE_GPS_STATUS,
					Constants.STATUS_MESSAGE_GPS_STATUS_MESSAGE_INACTIVE);

		}		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.unregisterOnSharedPreferenceChangeListener(mListener);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		//non vogliamo che la batteria si scarichi senza motivo
		LocationManager locationManager = (LocationManager) getSystemService(BaseActivity.LOCATION_SERVICE);
		locationManager.removeUpdates(locationListener);
	}
	


	private boolean legaRFIDStruttura(int rfid) {
		if(currentStruttura!=null && rfid >0){
			if(currentStruttura.getClass().equals(Gioco.class)){
				currentStruttura.rfid = rfid;
			} else if(currentStruttura.getClass().equals(Area.class)){
				currentStruttura.rfidArea = rfid;
			}
			salvaModifiche(null);		
			return true;		
		}
		return false;
	}
	
	private boolean legaRFIDAreaAGioco(int rfid){
		if(currentStruttura != null && rfid >0){
			if(currentStruttura.getClass().equals(Gioco.class)){
				currentStruttura.rfidArea = rfid;
				salvaModifiche(null);		
				return true;		
			} else if(currentStruttura.getClass().equals(Area.class)){
				return false;
			}
		}
		return false;		
	}

	private void feedback(String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Risultato");
		alert.setMessage(message);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		alert.show();
	}
	private void confirmLegaRFIDAreaAGioco() {
		final int mrfid = currentRFID;
		final Struttura mStruttura = currentStruttura;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		if(!currentStruttura.getClass().equals(Gioco.class)){
			Log.e(TAG,"La struttura non è un gioco, non posso legare un idArea");
			return;
		}
			
		
		if(mStruttura != null)
			alert.setTitle("Vuoi associare l'RFID di area " + mrfid + " al Gioco "
				+ mStruttura.idGioco + " ?");
		if (mStruttura.rfidArea > 0) {
			alert.setMessage(" Il Gioco " + mStruttura.idGioco + "attualmente è legato all'area "+ mStruttura.rfidArea );
		} else {
			alert.setMessage(" Il Gioco " + mStruttura.idGioco + 
					" attualmente non ha Aree associate");
		}

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (legaRFIDAreaAGioco(mrfid)) {
					feedback("Ok, ora il Gioco " + mStruttura.idGioco 
							+ " è associato all'RFID " + mrfid);
					String currentTag = (String) actionBar.getSelectedTab()
							.getTag();
					ICustomFragment mf = (ICustomFragment) getFragmentManager()
							.findFragmentByTag(currentTag);
					mf.showStrutturaData(currentStruttura);
					// TODO: triggerare il salvataggio dei dati locali che poi
					// scatena a sua volta il salvataggio remoto
				} else {
					feedback("Qualcosa non ha funzionato, ritentare l'operazione");
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		alert.show();

	}


	private void confirmLegaRFIDAStruttura() {
		final int mrfid = currentRFID;
		final Struttura mStruttura = currentStruttura;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final String aCosa;
		final String cosa;
		final String idMostra;
		final int rfidLegato;
		if(currentStruttura.getClass().equals(Gioco.class)){
			aCosa = " al Gioco ";
			cosa = " Il Gioco ";
			idMostra = ((Gioco) mStruttura).idGioco+"";
			rfidLegato = mStruttura.rfid;
		} else {
			aCosa = " all'Area ";
			cosa = " L'Area ";
			idMostra = ((Area) mStruttura).idArea+"";
			rfidLegato = mStruttura.rfidArea;
		}
		
		if(mStruttura != null)
			alert.setTitle("Vuoi associare l'RFID " + mrfid + aCosa
				+ idMostra + " ?");
		if (rfidLegato > 0) {
			alert.setMessage(cosa + idMostra
					+ " attualmente ha l'RFID " + rfidLegato);
		} else {
			alert.setMessage(cosa + idMostra
					+ " attualmente non ha RFID associati");
		}

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (legaRFIDStruttura(mrfid)) {
					feedback("Ok, ora "+ cosa  + idMostra
							+ " è associato all'RFID " + mrfid);
					String currentTag = (String) actionBar.getSelectedTab()
							.getTag();
					ICustomFragment mf = (ICustomFragment) getFragmentManager()
							.findFragmentByTag(currentTag);
					mf.showStrutturaData(currentStruttura);
					// TODO: triggerare il salvataggio dei dati locali che poi
					// scatena a sua volta il salvataggio remoto
				} else {
					feedback("Qualcosa non ha funzionato, ritentare l'operazione");
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		alert.show();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		try {
			NdefMessage rawMsg = (NdefMessage) intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)[0];

			int length = rawMsg.toByteArray().length;
			byte[] res = new byte[length - 5];
			for (int i = 5; i < length; i++) {
				res[i - 5] = rawMsg.toByteArray()[i];
			}
			String out = new String(res);
			
			currentRFID = parseNDEFForRFID(out);
		} catch (Exception e) {
			// Non è un intent che ci interessa in questo caso
		}
	}

	/**
	 * @param out
	 */
	public int parseNDEFForRFID(String out) {
		String[] pieces = out.split("://");
		String[] actualValues = pieces[1].split("/");
		String trimmedAndStripped = actualValues[1].replaceAll("]", "").trim();
		int rfid;
		try{
			
			rfid = Integer.parseInt(trimmedAndStripped );
		} catch (Exception e){
			Log.d(TAG,"Messaggio NDEF non valido: "+out);
			rfid = -1;
		}
		
		return rfid;
	}

	public void startRilevazioneDaID(View v) {
		Log.d(TAG, "Partiamo da id");

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Inserisci l'ID di un gioco");
		alert.setMessage("ID");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				partitiDaID = true;
				int idRequested = 0;
				try {
					idRequested = Integer.parseInt(value);
					getStructureDataByID(idRequested);// Do something
					// with value!
				} catch (NumberFormatException nfe) {
					Toast.makeText(getApplicationContext(),
							"Numero non riconosciuto", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		alert.show();

	}
	
	public void startRilevazioneDaRFID(View v){
		Log.d(TAG,"Partiamo da rfid, lancio l'activity");
		Intent leggiRFID = new Intent();
		leggiRFID.setClass(this, NDEFReadActivity.class);
		partitiDaID = false;
		partitiDaRFID = true;
		startActivityForResult(leggiRFID, Constants.LEGGI_RFID_DA_LETTORE_ESTERNO);
	}
	
	public void recuperaControlliPerRFID(View v){
		Log.d(TAG,"Partiamo col controllo da rfid, lancio l'activity");
		Intent leggiRFID = new Intent();
		leggiRFID.setClass(this, NDEFReadActivity.class);
		leggiRFID.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_AREA_ID_METHOD_NAME);			
		partitiDaID = false;
		partitiDaRFID = true;
		startActivityForResult(leggiRFID, Constants.LEGGI_RFID_DA_LETTORE_ESTERNO_PER_CONTROLLI);
	}
	
	public void associaRFIDStruttura(View v){
		Log.d(TAG,"Leghiamo l'rfid dell'area, lancio l'activity");
		Intent leggiRFID = new Intent();
		leggiRFID.setClass(this, NDEFReadActivity.class);
		partitiDaID = false;
		partitiDaRFID = true;
		startActivityForResult(leggiRFID, Constants.LEGGI_RFID_DA_LETTORE_ESTERNO_E_LEGALO_A_STRUTTURA);		
	}
	
	public void associaRFIDArea(View v){
		Log.d(TAG,"Leghiamo l'rfid dell'area al gioco, lancio l'activity");
		Intent leggiRFID = new Intent();
		leggiRFID.setClass(this, NDEFReadActivity.class);
		partitiDaID = false;
		partitiDaRFID = true;
		startActivityForResult(leggiRFID, Constants.LEGGI_RFID_AREA_DA_LETTORE_ESTERNO_E_LEGALO_A_GIOCO);		
	}
	
	

	private void getServiceInfo() {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_INFO_METHOD_NAME);
		startActivityForResult(serviceIntent, Constants.SOAP_SERVICE_INFO_REQUEST_CODE);
	}
	public void sincronizzaModifiche(View v) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME,
				Constants.EXTRAKEY_SYNC_ALL);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("all", true);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, Constants.SOAP_SINCRONIZZA_TUTTO_REQUEST_CODE);

	}
	private void getStructureDataByID(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("args0", "" + id);
		currentQueriedId = id;
		currentStruttura = null;
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		
		//Controlliamo di che tipo di struttura stiamo parlando:
		//Gioco o Area
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		if(currentTag.equals(AvailableFragment.RILEVAZIONE_AREA.label)){
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_AREA_ID_METHOD_NAME);			
			serviceIntent.putExtra(Constants.EXTRAKEY_STRUCTURE_TYPE, Constants.CODICE_STRUTTURA_AREA);			
			startActivityForResult(serviceIntent, Constants.SOAP_GET_AREA_REQUEST_CODE_BY_ID);
		} else if(currentTag.equals(AvailableFragment.RILEVAZIONE_GIOCO.label)){
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_GIOCO_ID_METHOD_NAME);			
			serviceIntent.putExtra(Constants.EXTRAKEY_STRUCTURE_TYPE, Constants.CODICE_STRUTTURA_GIOCO);			
			startActivityForResult(serviceIntent, Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_ID);
		}
	}
	private void getStructureFotoByID(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_FOTO_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idGioco", "" + id);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, Constants.SOAP_GET_GIOCO_FOTO_REQUEST_CODE);
	}
	private void getTabellaSupporto() {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_TABELLA_METHOD_NAME);
		startActivityForResult(serviceIntent, Constants.SOAP_GET_TABELLA_REQUEST_CODE);
	}
	
	private void getControlliPerStruttura(int tipoStruttura){
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("args0", tipoStruttura);
		map.put("args1", currentRFID);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_CONTROLLO_METHOD_NAME);
		startActivityForResult(serviceIntent, Constants.SOAP_GET_CONTROLLO_REQUEST_CODE_BY_RFID);		
	}
	
	
	private void getStructureDataByRFID() {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rfid", "" + currentRFID);
		currentStruttura = null;
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		if(currentTag.equals(AvailableFragment.RILEVAZIONE_AREA.label)){
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_AREA_METHOD_NAME);			
			serviceIntent.putExtra(Constants.EXTRAKEY_STRUCTURE_TYPE, Constants.CODICE_STRUTTURA_AREA);			
			startActivityForResult(serviceIntent, Constants.SOAP_GET_AREA_REQUEST_CODE_BY_RFID);
		} else if(currentTag.equals(AvailableFragment.RILEVAZIONE_GIOCO.label)){
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_GIOCO_METHOD_NAME);			
			serviceIntent.putExtra(Constants.EXTRAKEY_STRUCTURE_TYPE, Constants.CODICE_STRUTTURA_GIOCO);			
			startActivityForResult(serviceIntent, Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID);
		} else if(currentTag.equals(AvailableFragment.CONTROLLO.label)){
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, ControlloFragment.methodName );			
			serviceIntent.putExtra(Constants.EXTRAKEY_STRUCTURE_TYPE, ControlloFragment.tipoStruttura);
			serviceIntent.putExtra(Constants.EXTRAKEY_RECOVER_CONTROLS, true);
			startActivityForResult(serviceIntent, ControlloFragment.soapMethodName);
		}		
	}

	private void getStructureFoto(int tipoStruttura, int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_FOTO_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("idGioco", "" + id);
		map.put("args0", tipoStruttura);
		map.put("args1", "" + id);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, Constants.SOAP_GET_GIOCO_FOTO_REQUEST_CODE);
	}
	
	private void renewAuthenticationToken(){
//		Toast.makeText(this, "Rinnovo l'autenticazione",Toast.LENGTH_SHORT).show();
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_LOGINUSER_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String username = prefs.getString(getString(R.string.settings_key_username), "UNSET");
		String password = prefs.getString(getString(R.string.settings_key_password), "UNSET");
		map.put("args0", username);
		map.put("args1", password);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		if(username.equals("UNSET")){
			//Prima inizializzazione
			AlertDialog.Builder changeCredentials = new AlertDialog.Builder(this);
			changeCredentials.setTitle("Imposta le credenziali");
			changeCredentials.setMessage("Per inizializzare il sistema è necessario avere una connessione dati attiva\n\nPer usare questo sistema occorre impostare username e password\nClicca su OK per inserire le credenziali\nUna volta inserite clicca su indietro e l'applicazione si inizializzerà");
			changeCredentials.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), SettingsActivity.class);
					startActivityForResult(intent,Constants.CREDENTIALS_UPDATED_REQUEST_CODE);
					return;
				}
				
			});
			changeCredentials.show();			
		} else {		
		startActivityForResult(serviceIntent, Constants.SOAP_GET_TOKEN_REQUEST_CODE);
		}
	}
	
	private void displayStruttura(){
		String currentTag = "";
		if(actionBar != null && actionBar.getSelectedTab() != null){
			currentTag = (String) actionBar.getSelectedTab().getTag();
		}
		ICustomFragment mf = (ICustomFragment) getFragmentManager()
				.findFragmentByTag(currentTag);
		mf.showStrutturaData(currentStruttura);		
	}
	

	@Override
	public void onActivityResult(int requestCode, int returnCode, Intent intent) {
		HashMap<String, Object> res;
		int tipoStruttura = -1;
		switch (requestCode) {
		case Constants.LEGGI_RFID_DA_LETTORE_ESTERNO:
			if(intent == null){
				Toast.makeText(this, "Lettura annullata", Toast.LENGTH_LONG).show();
			} else if( intent.getExtras() != null && intent.getExtras().get(Constants.EXTRAKEY_RFID) != null){
				currentRFID = parseNDEFForRFID(intent.getExtras().get(Constants.EXTRAKEY_RFID).toString());
				
				if (partitiDaID) {
					confirmLegaRFIDAStruttura();
				} else if (partitiDaRFID) {
					getStructureDataByRFID();
				}
			} else {
				Toast.makeText(this, "Tag non riconosciuto", Toast.LENGTH_LONG).show();
			}
			break;

		case Constants.LEGGI_RFID_AREA_DA_LETTORE_ESTERNO_E_LEGALO_A_GIOCO:
			if(intent == null){
				Toast.makeText(this, "Lettura annullata", Toast.LENGTH_LONG).show();
			} else if( intent.getExtras() != null && intent.getExtras().get(Constants.EXTRAKEY_RFID) != null){
				currentRFID = parseNDEFForRFID(intent.getExtras().get(Constants.EXTRAKEY_RFID).toString());
				confirmLegaRFIDAreaAGioco();				
			} else {
				Toast.makeText(this, "Tag non riconosciuto", Toast.LENGTH_LONG).show();
			}
			break;
		case Constants.LEGGI_RFID_DA_LETTORE_ESTERNO_E_LEGALO_A_STRUTTURA:
			if(intent == null){
				Toast.makeText(this, "Lettura annullata", Toast.LENGTH_LONG).show();
			} else if( intent.getExtras() != null && intent.getExtras().get(Constants.EXTRAKEY_RFID) != null){
				currentRFID = parseNDEFForRFID(intent.getExtras().get(Constants.EXTRAKEY_RFID).toString());
				confirmLegaRFIDAStruttura();				
			} else {
				Toast.makeText(this, "Tag non riconosciuto", Toast.LENGTH_LONG).show();
			}			
			break;			
		case Constants.CREDENTIALS_UPDATED_REQUEST_CODE:
			renewAuthenticationToken();
			break;
		case Constants.SOAP_GET_TOKEN_REQUEST_CODE:
			res = SynchroSoapActivity.getRes(Constants.GET_LOGINUSER_METHOD_NAME);
			if(res != null && res.containsKey("success") ){
				String faultString = res.get("string").toString();
				Toast.makeText(getApplicationContext(), faultString, Toast.LENGTH_SHORT).show();
				AlertDialog.Builder changeCredentials = new AlertDialog.Builder(this);
				changeCredentials.setTitle("Credenziali errate");
				changeCredentials.setMessage(faultString+"\nClicca su OK per modificare le credenziali");
				changeCredentials.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(), SettingsActivity.class);
						startActivityForResult(intent,Constants.CREDENTIALS_UPDATED_REQUEST_CODE);
						return;
					}
					
				});
				changeCredentials.show();
				
			} else if(res != null && res.containsKey("mapped")){
				SOAPAutGiochi auth = (SOAPAutGiochi) res.get("mapped");
				AuthCheck.setAutGiochi(auth);
				setupActionBar();
				setupTabelleAppoggio();
			}
			
			if(res!=null && res.containsKey("headerIn")){
				AuthCheck.setHeaderOut((Element[]) res.get("headerIn"));
			}
			
			break;
		case Constants.SOAP_SINCRONIZZA_TUTTO_REQUEST_CODE:
			updateCountDaSincronizzare();
			break;
		case Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_ID:
			res = SynchroSoapActivity.getRes(Constants.GET_GIOCO_ID_METHOD_NAME);
			tipoStruttura = intent.getExtras().getInt(Constants.EXTRAKEY_STRUCTURE_TYPE);
			manageSOAPGenericStrutturaResponse(res, tipoStruttura);
			break;
		case Constants.SOAP_GET_AREA_REQUEST_CODE_BY_ID:
			res = SynchroSoapActivity.getRes(Constants.GET_AREA_ID_METHOD_NAME);
			tipoStruttura = intent.getExtras().getInt(Constants.EXTRAKEY_STRUCTURE_TYPE);
			manageSOAPGenericStrutturaResponse(res, tipoStruttura);
			break;
		case Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID:
			res = SynchroSoapActivity.getRes(Constants.GET_GIOCO_METHOD_NAME);
			tipoStruttura = intent.getExtras().getInt(Constants.EXTRAKEY_STRUCTURE_TYPE);
			manageSOAPGenericStrutturaResponse(res, tipoStruttura);
			if (intent.getExtras().containsKey(Constants.EXTRAKEY_RECOVER_CONTROLS)){
				getControlliPerStruttura(tipoStruttura);
			}
			break;
		case Constants.SOAP_GET_AREA_REQUEST_CODE_BY_RFID:
			res = SynchroSoapActivity.getRes(Constants.GET_AREA_METHOD_NAME);
			tipoStruttura = intent.getExtras().getInt(Constants.EXTRAKEY_STRUCTURE_TYPE);
			manageSOAPGenericStrutturaResponse(res, tipoStruttura);
			if (intent.getExtras().containsKey(Constants.EXTRAKEY_RECOVER_CONTROLS)){
				getControlliPerStruttura(tipoStruttura);
			}
			break;
		case Constants.SOAP_GET_CONTROLLO_REQUEST_CODE_BY_RFID:
			res = SynchroSoapActivity.getRes(Constants.GET_CONTROLLO_METHOD_NAME);
			if(res == null){
				
			}else if(res.containsKey("success")){
				manageRemoteException(res);
			} else {
				if(res.containsKey("mapped")){
					Controllo controllo = new Controllo((SOAPControllo) res.get("mapped"));
					ControlloFragment.appendControllo(controllo);
					displayStruttura();
				}
				//metodo per passare il controllo al controlloFragment
			}
			
			
			break;			
		case Constants.SOAP_GET_TABELLA_REQUEST_CODE:
			if (returnCode == RESULT_OK) {
				for(int id : Constants.ID_TABELLE_SUPPORTO){
					res = SynchroSoapActivity.getRes(Constants.GET_TABELLA_METHOD_NAME+"_"+id);
					if(res!=null && !res.containsKey("success")){
						ArrayList<RecordTabellaSupporto> records = new ArrayList<RecordTabellaSupporto>();
						Set<Entry<String,Object>> s = res.entrySet();
						Iterator<Entry<String,Object>> iterator = s.iterator();
						while(iterator.hasNext()){
							Entry<String,Object> e = iterator.next();
							if(e.getValue() instanceof SOAPCodTabella){
								records.add(new RecordTabellaSupporto(id,(SOAPCodTabella) e.getValue()) );						
							}
						}
						db.tabelleSupportoUpdate(records.toArray(new RecordTabellaSupporto[records.size()]));
					} else {
						manageRemoteException(res);
					}
				}
			}
			break;
		case Constants.SOAP_GET_GIOCO_FOTO_REQUEST_CODE:
			if (returnCode == RESULT_OK) {
				res = SynchroSoapActivity.getRes(Constants.GET_FOTO_METHOD_NAME);
				currentStruttura.addImmagine(res.entrySet());
			}
			displayStruttura();
			break;
		case Constants.FOTO_REQUEST_CODE:
			legaSnapshotAStruttura(intent);
			break;
		case Constants.FOTO_CONTROLLO_REQUEST_CODE:
			legaSnapshotAControllo(intent);
			break;
		case Constants.SOAP_SERVICE_INFO_REQUEST_CODE:
			//TODO: rimappare l'oggetto info
			serviceInfo = SynchroSoapActivity.getRes(Constants.GET_INFO_METHOD_NAME);
			serviceInfoTaken = true;
			errorMessages.put(Constants.STATUS_MESSAGE_SERVER_STATUS,
					"Connessione al server: OK");
			break;
			default:
				super.onActivityResult(requestCode, returnCode, intent);
				break;

		}

	}

	private void legaSnapshotAStruttura(Intent intent) {
		try {
			snapshot = (Bitmap) intent.getExtras().get("data");
			
			
			if (currentStruttura == null) {
				currentStruttura = new Struttura();
				currentStruttura.sincronizzato = false;
				currentStruttura.hasDirtyData = true;
				currentRFID = 0;
			}
			int whichOne;
			if(intent.getExtras().containsKey(Constants.EXTRAKEY_FOTO_NUMBER)){
			 whichOne = intent.getExtras().getInt(
					Constants.EXTRAKEY_FOTO_NUMBER);
			} else {
				whichOne = currentSnapshotID;
				currentSnapshotID = -1;
			}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();  
			snapshot.compress(Bitmap.CompressFormat.PNG, 80, stream);
			byte[] image = stream.toByteArray();

			ImageView mImageView = null;
			switch (whichOne) {
			case 0:
				mImageView = (ImageView) findViewById(R.id.snapshot_gioco_0);
				currentStruttura.foto0 = Base64.encodeToString(image, Base64.DEFAULT);
				break;
			case 1:
				mImageView = (ImageView) findViewById(R.id.snapshot_gioco_1);
				currentStruttura.foto1 = Base64.encodeToString(image, Base64.DEFAULT);
				break;
			case 2:
				mImageView = (ImageView) findViewById(R.id.snapshot_gioco_2);
				currentStruttura.foto2 = Base64.encodeToString(image, Base64.DEFAULT);
				break;
			case 3:
				mImageView = (ImageView) findViewById(R.id.snapshot_gioco_3);
				currentStruttura.foto3 = Base64.encodeToString(image, Base64.DEFAULT);
				break;
			case 4:
				mImageView = (ImageView) findViewById(R.id.snapshot_gioco_4);
				currentStruttura.foto4 = Base64.encodeToString(image, Base64.DEFAULT);
				break;
			}
			currentStruttura.sincronizzato = false;
			currentStruttura.hasDirtyData = true;

			if (snapshot != null && mImageView != null) {
				snapshots[whichOne] = snapshot;
				mImageView.setImageBitmap(snapshot);
			}
		} catch (NullPointerException e) {
			Log.d(TAG, "Immagine nulla");
		}
	}

	private void legaSnapshotAControllo(Intent intent) {
		try {
			snapshot = (Bitmap) intent.getExtras().get("data");
			
			
			int whichOne;
			if(intent.getExtras().containsKey(Constants.EXTRAKEY_FOTO_NUMBER)){
			 whichOne = intent.getExtras().getInt(
					Constants.EXTRAKEY_FOTO_NUMBER);
			} else {
				whichOne = 1;
			}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();  
			snapshot.compress(Bitmap.CompressFormat.PNG, 80, stream);
			byte[] image = stream.toByteArray();

			ImageView mImageView = null;
			mImageView = (ImageView) findViewById(R.id.snapshot_controllo_0);
			ControlloFragment.aggiungiSnapshotAControlloCorrente(Base64.encodeToString(image, Base64.DEFAULT));
			
			if (snapshot != null && mImageView != null) {
				snapshots[whichOne] = snapshot;
				mImageView.setImageBitmap(snapshot);
			}
		} catch (NullPointerException e) {
			Log.d(TAG, "Immagine nulla");
		}
	}	
	
	private void manageSOAPGenericStrutturaResponse(
			HashMap<String, Object> res, int tipoStruttura) {
		Struttura remoteStruttura = null;
		Struttura localStruttura = null;
		if(res!=null && !res.containsKey("success")) {
			switch(tipoStruttura){
			case Constants.CODICE_STRUTTURA_GIOCO:
			if(res.containsKey("mapped")){
				remoteStruttura = new Gioco((it.opencontent.android.ocparchitn.SOAPMappings.SOAPGioco) res.get("mapped"),
						getApplicationContext());
			} else {
				remoteStruttura = new Gioco(res.entrySet(),
						getApplicationContext());
			}
			localStruttura = db.readGiocoLocallyByID(remoteStruttura.idGioco);
			break;
			case Constants.CODICE_STRUTTURA_AREA:
				if(res.containsKey("mapped")){
					remoteStruttura = new Area((it.opencontent.android.ocparchitn.SOAPMappings.SOAPArea) res.get("mapped"),
							getApplicationContext());
				} else {
					remoteStruttura = new Area(res.entrySet(),
							getApplicationContext());
				}
				localStruttura = db.readAreaLocallyByID(((Area)remoteStruttura).idArea);
				break;
			}
		} else if(res.containsKey("success") && res.get("success").equals(false) ){
			manageRemoteException(res);
			switch(tipoStruttura){
			case Constants.CODICE_STRUTTURA_GIOCO:
				remoteStruttura = new Gioco();
				break;
			case Constants.CODICE_STRUTTURA_AREA:
				remoteStruttura = new Area();
				break;
			}
			localStruttura = db.readGiocoLocallyByID(currentQueriedId);
		}else {
			//res == null
			switch(tipoStruttura){
			case Constants.CODICE_STRUTTURA_GIOCO:
				remoteStruttura = new Gioco();
				break;
			case Constants.CODICE_STRUTTURA_AREA:
				remoteStruttura = new Area();
				break;
			}				
		 localStruttura = db.readGiocoLocallyByID(currentQueriedId);
		}
		
		
		
		if (localStruttura != null) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Decidi quali dati tenere");
			TextView content = new TextView(this);
			final Struttura loc = localStruttura;
			final Struttura rem = remoteStruttura;
			final int tipo = tipoStruttura;
			content.setText("La struttura "+localStruttura.idGioco+" ha delle modifiche non sincronizzate col server "+
			"\n Decidi quali vuoi: quelle locali ancora non inviate o quelle remote "+
			"\n Se scegli di tenere valide quelle remote saranno cancellate le modifiche locali "+
			"\n Se scegli di tenere valide quelle locali quelle remote saranno ignorate"+
			"\n Se scegli nessuna delle due sarà creata una nova struttura localmente, che cancellerà i dati locali precedenti (foto comprese) ");
			alert.setView(content);
			
			alert.setItems(new String[] {"Versione Server","Versione Locale","Nessuna delle due"}, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which){
					case 0:
						currentStruttura = rem;
						break;
					case 1:
						currentStruttura = loc;
						break;
					case 2:
					default:
						currentStruttura=null;
						switch(tipo){
						case Constants.CODICE_STRUTTURA_AREA:
							currentStruttura = new Area();
							break;
						case Constants.CODICE_STRUTTURA_GIOCO:
							currentStruttura = new Gioco();
							break;
						}
						currentStruttura.idGioco = currentQueriedId;
						break;
					}
					displayStruttura();
				}
			});
			alert.show();
			

			//Le foto le abbiamo già in locale
		} else if(PlatformChecks.siamoOnline(getApplicationContext()) && remoteStruttura !=null){
			currentStruttura = remoteStruttura;
			//Lo showStruttura viene chiamato dal loop delle foto
			if(currentStruttura.numeroFotografie > 0){
				switch(tipoStruttura){
				case Constants.CODICE_STRUTTURA_AREA:
					getStructureFoto(tipoStruttura, ((Area) currentStruttura).idArea);
					break;
				case Constants.CODICE_STRUTTURA_GIOCO:
					getStructureFoto(tipoStruttura, currentStruttura.idGioco);
					break;
				}
			}	
			displayStruttura();
		} else {
				switch(tipoStruttura){
				case Constants.CODICE_STRUTTURA_GIOCO:
					currentStruttura = db.readGiocoLocallyByID(currentQueriedId);
					if(currentStruttura == null){
					currentStruttura = new Gioco();
					((Gioco) currentStruttura).idGioco = currentQueriedId;
					}
					break;
				case Constants.CODICE_STRUTTURA_AREA:
					currentStruttura = db.readAreaLocallyByID(currentQueriedId);
					if(currentStruttura == null){
					currentStruttura = new Area();
					((Area) currentStruttura).idArea = currentQueriedId;
					}
					break;
				}	
			displayStruttura();
		}
	}

	/**
	 * Gestiamo le eccezioni remote, fra cui anche quelle di autenticazione
	 * 
	 * @param res
	 */
	private void manageRemoteException(HashMap<String, Object> res) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Errore remoto nel recupero dei dati ");
		TextView content = new TextView(getApplicationContext());
		String outText = "";
		
		
		if(res.get("exception") != null){
			KvmSerializable exception = (KvmSerializable) res.get("exception");
			if(exception.getClass().equals(SOAPSrvGiocoArkAutException.class)){
				String codice = ((SOAPSrvGiocoArkAutException) exception).codice;
				outText += "\nCodice: "+codice;
				outText += "\nMessaggio: "+((SOAPSrvGiocoArkAutException) exception).message;
				if(codice.equals(Constants.SOAP_EXCEPTION_ARKAUT_TOKEN_SCADUTO)){
					 tokenIsValid = false;					
				}
			}else if(exception.getClass().equals(SOAPSrvGiocoArkGiochiException.class)){
				outText += "\nCodice: "+((SOAPSrvGiocoArkGiochiException) exception).codice;
				outText += "\nMessaggio: "+((SOAPSrvGiocoArkGiochiException) exception).message;
			} else {
				outText += "\nMessaggio: "+((SOAPSrvGiocoArkSrvException) exception).message;
			}
			
		} else {
			if(res.get("faultcode") != null ){
				outText += res.get("faultcode"); 
			}
			if(res.get("string") != null ){
				outText += "\n"+res.get("string"); 
			}
		}
		content.setText(outText);
		alert.setView(content);
		alert.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!tokenIsValid){
					renewAuthenticationToken();
				}
			}
		});
		alert.show();
	}

	public static Gioco getCurrentGioco() {
		if (currentStruttura != null && currentStruttura.getClass().equals(Gioco.class)){
			return (Gioco) currentStruttura;
		} else {
			return null;
		}
	}
	public static Area getCurrentArea() {
		if (currentStruttura != null && currentStruttura.getClass().equals(Area.class)){
			return (Area) currentStruttura;
		} else {
			return null;
		}
	}
	public static void setCurrentArea(Area a) {
		if(a != null && a.getClass().equals(Area.class)){
			currentStruttura = a;
		}
	}

	public void editMe(View v) {
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		ICustomFragment f = (ICustomFragment) getFragmentManager()
				.findFragmentByTag(currentTag);
		f.editMe(v);
	}
	public void clickedMe(View v) {
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		ICustomFragment f = (ICustomFragment) getFragmentManager()
				.findFragmentByTag(currentTag);
		f.clickedMe(v);
	}

	public void salvaModifiche(View v) {
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		ICustomFragment f = (ICustomFragment) getFragmentManager()
				.findFragmentByTag(currentTag);
		f.salvaModifiche(v);
	}

	public void showError(HashMap<String, String> map) {
		if(actionBar != null && actionBar.getSelectedTab() != null){
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		ICustomFragment f = (ICustomFragment) getFragmentManager()
				.findFragmentByTag(currentTag);
		f.showError(map);
		} else {
			showError(map, true);
		}
	}

	private void showError(HashMap<String,String> map, boolean here){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Errore");
		String out = "";
		Iterator<Entry<String,String>> i = map.entrySet().iterator();
		while(i.hasNext()){
			Entry<String,String> n = (Entry<String, String>) i.next();
			out +="\n"+n.getValue();
		}
		alert.setMessage(out);
		alert.setPositiveButton("OK", null);
		alert.show();
	}
	
	public void takeSnapshot(View button) {
		Intent customCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		int whichOne = Integer.parseInt((String) button.getTag());
		customCamera.putExtra(Constants.EXTRAKEY_FOTO_NUMBER, whichOne);
		currentSnapshotID = whichOne;
		startActivityForResult(customCamera, Constants.FOTO_REQUEST_CODE);
	}
	
	public void takeSnapshotControllo(View button) {
		Intent customCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		int whichOne = Integer.parseInt((String) button.getTag());
		customCamera.putExtra(Constants.EXTRAKEY_FOTO_NUMBER, whichOne);
		currentSnapshotID = whichOne;
		startActivityForResult(customCamera, Constants.FOTO_CONTROLLO_REQUEST_CODE);		
	}
	

	public static float getCurrentLon() {
		return currentLon;
	}

	public static float getCurrentLat() {
		return currentLat;
	}

//	public static class CustomTabListener<T extends Fragment> implements
	public static class CustomTabListener<T> implements
			ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public CustomTabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

	private Listener gpsListener = new Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {

			case GpsStatus.GPS_EVENT_FIRST_FIX:

				Log.d(TAG, "onGpsStatusChanged First Fix");
				errorMessages.put(Constants.STATUS_MESSAGE_GPS_STATUS,
						Constants.STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXED);
//				showError(errorMessages);
				break;

			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

//				Log.d(TAG, "onGpsStatusChanged Satellite");
				break;

			case GpsStatus.GPS_EVENT_STARTED:

				Log.d(TAG, "onGpsStatusChanged Started");
				errorMessages.put(Constants.STATUS_MESSAGE_GPS_STATUS,
						Constants.STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXING);
//				showError(errorMessages);
				break;

			case GpsStatus.GPS_EVENT_STOPPED:

				Log.d(TAG, "onGpsStatusChanged Stopped");

				break;

			}

		}
	};

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			errorMessages.put(Constants.STATUS_MESSAGE_GPS_STATUS,
					Constants.STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXED);
//			showError(errorMessages);
			currentLat = (float) location.getLatitude();
			currentLon = (float) location.getLongitude();
			if(currentStruttura != null){
				currentStruttura.gpsx = currentLon+"";
				currentStruttura.gpsy = currentLat+"";
			}

			TextView tgpsx = (TextView) findViewById(R.id.display_gioco_gpsx);
			TextView tgpsy = (TextView) findViewById(R.id.display_gioco_gpsy);
			TextView tgpsc = (TextView) findViewById(R.id.display_gioco_gps_confidence);
			if(tgpsc!=null){
				tgpsc.setText("Confidence: "+location.getAccuracy()+"mt");
			}
			if(tgpsx!=null){
				tgpsx.setText(""+currentLon);
			}
			if(tgpsy!=null){
				tgpsy.setText(""+currentLat);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			Log.d(TAG, provider + " " + status);
		}
	};

}
