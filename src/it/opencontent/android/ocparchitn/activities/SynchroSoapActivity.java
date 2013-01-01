package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.FotoUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.GiocoUpdate;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.services.IRemoteConnection;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import it.opencontent.android.ocparchitn.utils.SoapConnector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SynchroSoapActivity extends Activity implements IRemoteConnection {

	private static final String TAG = SynchroSoapActivity.class.getSimpleName();
	private static String methodName;
	private static HashMap<String, Object> requestParameters;
	private static HashMap<String, Object> res;
	private static Thread remoteThread;

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
		if (methodName.equals(Constants.EXTRAKEY_SYNC_ALL)) {
			OCParchiDB db = new OCParchiDB(getApplicationContext());
			LinkedHashMap<String, Struttura> set = db
					.getStruttureDaSincronizzare();
			if (!set.isEmpty() && PlatformChecks.siamoOnline(getApplicationContext())) {
				Iterator<String> keyIterator = set.keySet().iterator();
				while (keyIterator.hasNext()) {
					String k = keyIterator.next();
					Struttura s = set.get(k);
					if (s.getClass().equals(Gioco.class)) {
						Gioco g = (Gioco) s;

						HashMap<String, Object> map = new HashMap<String, Object>();
						GiocoUpdate gu = new GiocoUpdate();
						gu.id_gioco = "" + g.id_gioco;
						if (g.rfid > 0) {
							gu.rfid = "" + g.rfid;
						}
						gu.gpsx = "" + g.gpsx;
						gu.gpsy = "" + g.gpsy;
						gu.tabletUserName = "Utente Tablet";

						// map.put("tabletDataModifica",);
						// String uuid =
						// Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
						String uuid = "Android tablet n° 1234567890";
						gu.tabletDispositivoName = uuid;
						gu.note = g.note;

						// map.put("tabletDispositivoName",newuuid);
						// map.put("tabletTimeModifica",);
						// map.put("tabletUserName", "Utente Di Test");

						map.put("Giocoupdate", gu);
						returnResponse("setGioco", map, false);

						// cicliamo le foto eventuali

						if (g.foto0 != null && !g.foto0.equals("")) {
							FotoUpdate fu = new FotoUpdate();
							fu.idGioco = "" + g.id_gioco;
							fu.sovrascrittura = true;
							fu.estensioneImmagine = "jpg";
							fu.immagine = g.foto0;
							fu.nomeImmagine = "gioco_" + g.id_gioco + "_foto_0";
							map = new HashMap<String, Object>();
							map.put("Fotoupdate", fu);
							returnResponse("setFoto", map, false);
						}
						if (g.foto1 != null && !g.foto1.equals("")) {
							FotoUpdate fu = new FotoUpdate();
							fu.idGioco = "" + g.id_gioco;
							fu.sovrascrittura = true;
							fu.estensioneImmagine = "jpg";
							fu.immagine = g.foto1;
							fu.nomeImmagine = "gioco_" + g.id_gioco + "_foto_1";
							map = new HashMap<String, Object>();
							map.put("Fotoupdate", fu);
							returnResponse("setFoto", map, false);
						}
						if (g.foto2 != null && !g.foto2.equals("")) {
							FotoUpdate fu = new FotoUpdate();
							fu.idGioco = "" + g.id_gioco;
							fu.sovrascrittura = true;
							fu.estensioneImmagine = "jpg";
							fu.immagine = g.foto2;
							fu.nomeImmagine = "gioco_" + g.id_gioco + "_foto_2";
							map = new HashMap<String, Object>();
							map.put("Fotoupdate", fu);
							returnResponse("setFoto", map, false);
						}
						if (g.foto3 != null && !g.foto3.equals("")) {
							FotoUpdate fu = new FotoUpdate();
							fu.idGioco = "" + g.id_gioco;
							fu.sovrascrittura = true;
							fu.estensioneImmagine = "jpg";
							fu.immagine = g.foto3;
							fu.nomeImmagine = "gioco_" + g.id_gioco + "_foto_3";
							map = new HashMap<String, Object>();
							map.put("Fotoupdate", fu);
							returnResponse("setFoto", map, false);
						}
						if (g.foto4 != null && !g.foto4.equals("")) {
							FotoUpdate fu = new FotoUpdate();
							fu.idGioco = "" + g.id_gioco;
							fu.sovrascrittura = true;
							fu.estensioneImmagine = "jpg";
							fu.immagine = g.foto4;
							fu.nomeImmagine = "gioco_" + g.id_gioco + "_foto_4";
							map = new HashMap<String, Object>();
							map.put("Fotoupdate", fu);
							returnResponse("setFoto", map, false);
						}

						db.marcaStrutturaSincronizzata(g);
					}
				}
			} else {
				setResult(RESULT_OK,getIntent());
				finish();
			}
			// Messo nel thread e comandato dalla queuelength
			// finish();
		} else {
			returnResponse(methodName, requestParameters, true);
		}
	}

	@Override
	public void sendRequest(Object data) {
		// TODO Auto-generated method stub

	}

	public static HashMap<String, Object> getRes() {
		return res;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (remoteThread != null && remoteThread.isAlive()
				&& !remoteThread.isInterrupted()) {
			remoteThread.interrupt();
		}
	}

	public final void updateCounter(int amount) {
		TextView counterView = (TextView) findViewById(R.id.remote_loading_dialog_counter);
		if (counterView != null) {
			counterView.setText(" Elementi in coda : " + queueLength);
		}
	}

	@Override
	public void returnResponse(String method, HashMap<String, Object> data,
			boolean f) {
		methodName = method;
		final boolean finish = f;
		final Handler h = new Handler();

		if (data == null) {
			data = new HashMap<String, Object>();
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
		// TODO Auto-generated method stub
		if (PlatformChecks.siamoOnline(this.getApplicationContext())) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					SoapConnector sc = new SoapConnector();
					try {
						res = sc.soap(methodName, Constants.SOAP_ENDPOINT,
								Constants.SOAP_NAMESPACE, Constants.SOAP_URL,
								properties);
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
						if (finish || queueLength <= 0) {
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

			Log.d(TAG, "Network non connesso");
			if (finish) {
				setResult(RESULT_CANCELED, getIntent());
				finish();
			}
		}
	}

}
