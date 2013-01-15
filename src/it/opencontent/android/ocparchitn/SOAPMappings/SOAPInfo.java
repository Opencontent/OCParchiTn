package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPInfo implements KvmSerializable {
	
	public String descrizione;
	public String note;
	public String sviluppatore;
	public String versione;

	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return descrizione;
		case 1:
			return note;
		case 2:
			return sviluppatore;
		case 3:
			return versione;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
		arg2.type= PropertyInfo.STRING_CLASS;
        switch(arg0)
        {
		case 0:
			arg2.name ="descrizione";
			break;
		case 1:
			arg2.name ="note";
			break;
		case 2:
			arg2.name ="sviluppatore";
			break;
		case 3:
			arg2.name ="versione";
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
			descrizione = arg1.toString();
			break;
		case 1:
			note = arg1.toString();
			break;
		case 2:
			sviluppatore = arg1.toString();
			break;
		case 3:
			versione = arg1.toString();
			break;
		}
		
	}

}