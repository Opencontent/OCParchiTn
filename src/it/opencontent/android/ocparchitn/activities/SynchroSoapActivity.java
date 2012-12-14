package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.services.IRemoteConnection;
import it.opencontent.android.ocparchitn.utils.GiocoUpdate;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import it.opencontent.android.ocparchitn.utils.SoapConnector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class SynchroSoapActivity extends Activity implements IRemoteConnection {

	private static final String TAG = SynchroSoapActivity.class.getSimpleName();
	private static String methodName;
	private static HashMap<String, Object> requestParameters;
	private static HashMap<String, Object> res;
	private static Thread remoteThread;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.remote_loading_dialog);

		Intent intent = getIntent();

		methodName = (String) intent.getExtras().get(
				Constants.EXTRAKEY_METHOD_NAME);
		requestParameters = (HashMap<String, Object>) intent.getExtras().get(
				Constants.EXTRAKEY_DATAMAP);
		if (methodName.equals(Constants.EXTRAKEY_SYNC_ALL)) {
			OCParchiDB db = new OCParchiDB(getApplicationContext());
			LinkedHashMap<String, Struttura> set = db
					.getStruttureDaSincronizzare();
			String a = "";
			if (!set.isEmpty()) {
				Iterator<String> keyIterator = set.keySet().iterator();
				while (keyIterator.hasNext()) {
					String k = keyIterator.next();
					Struttura s = set.get(k);
					if (s.getClass().equals(Gioco.class)) {
						Gioco g = (Gioco) s;
						// setGioco
						// <xs:element minOccurs="0" name="gpsx" nillable="true"
						// type="xs:string"/>
						// <xs:element minOccurs="0" name="gpsy" nillable="true"
						// type="xs:string"/>
						// <xs:element minOccurs="0" name="id_gioco"
						// nillable="true" type="xs:string"/>
						// <xs:element minOccurs="0" name="note" nillable="true"
						// type="xs:string"/>
						// <xs:element minOccurs="0" name="rfid" nillable="true"
						// type="xs:string"/>
						// <xs:element minOccurs="0" name="tabletDataModifica"
						// nillable="true" type="xs:date"/>
						// <xs:element minOccurs="0"
						// name="tabletDispositivoName" nillable="true"
						// type="xs:string"/>
						// <xs:element minOccurs="0" name="tabletTimeModifica"
						// nillable="true" type="xs:string"/>
						// <xs:element minOccurs="0" name="tabletUserName"
						// nillable="true" type="xs:string"/>

						
						HashMap<String, Object> map = new HashMap<String, Object>();
						GiocoUpdate  gu = new GiocoUpdate();
						gu.id_gioco = "9";
						if (g.rfid > 0) {
							gu.rfid = ""+g.rfid;
						}
						gu.gpsx = "33.3";
						gu.gpsy ="44.4";
						gu.tabletUserName = "Qualcun altro";
						 
						// map.put("tabletDataModifica",);
						String uuid = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
						Log.d(TAG,"Questo uuid: "+uuid);
						String newuuid = "nomd5";
						try {
							gu.note	= URLEncoder.encode("Nota di test per verificare lunghezza del campo e encoding: èòàùùì ßðđŋħ üäëö ¹²³¼½ ¡⅜⅝⅞™ Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec mattis elit enim. Ut accumsan nulla nec dui vestibulum gravida. Integer quis libero elit, eu sagittis ante. Integer molestie blandit lacus quis consequat. Aliquam odio sem, ultricies ut iaculis eget, porttitor sed justo. Mauris cursus velit in dolor molestie sed semper odio faucibus. Pellentesque erat risus, ultricies at ullamcorper at, lobortis eu leo. Duis imperdiet pharetra turpis quis eleifend. Vivamus turpis ligula, posuere elementum bibendum quis, facilisis sed elit. Fusce a erat vitae turpis lobortis ullamcorper ac vitae ante. Nulla scelerisque, ipsum ut feugiat porttitor, risus sem convallis ligula, non cursus enim augue in nisi. In hac habitasse platea dictumst. Vestibulum sit amet nulla elit, ac tristique nunc. Fusce sem odio, pellentesque non interdum sit amet, molestie eget lectus. Nulla turpis arcu, volutpat non egestas non, vulputate quis eros. Fusce eget urna metus.","UTF-8");
							gu.tabletDispositivoName = Base64.encodeToString(MessageDigest.getInstance("MD5").digest(uuid.getBytes()),Base64.NO_WRAP);
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							gu.note = e1.getMessage();
						}
						//map.put("tabletDispositivoName",newuuid);
						// map.put("tabletTimeModifica",);
						//map.put("tabletUserName", "Utente Di Test");


						map.put("Giocoupdate",gu);
						returnResponse("setGioco", map, false);

					}
				}
			}

			finish();
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

	@Override
	public void returnResponse(String method, HashMap<String, Object> data,
			boolean f) {
		methodName = method;
		final boolean finish = f;
		if (data == null) {
			data = new HashMap<String, Object>();
		}

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
						res = sc.soap(methodName,
								Constants.SOAP_ENDPOINT,
								Constants.SOAP_NAMESPACE,
								Constants.SOAP_URL, properties);
						if (finish) {
							setResult(RESULT_OK, getIntent());
							finish();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (finish) {
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
