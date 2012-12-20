package it.opencontent.android.ocparchitn.utils;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Fotografia implements KvmSerializable {

    
	public String estensioneImmagine;
	public String immagine;
	public String nomeImmagine;
	public String descrizioneImmagine;
	
	
	public Fotografia(){
		estensioneImmagine = "png";
		immagine = "";
		nomeImmagine = "foto_1";
		descrizioneImmagine = "";
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
		
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 4;
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

		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		
		switch(arg0){
		case 0:
			estensioneImmagine = arg1.toString();
		case 1:
			immagine = arg1.toString();
		case 2:
			nomeImmagine = arg1.toString();
		case 3:
			if(arg1!=null){
			descrizioneImmagine = arg1.toString();
			}
		}		
	}

}
