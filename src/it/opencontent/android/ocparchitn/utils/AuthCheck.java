package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAutGiochi;

import org.kxml2.kdom.Element;

public class AuthCheck {

	private static SOAPAutGiochi auth;
	private static boolean tokenValid = false;
	private static Element[] headerOut = null;
	private static int lastKnownAutComuneLevel = 0;
	private static int lastKnownAutCooperativaLevel = 0;
	
	
	public static void setAutGiochi(SOAPAutGiochi a){
		auth = a;
		lastKnownAutComuneLevel = auth.autComune;
		lastKnownAutCooperativaLevel = auth.autCooperativa;
		tokenValid = true;
	}
	
	public static void setTokenExpired(){
		tokenValid = false;
		headerOut = null;
	}
	
	public static Element[] getHeaderOut(){
		return headerOut;
	}
	public static void setHeaderOut(Element[] h){
		headerOut = h;
	}
	
	public static boolean getTokenValid(){
		return tokenValid;
	}
	
	public static boolean siamoComune(){
		if(auth != null){
			return auth.userComune;
		} else {
			return false;
		}
	}
	public static boolean siamoCooperativa(){
		if(auth != null){
			return auth.userCooperativa;
		} else {
			return false;
		}
	}
	
	public static int getAutComune(){
		return auth.autComune;
	}
	public static int getAutCooperativa(){
		return auth.autCooperativa;
	}
	
}
