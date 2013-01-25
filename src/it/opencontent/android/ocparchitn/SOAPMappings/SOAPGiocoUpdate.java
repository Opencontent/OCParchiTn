package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPGiocoUpdate implements KvmSerializable {

	
	public String gpsx; 
	public String gpsy;
	public String idGioco;
	public String note;
	public String rfid;
	public String rfidArea;
	public String posizioneRfid;
	
	public SOAPGiocoUpdate(){
		
	}

	@Override
	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		switch(arg0){
		case 0:
			return gpsx;
		case 1:
			return gpsy;
		case 2:
			return idGioco;
		case 3:
			return note;
		case 4:
			return rfid;
		case 5:
			return rfidArea;
		case 6:
			return posizioneRfid;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 7;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		info.type= PropertyInfo.STRING_CLASS;
        switch(arg0)
        {
		case 0:
			info.name ="gpsx";
			break;
		case 1:
			info.name ="gpsy";
			break;
		case 2:
			info.name ="idGioco";
			break;
		case 3:
			info.name ="note";
			break;
		case 4:
			info.name ="rfid";
			break;
		case 5:
			info.name ="rfidArea";
			break;
		case 6:
			info.name ="posizioneRfid";
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
			gpsx = arg1.toString();
			break;
		case 1:
			gpsy = arg1.toString();
			break;
		case 2:
			idGioco = arg1.toString();
			break;
		case 3:
			note = arg1.toString();
			break;
		case 4:
			rfid = arg1.toString();
			break;
		case 5:
			rfidArea = arg1.toString();
			break;
		case 6:
			posizioneRfid = arg1.toString();
			break;

		}		
	}

}
