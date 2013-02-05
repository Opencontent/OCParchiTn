package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPControlloUpdate implements KvmSerializable {
	
	public int controllo;
	public String dtControllo;
	public String idRiferimento;
	public String noteEsito;
	public String oraControllo;
	public int rfid;
	public int tipoControllo;
	
	public int tipoSegnalazione;
	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return controllo;
		case 1:
			return dtControllo;
		case 2:
			return noteEsito;
		case 3:
			return oraControllo;
		case 4:
			return rfid;
		case 5:
			return tipoControllo;
		case 6:
			return idRiferimento;	
		case 7:
			return tipoSegnalazione;	
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
			arg2.name = "controllo";
			break;
		case 1:
			arg2.name = "dtControllo";
			break;
		case 2:
			arg2.name = "noteEsito";
			break;
		case 3:
			arg2.name = "oraControllo";
			break;
		case 4:
			arg2.name = "rfid";
			break;
		case 5:
			arg2.name = "tipoControllo";
			break;
		case 6:
			arg2.name = "idRiferimento";
			break;
		case 7:
			arg2.name = "tipoSegnalazione";
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
			controllo = Integer.parseInt(arg1.toString());
			break;
		case 1:
			dtControllo = arg1.toString();
			break;
		case 2:
			noteEsito = arg1.toString();
			break;
		case 3:
			 oraControllo = arg1.toString();
			break;
		case 4:
			rfid  = Integer.parseInt(arg1.toString());
			break;
		case 5:
			tipoControllo = Integer.parseInt(arg1.toString());
			break;
		case 6:
			idRiferimento = arg1.toString();
			break;
		case 7:
			tipoSegnalazione = Integer.parseInt(arg1.toString());
			break;
			}
	}

}
