package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAutGiochi;

import org.kxml2.kdom.Element;

public class AuthCheck {

	private static SOAPAutGiochi auth;
	private static boolean tokenValid = false;
	private static Element[] headerOut = null;
	
	
	public static void setAutGiochi(SOAPAutGiochi a){
		auth = a;
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
	
	public static int getTipoUtente(){
		if(siamoComune()){
			return Constants.UTENTE_COMUNE;
		} else if(siamoCooperativa()){
			return Constants.UTENTE_COOPERATIVA;
		} else {
			return 0;
		}
	}
	
	public static void setTipoUtente(int tipoUtente){
		if(auth==null){
			auth = new SOAPAutGiochi();
		}
		switch(tipoUtente){
		case Constants.UTENTE_COOPERATIVA:
			auth.userCooperativa = true;
			break;
		case Constants.UTENTE_COMUNE:
			auth.userComune = true;
			break;
		}
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
			return true;
		}
	}
	
	public static int getAutComune(){
		if(auth!=null){
			return auth.autComune;
		} else {
			return Constants.PERMESSO_VISUALIZZA;
		}
	}
	public static int getAutCooperativa(){
		if(auth!=null){
			return auth.autCooperativa;
		} else {
			return Constants.PERMESSO_VISUALIZZA;
		}
	}
	
}
