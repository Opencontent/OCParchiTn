package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.SOAPMappings.FotoUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.Fotografia;
import it.opencontent.android.ocparchitn.SOAPMappings.GiocoUpdate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class SoapConnector {

	private static final String USERNAME = "PG_CED";
	private static final String PASSWORD = "euforia";
	private static final String TAG = SoapConnector.class.getSimpleName();

	public HashMap<String, Object> soap(String METHOD_NAME, String SOAP_ACTION,
			String NAMESPACE, String URL, PropertyInfo[] properties)
			throws IOException, XmlPullParserException {

		HashMap<String, Object> map = new HashMap<String, Object>();

		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME); // set up
																		// request
		if (properties != null && properties.length > 0) {
			for (PropertyInfo p : properties) {
				request.addProperty(p);
			}
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); // put all required data into a soap
										// envelope
		envelope.setOutputSoapObject(request); // prepare request

		
		//new MarshalDouble().register(envelope);
       	new MarshalBase64().register(envelope);   //serialization
       	envelope.encodingStyle = SoapEnvelope.ENC;
       	
       envelope.bodyOut = request;
       envelope.dotNet = true; 
       envelope.setOutputSoapObject(request);
       envelope.setAddAdornments(false);
       envelope.implicitTypes= true;
		

		envelope.addMapping("http://gioco.parcogiochi/xsd", "Giocoupdate", GiocoUpdate.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Fotoupdate", FotoUpdate.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Fotografia", Fotografia.class);
		
		HttpTransportSE httpTransport = new HttpTransportSE(URL);

		StringBuffer auth = new StringBuffer(USERNAME);
		auth.append(':').append(PASSWORD);
		byte[] raw = auth.toString().getBytes();
		auth.setLength(0);
		auth.append("Basic ");
		org.kobjects.base64.Base64.encode(raw, 0, raw.length, auth);
		List<HeaderProperty> headers = new ArrayList<HeaderProperty>();
		headers.add(new HeaderProperty("Authorization", auth.toString())); // "Basic V1M6"));

		httpTransport.debug = true; // this is optional, use it if you don't
									// want to use a packet sniffer to check
									// what the sent message was
									// (httpTransport.requestDump)
		SoapObject result = null;
		try {
			httpTransport.call(SOAP_ACTION, envelope, headers); // send request
			result = (SoapObject) envelope.getResponse(); // get response
//			result = (SoapObject) envelope.bodyIn; // get response


		} catch (Exception e) {
			result=(SoapObject) envelope.bodyIn;
			Log.e(TAG, "SOAP ERROR:");
			e.printStackTrace();
			map.put("dump", httpTransport.responseDump);
			
		}
		if (result != null) {
			int props = result.getPropertyCount();
			Log.d(TAG, " risultano " + props + " proprietà");

			for (int i = 0; i < props; i++) {

				PropertyInfo pi = new PropertyInfo();
				result.getPropertyInfo(i, pi);
				String key =pi.name;
				if(pi.name.equals("return")){
					key = ""+i;
				}
				map.put(key, pi.getValue());

			}
		} else {
			Log.d(TAG, envelope.bodyOut.toString());
		}		
		
		Log.d(TAG,httpTransport.requestDump);
		Log.d(TAG,httpTransport.responseDump);
 		return map;
	}

}
