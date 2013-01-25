package it.opencontent.android.ocparchitn.SOAPMappings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import android.util.Log;

public class SOAPCodTabella implements KvmSerializable {

	public int codice;
	public String descrizione;
	public Date dtValidita;
	
	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return codice;
		case 1:
			return descrizione;
		case 2:
			return dtValidita;
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
			arg2.name = "codice";
			arg2.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 1:
			arg2.name = "descrizione";
			arg2.type = PropertyInfo.STRING_CLASS;
			break;
		case 2:
			arg2.name = "dtValidita";
			arg2.type = PropertyInfo.OBJECT_CLASS;
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
		case 2:
			//2050-12-31
			
			try {
				String pattern = "yyyy-MM-dd";
				SimpleDateFormat sdf = new SimpleDateFormat(pattern,Locale.US);
				dtValidita = sdf.parse(arg1+"");
			} catch (ParseException e) {
				e.printStackTrace();
			} 
			break;
		}

	}

}
