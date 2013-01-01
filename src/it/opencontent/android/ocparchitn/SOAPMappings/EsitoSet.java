package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class EsitoSet implements KvmSerializable {

	public String codice;
	public String messaggio;
	public boolean successo;
		
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return codice;
		case 1:
			return messaggio;
		case 2:
			return successo;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 3;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
		arg2.type = PropertyInfo.STRING_CLASS;
		switch(arg0){
		case 0:
			arg2.name = "codice";
		case 1:
			arg2.name = "messaggio";
		case 2:
			arg2.name = "successo";
			arg2.type=PropertyInfo.BOOLEAN_CLASS;
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0){
		case 0:
			codice = arg1.toString();
		case 1:
			messaggio = arg1.toString();
		case 2:
			successo = Boolean.getBoolean(arg1.toString());
		}
	}

}