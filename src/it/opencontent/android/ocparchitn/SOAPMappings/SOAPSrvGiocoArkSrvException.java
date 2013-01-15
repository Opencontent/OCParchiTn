package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPSrvGiocoArkSrvException implements KvmSerializable{
	
    public String message;

	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return message;
		}
	
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 1;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch(arg0){
		case 0:
			arg2.type = PropertyInfo.STRING_CLASS ;
			arg2.name = "message";
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
			message = arg1.toString();
			break;
		}
	}

}
