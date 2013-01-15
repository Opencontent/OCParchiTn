package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAutGiochi;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkAutException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkGiochiException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkSrvException;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.fragments.DebugFragment;
import it.opencontent.android.ocparchitn.fragments.ICustomFragment;
import it.opencontent.android.ocparchitn.fragments.MainFragment;
import it.opencontent.android.ocparchitn.fragments.PeriodicaFragment;
import it.opencontent.android.ocparchitn.fragments.RendicontazioneFragment;
import it.opencontent.android.ocparchitn.fragments.SpostamentoFragment;
import it.opencontent.android.ocparchitn.utils.FileNameCreator;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.ksoap2.serialization.KvmSerializable;
import org.kxml2.kdom.Element;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
	
	public static Element[] headerOut = null;
	public static boolean tokenIsValid = false;

	private Bitmap snapshot;
	private OCParchiDB db;

	private static HashMap<String, Object> serviceInfo;

	private static Gioco currentGioco;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main_fragments);

		db = new OCParchiDB(getApplicationContext());

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab;
		tab = actionBar
				.newTab()
				.setText(getString(R.string.fragment_title_rilevazione))
				.setTag("rilevazione")
				.setTabListener(
						new CustomTabListener<MainFragment>(this,
								"rilevazione", MainFragment.class));
		actionBar.addTab(tab);
		tab = actionBar
				.newTab()
				.setTag("periodica")
				.setText(getString(R.string.fragment_title_attivita_periodica))
				.setTabListener(
						new CustomTabListener<PeriodicaFragment>(this,
								"periodica", PeriodicaFragment.class));
		actionBar.addTab(tab);
		tab = actionBar
				.newTab()
				.setTag("rendicontazione")
				.setText(
						getString(R.string.fragment_title_rendicontazione_manutenzione))
				.setTabListener(
						new CustomTabListener<RendicontazioneFragment>(this,
								"rendicontazione",
								RendicontazioneFragment.class));
		actionBar.addTab(tab);
		tab = actionBar
				.newTab()
				.setTag("spostamento")
				.setText(getString(R.string.fragment_title_spostamento_gioco))
				.setTabListener(
						new CustomTabListener<SpostamentoFragment>(this,
								"spostamento", SpostamentoFragment.class));
		actionBar.addTab(tab);
		tab = actionBar
				.newTab()
				.setTag("debug")
				.setText(getString(R.string.fragment_title_debug))
				.setTabListener(
						new CustomTabListener<DebugFragment>(this,
								"debug", DebugFragment.class));
		actionBar.addTab(tab);

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

		// setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		parseIntent(intent);

		if (!serviceInfoTaken) {
			getServiceInfo();
			serviceInfoTaken = true;
		}

		updateCountDaSincronizzare();
		
		if(!tokenIsValid){
			renewToken();
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
		showError(errorMessages);
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
	}
	
	@Override
	public void onStop(){
		super.onStop();
		//non vogliamo che la batteria si scarichi senza motivo
		LocationManager locationManager = (LocationManager) getSystemService(BaseActivity.LOCATION_SERVICE);
		locationManager.removeUpdates(locationListener);
	}

	private boolean legaRFIDGioco(int rfid, Gioco gioco) {
		gioco.rfid = rfid;
		gioco.hasDirtyData = true;
		currentGioco = gioco;
		return true;
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



	private void confirmLegaRFIDGioco(int rfid, Gioco gioco) {
		final int mrfid = rfid;
		final Gioco mgioco = gioco;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Vuoi associare l'RFID " + rfid + " al Gioco "
				+ gioco.idGioco + " ?");
		if (gioco.rfid > 0) {
			alert.setMessage("il Gioco " + gioco.idGioco
					+ " attualmente ha l'RFID " + gioco.rfid);
		} else {
			alert.setMessage("il Gioco " + gioco.idGioco
					+ " attualmente non ha RFID associati");
		}

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (legaRFIDGioco(mrfid, mgioco)) {
					feedback("Ok, ora il gioco " + mgioco.idGioco
							+ " è associato all'RFID " + mgioco.rfid);
					String currentTag = (String) actionBar.getSelectedTab()
							.getTag();
					ICustomFragment mf = (ICustomFragment) getFragmentManager()
							.findFragmentByTag(currentTag);
					mf.showStrutturaData(currentGioco);
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
			String[] pieces = out.split("://");

			String[] actualValues = pieces[1].split("/");

			int rfid = Integer.parseInt(actualValues[1]);
			// currentRFID = rfid;
			if (partitiDaID) {
				confirmLegaRFIDGioco(rfid, currentGioco);
			} else {

				if (currentGioco != null && currentGioco.hasDirtyData) {
					db.salvaGiocoLocally(currentGioco);
				}

				// String name = getString(R.string.display_gioco_id)
				// + currentRFID;
				// String ser = getString(R.string.display_gioco_seriale) + out;
				//
				// TextView giocoId = (TextView)
				// findViewById(R.id.display_gioco_id);
				// giocoId.setText(name);
				// TextView giocoSeriale = (TextView)
				// findViewById(R.id.display_gioco_seriale);
				// giocoSeriale.setText(ser);
				// Log.d(TAG, "Qualcosa è successo " + name + " " + res);
			}
		} catch (Exception e) {
			// Non è un intent che ci interessa in questo caso
		}
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

	private void parseIntent(Intent intent) {

	}

	private void getServiceInfo() {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_INFO_METHOD_NAME);
		startActivityForResult(serviceIntent, SOAP_SERVICE_INFO_REQUEST_CODE);
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
		startActivityForResult(serviceIntent, SOAP_SINCRONIZZA_TUTTO_REQUEST_CODE);

	}
	private void getStructureDataByID(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_GIOCO_ID_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idgioco", "" + id);
		currentQueriedId = id;
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, SOAP_GET_GIOCO_REQUEST_CODE_BY_ID);
	}
	private void getStructureFotoByID(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_FOTO_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("idGioco", "" + id);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, SOAP_GET_GIOCO_FOTO_REQUEST_CODE);
	}

	private void getStructureDataByRFID(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_GIOCO_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rfid", "" + id);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, SOAP_GET_GIOCO_REQUEST_CODE);
	}

	private void getStructureFoto(int id) {
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_FOTO_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("idGioco", "" + id);
		map.put("args0", "" + id);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, SOAP_GET_GIOCO_FOTO_REQUEST_CODE);
	}
	
	private void renewToken(){
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getApplicationContext(),
				SynchroSoapActivity.class);
		serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_LOGINUSER_METHOD_NAME);
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String username = prefs.getString(getString(R.string.settings_key_username), "UNSET");
		String password = prefs.getString(getString(R.string.settings_key_password), "UNSET");
		
		map.put("args0", username);
		map.put("args1", password);
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		startActivityForResult(serviceIntent, SOAP_GET_TOKEN_REQUEST_CODE);		
	}

	@Override
	public void onActivityResult(int requestCode, int returnCode, Intent intent) {
		HashMap<String, Object> res;
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		ICustomFragment mf = (ICustomFragment) getFragmentManager()
				.findFragmentByTag(currentTag);

		switch (requestCode) {
		case BaseActivity.SOAP_GET_TOKEN_REQUEST_CODE:
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
						startActivity(intent);
						return;
					}
					
				});
				changeCredentials.show();
				
			} else if(res != null && res.containsKey("mapped")){
				SOAPAutGiochi auth = (SOAPAutGiochi) res.get("mapped");
				int a = auth.autComune;
			}
			
			if(res!=null && res.containsKey("headerIn")){
				headerOut = (Element[]) res.get("headerIn");
			}
			
			break;
		case BaseActivity.SOAP_SINCRONIZZA_TUTTO_REQUEST_CODE:
			updateCountDaSincronizzare();
			break;
		case BaseActivity.SOAP_GET_GIOCO_REQUEST_CODE_BY_ID:
			res = SynchroSoapActivity.getRes(Constants.GET_GIOCO_ID_METHOD_NAME);
			Gioco remoteGioco = null;
			Gioco localGioco = null;
			if(res!=null && !res.containsKey("success")) {
				if(res.containsKey("mapped")){
					remoteGioco = new Gioco((it.opencontent.android.ocparchitn.SOAPMappings.SOAPGioco) res.get("mapped"),
							getApplicationContext());
				} else {
					remoteGioco = new Gioco(res.entrySet(),
							getApplicationContext());
				}
				localGioco = db.readGiocoLocallyByID(remoteGioco.idGioco);
			} else if(res.containsKey("success") && res.get("success").equals(false) ){
				/********************************************************
				 * TODO:                                                *
				 * raggruppare la gestione dell'errore in un punto solo *
				 ********************************************************/

				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Errore remoto nel recupero della struttura "+currentQueriedId);
				TextView content = new TextView(getApplicationContext());
				String outText = "";
				if(res.get("faultcode") != null ){
					outText += res.get("faultcode"); 
				}
				if(res.get("string") != null ){
					outText += "\n"+res.get("string"); 
				}
				if(res.get("exception") != null){
					KvmSerializable exception = (KvmSerializable) res.get("exception");
					if(exception.getClass().equals(SOAPSrvGiocoArkAutException.class)){
						outText += "\nCodice: "+((SOAPSrvGiocoArkAutException) exception).codice;
						outText += "\nMessaggio: "+((SOAPSrvGiocoArkAutException) exception).message;
					}else if(exception.getClass().equals(SOAPSrvGiocoArkGiochiException.class)){
						outText += "\nCodice: "+((SOAPSrvGiocoArkGiochiException) exception).codice;
						outText += "\nMessaggio: "+((SOAPSrvGiocoArkGiochiException) exception).message;
					} else {
						outText += "\nMessaggio: "+((SOAPSrvGiocoArkSrvException) exception).message;
					}
					
				}
				content.setText(outText);
				alert.setView(content);
				alert.setPositiveButton("OK", null);
				alert.show();
				/********************************
				 * Fine abbozzo gestione errori *
				 ********************************/
				
				
				remoteGioco = new Gioco();				
				localGioco = db.readGiocoLocallyByID(currentQueriedId);
			}else {
			 remoteGioco = new Gioco();				
			 localGioco = db.readGiocoLocallyByID(currentQueriedId);
			}
			
			
			
			//C'è da sistemare come viene gestita la concorrenza fra locale e remoto
			if (localGioco != null) {
				Toast.makeText(
						getApplicationContext(),
						"Gioco " + localGioco.idGioco
								+ " ha modifiche ancora non salvate",
						Toast.LENGTH_SHORT).show();
				currentGioco = localGioco;
				//Le foto le abbiamo già in locale
			} else if(PlatformChecks.siamoOnline(getApplicationContext()) && remoteGioco !=null){
				currentGioco = remoteGioco;
				//Lo showStruttura viene chiamato dal loop delle foto
				getStructureFoto(currentGioco.idGioco);			
			} else {
				currentGioco = db.readGiocoLocallyByID(remoteGioco.idGioco);
				if(currentGioco == null){
				currentGioco = new Gioco();
				currentGioco.idGioco = currentQueriedId;
				} 
			}
			
			mf.showStrutturaData(currentGioco);
			
			break;
		case BaseActivity.SOAP_GET_GIOCO_REQUEST_CODE:

			if (returnCode == RESULT_OK) {

				res = SynchroSoapActivity.getRes(Constants.GET_GIOCO_METHOD_NAME);

				if (res != null && res.size() > 0) {
					currentGioco = new Gioco(res.entrySet(), currentRFID,
							getApplicationContext());
					mf.showStrutturaData(currentGioco);
					getStructureFoto(currentGioco.idGioco);
				} else {
					mf.showStrutturaData(new Gioco());
					Toast.makeText(
							getApplicationContext(),
							getString(R.string.errore_generico_soap) + " "
									+ currentRFID, Toast.LENGTH_SHORT).show();
				}
			} else {
				// TODO: recuperare i dati dal device
				Toast.makeText(
						getApplicationContext(),
						"Recupero dei dati remoti impossibile: assenza di connettività",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case SOAP_GET_GIOCO_FOTO_REQUEST_CODE:
			if (returnCode == RESULT_OK) {
				res = SynchroSoapActivity.getRes(Constants.GET_FOTO_METHOD_NAME);
				currentGioco.addImmagine(res.entrySet());
			}
			mf.showStrutturaData(currentGioco);
			break;
		case FOTO_REQUEST_CODE:
			try {
				snapshot = CameraActivity.getImage();
				if (currentGioco == null) {
					currentGioco = new Gioco();
					currentGioco.sincronizzato = false;
					currentGioco.hasDirtyData = true;
					currentRFID = 0;
				}

				int whichOne = intent.getExtras().getInt(
						Constants.EXTRAKEY_FOTO_NUMBER);
				String filename = FileNameCreator.getSnapshotFullPath(
						currentRFID, whichOne);
				FileOutputStream fos = openFileOutput(filename,
						Context.MODE_PRIVATE);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();  
//				snapshot.compress(Bitmap.CompressFormat.PNG, 80, fos);
//				fos.close();
				snapshot.compress(Bitmap.CompressFormat.PNG, 80, stream);
				byte[] image = stream.toByteArray();

				ImageView mImageView = null;
				switch (whichOne) {
				case 0:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_0);
					currentGioco.foto0 = Base64.encodeToString(image, Base64.DEFAULT);
					break;
				case 1:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_1);
					currentGioco.foto1 = Base64.encodeToString(image, Base64.DEFAULT);
					break;
				case 2:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_2);
					currentGioco.foto2 = Base64.encodeToString(image, Base64.DEFAULT);
					break;
				case 3:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_3);
					currentGioco.foto3 = Base64.encodeToString(image, Base64.DEFAULT);
					break;
				case 4:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_4);
					currentGioco.foto4 = Base64.encodeToString(image, Base64.DEFAULT);
					break;
				}
				currentGioco.sincronizzato = false;
				currentGioco.hasDirtyData = true;

				if (snapshot != null && mImageView != null) {
					snapshots[whichOne] = snapshot;
					mImageView.setImageBitmap(snapshot);
				}
			} catch (NullPointerException e) {
				Log.d(TAG, "Immagine nulla");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case BaseActivity.SOAP_SERVICE_INFO_REQUEST_CODE:
			//TODO: rimappare l'oggetto info
			serviceInfo = SynchroSoapActivity.getRes(Constants.GET_INFO_METHOD_NAME);
			serviceInfoTaken = true;
			errorMessages.put(Constants.STATUS_MESSAGE_SERVER_STATUS,
					"Connessione al server: OK");
			showError(errorMessages);
			break;
			default:
				super.onActivityResult(requestCode, returnCode, intent);
				break;

		}

	}

	public static Gioco getCurrentGioco() {
		return currentGioco;

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
		String currentTag = (String) actionBar.getSelectedTab().getTag();
		ICustomFragment f = (ICustomFragment) getFragmentManager()
				.findFragmentByTag(currentTag);
		f.showError(map);
	}

	public void takeSnapshot(View button) {
		Intent customCamera = new Intent(Constants.TAKE_SNAPSHOT);
		int whichOne = Integer.parseInt((String) button.getTag());
		customCamera.putExtra(Constants.EXTRAKEY_FOTO_NUMBER, whichOne);
		customCamera.setClass(getApplicationContext(), CameraActivity.class);
		Log.d(TAG, customCamera.getAction());
		startActivityForResult(customCamera, BaseActivity.FOTO_REQUEST_CODE);
	}

	public static float getCurrentLon() {
		return currentLon;
	}

	public static float getCurrentLat() {
		return currentLat;
	}

	public static class CustomTabListener<T extends Fragment> implements
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
				showError(errorMessages);
				break;

			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

//				Log.d(TAG, "onGpsStatusChanged Satellite");
				break;

			case GpsStatus.GPS_EVENT_STARTED:

				Log.d(TAG, "onGpsStatusChanged Started");
				errorMessages.put(Constants.STATUS_MESSAGE_GPS_STATUS,
						Constants.STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXING);
				showError(errorMessages);
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
			showError(errorMessages);
			currentLat = (float) location.getLatitude();
			currentLon = (float) location.getLongitude();
			if(currentGioco != null){
				currentGioco.gpsx = currentLon;
				currentGioco.gpsy = currentLat;
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
