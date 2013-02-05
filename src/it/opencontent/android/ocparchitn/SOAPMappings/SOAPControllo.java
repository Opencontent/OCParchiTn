package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPControllo implements KvmSerializable {
	
	public int controllo;
	
	public boolean attivo;
	public int codTipologia;
	public String descTipologia;
	public String dtInizioControllo;
	
	public String dtScadenzaControllo;
	public String idRiferimento;
	public String noteControllo;
	public int rfid;
	public int tipoControllo;
		
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return controllo;
		case 1:
			return dtScadenzaControllo;
		case 2:
			return noteControllo;
		case 3:
			return idRiferimento;
		case 4:
			return rfid;
		case 5:
			return tipoControllo;
		case 6: 
			return attivo;
		case 7: 
			return codTipologia;
		case 8: 
			return descTipologia;
		case 9: 
			return dtInizioControllo;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 10;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
		arg2.type = PropertyInfo.STRING_CLASS;
		switch(arg0){
		case 0:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "controllo";
			break;
		case 1:
			arg2.name = "dtScadenzaControllo";
			break;
		case 2:
			arg2.name = "noteControllo";
			break;
		case 3:
			arg2.name = "idRiferimento";
			break;
		case 4:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "rfid";
			break;
		case 5:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "tipoControllo";
			break;
		case 6:
			arg2.type = PropertyInfo.BOOLEAN_CLASS;
			arg2.name = "attivo";
			break;
		case 7:
			arg2.type = PropertyInfo.INTEGER_CLASS;
			arg2.name = "codTipologia";
			break;
		case 8:
			arg2.name = "descTipologia";
			break;
		case 9:
			arg2.name = "dtInizioControllo";
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
			dtScadenzaControllo = arg1.toString();
			break;
		case 2:
			noteControllo = arg1.toString();
			break;
		case 3:
			 idRiferimento = arg1.toString();
			break;
		case 4:
			rfid  = Integer.parseInt(arg1.toString());
			break;
		case 5:
			tipoControllo = Integer.parseInt(arg1.toString());
			break;
		case 6:
			attivo = Boolean.parseBoolean(arg1.toString());
			break;
		case 7:
			codTipologia = Integer.parseInt(arg1.toString());
			break;
		case 8:
			descTipologia = arg1.toString();
			break;
		case 9:
			dtInizioControllo = arg1.toString();
			break;
		
		}
	}

}
