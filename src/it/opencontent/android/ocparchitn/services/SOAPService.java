package it.opencontent.android.ocparchitn.services;

import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import it.opencontent.android.ocparchitn.utils.SoapConnector;

import java.io.IOException;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SOAPService extends Service implements IRemoteConnection {

	// private String METHOD_NAME = "getInfo";
	private String METHOD_NAME = "getGioco";
	private String SOAP_ACTION = "https://webapps.comune.trento.it/parcogiochiSrv/";
	private String NAMESPACE = "http://db.comune.trento.it";
	private String URL = "https://webapps.comune.trento.it/parcogiochiSrv/services/SrvGioco?wsdl";

	// private String METHOD_NAME = "TopGoalScorers";
	// private String SOAP_ACTION =
	// "http://footballpool.dataaccess.eu/data/TopGoalScorers";
	// private String NAMESPACE = "http://footballpool.dataaccess.eu";
	// private String URL =
	// "http://footballpool.dataaccess.eu/data/info.wso?WSDL";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		returnResponse(null);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void sendRequest(Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void returnResponse(Object data) {
		PropertyInfo pi = new PropertyInfo();
		pi.setName("rfid");
		pi.setValue("2");
		final PropertyInfo[] properties = { pi };
		// TODO Auto-generated method stub
		if (PlatformChecks.siamoOnline(this.getApplicationContext())) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					SoapObject res = null;
					SoapConnector sc = new SoapConnector();
					try {
						res = sc.soap(METHOD_NAME, SOAP_ACTION, NAMESPACE, URL,
								properties);
						// TODO: creare l'handler nel servizio e ritornargli il
						// risultato
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			new Thread(runnable).start();
		}

	}

}
