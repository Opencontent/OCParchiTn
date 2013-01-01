package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Area implements KvmSerializable {
	
	public String id_parco;
	public String note;
	public String rfid;
	public String spessore;
	public String superficie;
	public String tipo_pavimentazione;

	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return id_parco;
		case 1:
			return note;
		case 2:
			return rfid;
		case 3:
			return spessore;
		case 4:
			return superficie;
		case 5:
			return tipo_pavimentazione;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 6;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
		arg2.type = PropertyInfo.STRING_CLASS;
		switch(arg0){
		case 0:
			arg2.name = "id_parco";
		case 1:
			arg2.name = "note";
		case 2:
			arg2.name = "rfid";
		case 3:
			arg2.name = "spessore";
		case 4:
			arg2.name = "superficie";
		case 5:
			arg2.name = "tipo_pavimentazione";
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0){
		case 0:
			id_parco= arg1.toString();
		case 1:
			note= arg1.toString();
		case 2:
			rfid= arg1.toString();
		case 3:
			spessore= arg1.toString();
		case 4:
			superficie= arg1.toString();
		case 5:
			tipo_pavimentazione= arg1.toString();
		}
	}

}
