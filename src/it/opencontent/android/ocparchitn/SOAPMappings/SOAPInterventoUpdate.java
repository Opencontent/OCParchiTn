package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPInterventoUpdate implements KvmSerializable {
	
	
	public String dtFineIntervento;
	public String idRiferimento;
	public String noteEsito;
	public String oraFineIntervento;
	public String rfid;
	public String tipoEsito;	
	public String dtInizioIntervento; 
	public String oraInizioIntervento; 
	

	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return dtFineIntervento;
		case 1:
			return idRiferimento;
		case 2:
			return noteEsito;
		case 3:
			return oraFineIntervento;
		case 4:
			return rfid;
		case 5:
			return tipoEsito;
		case 6:
			return dtInizioIntervento;
		case 7:
			return oraInizioIntervento;
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
			arg2.name = "dtFineIntervento";
			break;
		case 1:
			arg2.name = "idRiferimento";
			break;
		case 2:
			arg2.name = "noteEsito";
			break;
		case 3:
			arg2.name = "oraFineIntervento";
			break;
		case 4:
			arg2.name = "rfid";
			break;
		case 5:
			arg2.name = "tipoEsito";
			break;
		case 6:
			arg2.name = "dtInizioIntervento";
			break;
		case 7:
			arg2.name = "oraInizioIntervento";
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
			dtFineIntervento = arg1.toString();
			break;
		case 1:
			idRiferimento = arg1.toString();
			break;
		case 2:
			noteEsito = arg1.toString();
			break;
		case 3:
			 oraFineIntervento = arg1.toString();
			break;
		case 4:
			rfid  = arg1.toString();
			break;
		case 5:
			tipoEsito = arg1.toString();
			break;
		case 6:
			dtInizioIntervento = arg1.toString();
			break;
		case 7:
			oraInizioIntervento = arg1.toString();
			break;
			}
	}

}
