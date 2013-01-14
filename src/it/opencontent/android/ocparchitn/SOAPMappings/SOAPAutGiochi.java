package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPAutGiochi implements KvmSerializable{
	
	public int autComune = 0;
	public int autCooperativa = 0;
	public boolean userComune = false;
	public boolean userCooperativa = false;
	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return autComune;
		case 1:
			return autCooperativa;
		case 2:
			return userComune;
		case 3:
			return userCooperativa;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 4;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch(arg0){
		case 0:
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			arg2.name = "autComune";
			break;
		case 1:
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			arg2.name = "autCooperativa";
			break;
		case 2:
			arg2.type = PropertyInfo.BOOLEAN_CLASS ;
			arg2.name = "userComune";
			break;
		case 3:
			arg2.type = PropertyInfo.BOOLEAN_CLASS ;
			arg2.name = "userCooperativa";
			break;
		}
		
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0){
		case 0:
			autComune = (Integer) arg1;
			break;
		case 1:
			autCooperativa = (Integer) arg1;
			break;
		case 2:
			userComune = (Boolean) arg1;
			break;
		case 3:
			userCooperativa = (Boolean) arg1;
			break;
		}
		
	}

}
