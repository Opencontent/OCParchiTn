package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPCodTabella implements KvmSerializable {

	public int codice;
	public String descrizione;
	
	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return codice;
		case 1:
			return descrizione;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 2;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch(arg0){
		case 0:
			arg2.name = "codice";
			arg2.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 1:
			arg2.name = "descrizione";
			arg2.type = PropertyInfo.STRING_CLASS;
			break;
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0){
		case 0:
			codice = Integer.parseInt(arg1.toString());
			break;
		case 1:
			descrizione = arg1.toString();
			break;
		}

	}

}
