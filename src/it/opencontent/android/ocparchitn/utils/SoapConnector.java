package it.opencontent.android.ocparchitn.utils;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class SoapConnector {


	//you can get these values from the wsdl file^
	 
	 public SoapObject soap(String METHOD_NAME, String SOAP_ACTION, String NAMESPACE, String URL) throws IOException, XmlPullParserException {
	    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME); //set up request
	    request.addProperty("iTopN", "5"); //variable name, value. I got the variable name, from the wsdl file!
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11); //put all required data into a soap envelope
	    envelope.setOutputSoapObject(request);  //prepare request
	    HttpTransportSE httpTransport = new HttpTransportSE(URL);  
	    httpTransport.debug = true;  //this is optional, use it if you don't want to use a packet sniffer to check what the sent message was (httpTransport.requestDump)
	    httpTransport.call(SOAP_ACTION, envelope); //send request
	    SoapObject result=(SoapObject)envelope.getResponse(); //get response
	    return result;
	 }
	 
	//usage:
	//SoapObject result=soap(METHOD_NAME, SOAP_ACTION, NAMESPACE, URL);
	//don't forget to catch the exceptions!!
	
}
