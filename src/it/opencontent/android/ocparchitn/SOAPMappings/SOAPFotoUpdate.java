package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPFotoUpdate implements KvmSerializable {

	
	public String estensioneImmagine;
	public String immagine;
	public String nomeImmagine;
	public String idGioco;
	public boolean sovrascrittura;
	
	public SOAPFotoUpdate(){
		estensioneImmagine = "png";
		nomeImmagine = "foto_1";
		idGioco = "0";
		sovrascrittura = false;
	}

	@Override
	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		switch(arg0){
		case 0:
			return estensioneImmagine;
		case 1:
			return nomeImmagine;
		case 4:
			return immagine;
		case 3:
			return idGioco;
		case 2:
			return sovrascrittura;
		
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		info.type= PropertyInfo.STRING_CLASS;
        switch(arg0)
        {
		case 0:
			info.name = "estensioneImmagine";
			break;
		case 1:
			info.name ="nomeImmagine";
			break;
		case 4:
			info.name ="immagine";
			break;
		case 3:
			info.name ="idGioco";
			break;
		case 2:
			info.name ="sovrascrittura";
			break;
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		
		switch(arg0){
		case 0:
			estensioneImmagine = arg1.toString();
			break;
		case 1:
			nomeImmagine = arg1.toString();
			break;
		case 4:
			immagine = arg1.toString();
			break;
		case 3:
			idGioco = arg1.toString();
			break;
		case 2:
			sovrascrittura = Boolean.getBoolean(arg1.toString());
			break;
		}		
	}

}
