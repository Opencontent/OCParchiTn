package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPAreaUpdate implements KvmSerializable {
	
	public String idParco;
	public String note;
	public String rfid;
	public String spessore;
	public String superficie;
	public String tipoPavimentazione;
	public String idArea;
	public String posizioneRfid;

	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return idParco;
		case 1:
			return note;
		case 2:
			return rfid;
		case 3:
			return spessore;
		case 4:
			return superficie;
		case 5:
			return tipoPavimentazione;
		case 6:
			return idArea;
		case 7:
			return posizioneRfid;
	
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 8;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
		arg2.type = PropertyInfo.STRING_CLASS;
		switch(arg0){
		case 0:
			arg2.name = "idParco";
			break;
		case 1:
			arg2.name = "note";
			break;
		case 2:
			arg2.name = "rfid";
			break;
		case 3:
			arg2.name = "spessore";
			break;
		case 4:
			arg2.name = "superficie";
			break;
		case 5:
			arg2.name = "tipoPavimentazione";
			break;
		case 6:
			arg2.name = "idArea";
			break;
		case 7:
			arg2.name = "posizioneRfid";
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
			idParco= arg1.toString();
			break;
		case 1:
			note= arg1.toString();
			break;
		case 2:
			rfid= arg1.toString();
			break;
		case 3:
			spessore= arg1.toString();
			break;
		case 4:
			superficie= arg1.toString();
			break;
		case 5:
			tipoPavimentazione= arg1.toString();
			break;
		case 6:
			idArea=arg1.toString();
			break;
		case 7:
			posizioneRfid=arg1.toString();
			break;
		}
	}

}
