package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPFotografia implements KvmSerializable {

    
	public String descrizioneImmagine;
	public String dtFoto;
	public String estensioneImmagine;
	public String immagine;
	public String nomeImmagine;
	public String oraFoto;
	
	
	public SOAPFotografia(){
		estensioneImmagine = "png";
		immagine = "";
		nomeImmagine = "foto_1";
		descrizioneImmagine = "";
		dtFoto = "";
		oraFoto = "";
	}

	@Override
	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		switch(arg0){
		case 0:
			return estensioneImmagine;
		case 1:
			return immagine;
		case 2:
			return nomeImmagine;
		case 3:
			return descrizioneImmagine;
		case 4:
			return dtFoto;
		case 5: 
			return oraFoto;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 6;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo info) {
		info.type= PropertyInfo.STRING_CLASS;
        switch(arg0)
        {
		case 0:
			info.name = "estensioneImmagine";
			break;
		case 1:
			info.name ="immagine";
			break;
		case 2:
			info.name ="nomeImmagine";
			break;
		case 3:
			info.name ="descrizioneImmagine";
			break;
		case 4:
			info.name = "dtFoto";
			break;
		case 5:
			info.name = "oraFoto";
			break;
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		if(arg1 == null){
			arg1 = "";
		}		
		switch(arg0){
		case 0:
			estensioneImmagine = arg1.toString();
			break;
		case 1:
			immagine = arg1.toString();
			break;
		case 2:
			nomeImmagine = arg1.toString();
			break;
		case 3:
			if(arg1!=null){
			descrizioneImmagine = arg1.toString();
			}
			break;
		case 4:
			dtFoto = arg1.toString();
			break;
		case 5:
			oraFoto = arg1.toString();
			break;
		}		
	}

}
