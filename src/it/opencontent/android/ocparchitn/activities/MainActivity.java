package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Intents;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.fragments.MainFragment;
import it.opencontent.android.ocparchitn.fragments.PeriodicaFragment;
import it.opencontent.android.ocparchitn.fragments.RendicontazioneFragment;
import it.opencontent.android.ocparchitn.fragments.SpostamentoFragment;
import it.opencontent.android.ocparchitn.utils.FileManagement;
import it.opencontent.android.ocparchitn.utils.FileNameCreator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static NfcAdapter nfca;
	private static PendingIntent pi;
	private static IntentFilter[] ifa;
	private static String[][] techListsArray;
	private static boolean serviceInfoTaken = false;

	private Bitmap snapshot;
	private OCParchiDB db;

	private static HashMap<String, Object> serviceInfo;

	private static Gioco currentGioco;
	private static Bitmap[] snapshots = new Bitmap[Intents.MAX_SNAPSHOTS_AMOUNT];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main_fragments);

		db = new OCParchiDB(getApplicationContext());
		int pending = db.getPendingSynchronizations();

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getString(R.string.title_activity_main) + " ("
				+ pending + ")");

		Tab tab;
		tab = actionBar
				.newTab()
				.setText(getString(R.string.fragment_title_rilevazione))
				.setTabListener(
						new CustomTabListener<MainFragment>(this,
								"rilevazione", MainFragment.class));
		actionBar.addTab(tab);
		tab = actionBar
				.newTab()
				.setText(getString(R.string.fragment_title_attivita_periodica))
				.setTabListener(
						new CustomTabListener<PeriodicaFragment>(this,
								"periodica", PeriodicaFragment.class));
		actionBar.addTab(tab);
		tab = actionBar
				.newTab()
				.setText(
						getString(R.string.fragment_title_rendicontazione_manutenzione))
				.setTabListener(
						new CustomTabListener<RendicontazioneFragment>(this,
								"rendicontazione",
								RendicontazioneFragment.class));
		actionBar.addTab(tab);
		tab = actionBar
				.newTab()
				.setText(getString(R.string.fragment_title_spostamento_gioco))
				.setTabListener(
						new CustomTabListener<SpostamentoFragment>(this,
								"spostamento", SpostamentoFragment.class));
		actionBar.addTab(tab);

		if (!serviceInfoTaken) {
			getServiceInfo();
			serviceInfoTaken = true;
		}

		// setContentView(R.layout.activity_main);

		Intent intent = getIntent();
		parseIntent(intent);

		// LocationManager locationManager = (LocationManager)
		// getSystemService(BaseActivity.LOCATION_SERVICE);
		// List<String> locationProviders = locationManager.getProviders(true);
		//
		//

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

	@Override
	public void onPause() {
		super.onPause();
		nfca.disableForegroundDispatch(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		nfca.enableForegroundDispatch(this, pi, ifa, techListsArray);
		// nfca.enableForegroundDispatch(this, pi, null, null);
	}

	@Override
	public void onStart() {
		super.onStart();
		// nfca.disableForegroundDispatch(this);
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
			currentRFID = Integer.parseInt(actualValues[1]);

			if(currentGioco != null && !currentGioco.hasDirtyData){
				db.storeGiocoLocally(currentGioco);
			}
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

	private void parseIntent(Intent intent) {

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

	@Override
	public void onActivityResult(int requestCode, int returnCode, Intent intent) {
		switch (requestCode) {
		case BaseActivity.SOAP_GET_GIOCO_REQUEST_CODE:

			if (returnCode == RESULT_OK) {

				HashMap<String, Object> res = SynchroSoapActivity.getRes();
				MainFragment mf = (MainFragment) getFragmentManager()
						.findFragmentByTag("rilevazione");// (R.id.activity_main);

				if (res != null && res.size() > 0) {
					currentGioco = new Gioco(res.entrySet());
					Bitmap bmp = null;
					for(int i = 0;i< Intents.MAX_SNAPSHOTS_AMOUNT; i++){
						String filename = FileNameCreator.getSnapshotFullPath(currentRFID, i);
						try {
							bmp = BitmapFactory.decodeStream(openFileInput(filename));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							bmp = null;
							e.printStackTrace();
						} 
						switch(i){
						case 0:
							currentGioco.foto0 = bmp;
							break;
						case 1:
							currentGioco.foto1 = bmp;
							break;
						case 2:
							currentGioco.foto2 = bmp;
							break;
						case 3:
							currentGioco.foto3 = bmp;
							break;
						case 4:
							currentGioco.foto4 = bmp;
							break;
						}
					}
					mf.showGiocoData(currentGioco);
				} else {
					mf.showGiocoData(new Gioco());
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
		case BaseActivity.FOTO_REQUEST_CODE:
			try {
				snapshot = CameraActivity.getImage();
				if (currentGioco == null) {
					currentGioco = new Gioco();
					currentGioco.sincronizzato = false;
					currentGioco.hasDirtyData = true;
					currentRFID = 0;
				}
				

				int whichOne = intent.getExtras().getInt(
						Intents.EXTRAKEY_FOTO_NUMBER);
				String filename = FileNameCreator.getSnapshotFullPath(currentRFID, whichOne);
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				snapshot.compress(Bitmap.CompressFormat.PNG, 80, fos);
				fos.close();
		
				ImageView mImageView = null;
				switch (whichOne) {
				case 0:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_0);
					currentGioco.foto0 = snapshot;
					currentGioco.sincronizzato = false;
					currentGioco.hasDirtyData = true;
					break;
				case 1:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_1);
					currentGioco.foto1 = snapshot;
					currentGioco.sincronizzato = false;
					currentGioco.hasDirtyData = true;
					break;
				case 2:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_2);
					currentGioco.foto2 = snapshot;
					currentGioco.sincronizzato = false;
					currentGioco.hasDirtyData = true;
					break;
				case 3:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_3);
					currentGioco.foto3 = snapshot;
					currentGioco.sincronizzato = false;
					currentGioco.hasDirtyData = true;
					break;
				case 4:
					mImageView = (ImageView) findViewById(R.id.snapshot_gioco_4);
					currentGioco.foto4 = snapshot;
					currentGioco.sincronizzato = false;
					currentGioco.hasDirtyData = true;
					break;
				}

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
			serviceInfo = SynchroSoapActivity.getRes();
			serviceInfoTaken = true;
			break;

		}

	}

	public static Gioco getCurrentGioco() {
		return currentGioco;
	}

	public void test(View v) {
		Log.d(TAG, "cliccata " + v.getId());
	}

	public void takeSnapshot(View button) {
		Intent customCamera = new Intent(Intents.TAKE_SNAPSHOT);
		int whichOne = Integer.parseInt((String) button.getTag());
		customCamera.putExtra(Intents.EXTRAKEY_FOTO_NUMBER, whichOne);
		customCamera.setClass(getApplicationContext(), CameraActivity.class);
		Log.d(TAG, customCamera.getAction());
		startActivityForResult(customCamera, BaseActivity.FOTO_REQUEST_CODE);
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

		/* The following are each of the ActionBar.TabListener callbacks */

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				
				mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
				if(mFragment == null){
					mFragment = Fragment.instantiate(mActivity, mClass.getName());
					ft.add(android.R.id.content, mFragment, mTag);
				}
			} else {
				// If it exists, simply attach it in order to show it
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
}
