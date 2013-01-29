package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAreaUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPFotoupdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGiocoUpdate;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.services.IRemoteConnection;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import it.opencontent.android.ocparchitn.utils.SoapConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SynchroSoapActivity extends Activity implements IRemoteConnection {

	private static final String TAG = SynchroSoapActivity.class.getSimpleName();
	private String methodName;
	private HashMap<String, Object> requestParameters;
	private HashMap<String, Object> res;
	private Thread remoteThread;
	private static OCParchiDB db;
	private static HashMap<String,HashMap<String,Object>> allmaps = new HashMap<String, HashMap<String,Object>>();

	private static int queueLength = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.remote_loading_dialog);

		Intent intent = getIntent();
		queueLength = 0;

		methodName = (String) intent.getExtras().get(
				Constants.EXTRAKEY_METHOD_NAME);
		requestParameters = (HashMap<String, Object>) intent.getExtras().get(
				Constants.EXTRAKEY_DATAMAP);
		db = new OCParchiDB(getApplicationContext());
		
		if (methodName.equals(Constants.EXTRAKEY_SYNC_ALL)) {
			
			LinkedHashMap<String, Struttura> set = db
					.getStruttureDaSincronizzare();
			if (!set.isEmpty() && PlatformChecks.siamoOnline(getApplicationContext())) {
				Iterator<String> keyIterator = set.keySet().iterator();
				while (keyIterator.hasNext()) {
					String k = keyIterator.next();
					Struttura s = set.get(k);
						sincronizzaLaStruttura(s);
				}
			} else {
				setResult(RESULT_OK,getIntent());
				finish();
			}
			// Messo nel thread e comandato dalla queuelength
			// finish();
		} else if(methodName.equals(Constants.GET_TABELLA_METHOD_NAME)){
			//facciamo un loop delle tabelle e le facciamo accumulare
			for(int tid : Constants.ID_TABELLE_SUPPORTO){
				HashMap<String, Object> thismap = new HashMap<String, Object>();
				thismap.put("args0", "" + tid);
				/**
				 * Il @methodName in questo ciclo deve essere allineato con il methodname 
				 * nella main activity che va a sbobinare i risultati di questo stesso ciclo
				 * L'idea è che qui facciamo partire N chiamate asincrone
				 * L'ultima lancia il finish() di questa activity
				 * L'onResult della main si va a prendere i risultati con un ciclo identico
				 */
				getRemoteResponse(methodName, thismap, false,methodName+"_"+tid);	
			}
			
		}else {
			getRemoteResponse(methodName, requestParameters, true,methodName);
		}
		
	}

	/**
	 * @param db
	 * @param s
	 */
	private void sincronizzaLaStruttura(Struttura s) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(s.getClass().equals(Gioco.class)){
			Gioco g = (Gioco) s;
	
			SOAPGiocoUpdate gu = new SOAPGiocoUpdate();
			gu.idGioco = "" + g.idGioco;
			if (g.rfid > 0) {
				gu.rfid = "" + g.rfid;
				gu.gpsx = "" + g.gpsx;
				gu.gpsy = "" + g.gpsy;
				gu.note = g.note;
				gu.rfidArea = "" + g.rfidArea;
				gu.posizioneRfid = ""+ 
				map.put("Giocoupdate", gu);
				getRemoteResponse(Constants.SET_GIOCO_METHOD_NAME, map, false,Constants.SET_GIOCO_METHOD_NAME);
				sincronizzaTutteLeFoto(g);
			}else {
				Log.d(TAG,"Gioco senza rfid, non sincronizzato "+g.idGioco);
				if (queueLength <= 0) {
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		} else if(s.getClass().equals(Area.class)){
			Area a = (Area) s;
			SOAPAreaUpdate au = new SOAPAreaUpdate();
			if(a.rfidArea>0){
				au.idArea = "" + a.idArea;
				au.idParco = "" + a.idParco ;
				au.note = a.note;
				au.rfid = "" + a.rfidArea;
				au.spessore = "" + a.spessore;
				au.superficie = "" + a.superficie;
				au.tipoPavimentazione = "" + a.tipoPavimentazione;
				map.put("Areaupdate", au);
				getRemoteResponse(Constants.SET_AREA_METHOD_NAME, map, false,Constants.SET_AREA_METHOD_NAME);
				sincronizzaTutteLeFoto(a);				
			}else {
				Log.d(TAG,"Area senza rfid, non sincronizzato "+a.idArea);
				if (queueLength <= 0) {
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		}
	}

	/**
	 * @param g
	 */
	private void sincronizzaTutteLeFoto(Struttura g) {
		HashMap<String, Object> map;
		// cicliamo le foto eventuali
		String tipoFoto = "";
		String idRiferimento = "";
		String suffissoImmagine = "";
		if(g.getClass().equals(Gioco.class)){
			tipoFoto = Constants.CODICE_STRUTTURA_GIOCO+"";
			idRiferimento = g.idGioco +"";
			suffissoImmagine = "gioco_";
		}else if(g.getClass().equals(Area.class)){
			tipoFoto = Constants.CODICE_STRUTTURA_AREA+"";			
			idRiferimento = ((Area) g).idArea+"";
			suffissoImmagine = "area_";
		}
		

		SOAPFotoupdate fu = new SOAPFotoupdate();
		fu.tipoFoto = tipoFoto;
		fu.idRiferimento = idRiferimento;
		fu.sovrascrittura = true;
		fu.estensioneImmagine = "jpg";
		
		if (g.foto0 != null && !g.foto0.equals("")) {
			fu.immagine = g.foto0;
			fu.nomeImmagine = suffissoImmagine + idRiferimento + "_foto_0";
			map = new HashMap<String, Object>();
			map.put("Fotoupdate", fu);
			getRemoteResponse("setFoto", map, false,"setFoto");
		}
		if (g.foto1 != null && !g.foto1.equals("")) {
			fu.immagine = g.foto1;
			fu.nomeImmagine = suffissoImmagine + idRiferimento + "_foto_1";
			map = new HashMap<String, Object>();
			map.put("Fotoupdate", fu);
			getRemoteResponse("setFoto", map, false,"setFoto");
		}
		if (g.foto2 != null && !g.foto2.equals("")) {
			fu.immagine = g.foto2;
			fu.nomeImmagine = suffissoImmagine + idRiferimento + "_foto_2";
			map = new HashMap<String, Object>();
			map.put("Fotoupdate", fu);
			getRemoteResponse("setFoto", map, false,"setFoto");
		}
		if (g.foto3 != null && !g.foto3.equals("")) {
			fu.immagine = g.foto3;
			fu.nomeImmagine = suffissoImmagine + idRiferimento + "_foto_3";
			map = new HashMap<String, Object>();
			map.put("Fotoupdate", fu);
			getRemoteResponse("setFoto", map, false,"setFoto");
		}
		if (g.foto4 != null && !g.foto4.equals("")) {
			fu.immagine = g.foto4;
			fu.nomeImmagine = suffissoImmagine + idRiferimento + "_foto_4";
			map = new HashMap<String, Object>();
			map.put("Fotoupdate", fu);
			getRemoteResponse("setFoto", map, false,"setFoto");
		}

	}

	@Override
	public void sendRequest(Object data) {
		// TODO Auto-generated method stub

	}

	public static HashMap<String, Object> getRes(String key) {
		HashMap<String,Object> ret; 
		if(allmaps.containsKey(key)){
			ret = allmaps.get(key);
		allmaps.remove(key);
		} else {
			ret = null;
		}
		return ret;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (remoteThread != null && remoteThread.isAlive()
				&& !remoteThread.isInterrupted()) {
			remoteThread.interrupt();
		}
		db.close();
	}

	public final void updateCounter(int amount) {
		TextView counterView = (TextView) findViewById(R.id.remote_loading_dialog_counter);
		if (counterView != null) {
			counterView.setText(" Elementi in coda : " + (queueLength+1));
		}
	}

	@Override
	public void getRemoteResponse(String m, HashMap<String, Object> d,
			boolean f,String md) {

		//Abbiamo bisogno di tutti questi elementi nel runnable
		//per cui li definiamo come final
		
		final String method = m;
		final boolean finish = f;
		final Handler h = new Handler();
		final String mapid = md; 
		final HashMap<String,Object> data;
		
		if (d == null) {
			data = new HashMap<String, Object>();
		} else {
			data = d;
			
		}

		if (!finish) {
			queueLength++;
		}
		updateCounter(queueLength);

		final PropertyInfo[] properties = new PropertyInfo[data.entrySet()
				.size()];
		int i = 0;
		Iterator<Entry<String, Object>> iterator = data.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			PropertyInfo pi = new PropertyInfo();
			pi.setName(entry.getKey());
			pi.setType(entry.getValue().getClass());
			pi.setValue(entry.getValue());
			properties[i] = pi;
			i++;
		}
		if (PlatformChecks.siamoOnline(this.getApplicationContext())) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					SoapConnector sc = new SoapConnector();
					try {
						res = sc.soap(method, Constants.SOAP_ENDPOINT,
								Constants.SOAP_NAMESPACE, Constants.SOAP_URL,
								properties);

//							allmaps.put(method, res);
							allmaps.put(mapid, res);
							if(method.equals(Constants.SET_AREA_METHOD_NAME)
							|| method.equals(Constants.SET_GIOCO_METHOD_NAME)){
								db.eliminaCopiaLocaleDiStrutturaSincronizzata(data);
							}
						
						if (!finish) {
							queueLength--;
						}
						h.post(new Runnable() {
							public void run() {
								updateCounter(queueLength);
							}
						});
						// updateCounter(queueLength);
						if (finish || queueLength <= 0) {
							setResult(RESULT_OK, getIntent());
							finish();
						}
					} catch (IOException e) {
						queueLength--;
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						queueLength--;
						e.printStackTrace();
					} catch (Exception e) {
						queueLength--;
						e.printStackTrace();
					} finally {
						res = sc.getMap();
						if (finish || queueLength <= 0) {
							allmaps.put(method, res);
							setResult(RESULT_CANCELED, getIntent());
							finish();
						}
					}
				}
			};
			remoteThread = new Thread(runnable);
			remoteThread.start();

		} else {
			if (res == null) {
				res = new HashMap<String, Object>();
			}
			res.put("Errore", "Network non connesso");

			Button wirelessConfig = new Button(getApplicationContext());
			wirelessConfig.setText("Vai al centro connessioni");
			wirelessConfig.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(
							Settings.ACTION_WIRELESS_SETTINGS),
							BaseActivity.SETUP_NETWORK);
				}
			});

			res.put("Azioni possibili:", wirelessConfig);
			allmaps.put(method, res);
			Log.d(TAG, "Network non connesso");
			if (finish) {
				setResult(RESULT_CANCELED, getIntent());
				finish();
			}
		}
	}

}
