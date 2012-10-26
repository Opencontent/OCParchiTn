package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Intents;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.services.IRemoteConnection;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import it.opencontent.android.ocparchitn.utils.SoapConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class SynchroSoapActivity extends Activity implements IRemoteConnection {

	private static String methodName;
	private static HashMap<String, Object> requestParameters;
	private static HashMap<String, Object> res;
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		methodName = (String) intent.getExtras().get(Intents.EXTRAKEY_METHOD_NAME);
		requestParameters = (HashMap<String,Object>) intent.getExtras().get(Intents.EXTRAKEY_DATAMAP);
		returnResponse(methodName, requestParameters);
	}
	
	@Override
	public void sendRequest(Object data) {
		// TODO Auto-generated method stub

	}

	public static HashMap<String,Object> getRes(){
		return res;
	}
	
	@Override
	public void returnResponse(String method, HashMap<String, Object> data) {
		methodName = method;
		final PropertyInfo[] properties = new PropertyInfo[data.entrySet().size()];
		int i =0 ;
		Iterator<Entry<String, Object>> iterator =data.entrySet().iterator(); 
		while( iterator.hasNext()){
			Entry<String,Object> entry = iterator.next();
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
								getString(R.string.soap_url),
								properties);
						setResult(RESULT_OK, getIntent());
						finish();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} finally {
						setResult(RESULT_CANCELED, getIntent());
						finish();						
					}
				}
			};
			new Thread(runnable).start();
		}
	}

}
