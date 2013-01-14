package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPSrvGiocoArkAutException implements KvmSerializable{
	
	

    
    public String codice;
    public int level;
    public String message;

	

	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return codice;
		case 1:
			return level;
		case 2:
			return message;
		}
	
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 3;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch(arg0){
		case 0:
			arg2.type = PropertyInfo.STRING_CLASS ;
			arg2.name = "codice";
			break;
		case 1:
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			arg2.name = "level";
			break;
		case 2:
			arg2.type = PropertyInfo.STRING_CLASS ;
			arg2.name = "message";
			break;
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0){
		case 0:
			codice = arg1.toString();
			break;
		case 1:
			level = (Integer) arg1;
			break;
		case 2:
			message = arg1.toString();
			break;
		}
	}

}
