package it.opencontent.android.ocparchitn.services;

import it.opencontent.android.ocparchitn.Intents;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import it.opencontent.android.ocparchitn.utils.SoapConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SOAPService extends Service implements IRemoteConnection {

	// private String METHOD_NAME = "getInfo";
	private static String methodName;
	/*
	 * private String SOAP_ACTION =
	 * "https://webapps.comune.trento.it/parcogiochiSrv/"; private String
	 * NAMESPACE = "http://db.comune.trento.it"; private String URL =
	 * "https://webapps.comune.trento.it/parcogiochiSrv/services/SrvGioco?wsdl";
	 */

	private static HashMap<String, Object> res = null;
	private static HashMap<String, Object> requestParameters = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		methodName = (String) intent.getExtras().get(
				Intents.EXTRAKEY_METHOD_NAME);
		requestParameters = (HashMap<String, Object>) intent.getExtras().get(
				Intents.EXTRAKEY_DATAMAP);
		returnResponse(methodName, requestParameters);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void sendRequest(Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void returnResponse(String method, HashMap<String, Object> data) {
		methodName = method;
		final PropertyInfo[] properties = new PropertyInfo[data.entrySet()
				.size()];
		int i = 0;
		Iterator<Entry<String, Object>> iterator = data.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			PropertyInfo pi = new PropertyInfo();
			pi.setName(entry.getKey());
			pi.setValue(entry.getValue().toString());
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
								getString(R.string.soap_endpoint),
								getString(R.string.soap_namespace),
								getString(R.string.soap_url), properties);
						return;
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					}
				}
			};
			new Thread(runnable).start();
		} else {

		}

	}

}
