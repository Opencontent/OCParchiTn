package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPGioco implements KvmSerializable {
	
	public String descrizioneArea;
	public String descrizioneGioco;
	public String descrizioneMarca;
	public String dtAcquisto;
	public String dtInstallazione;
	public String dtPosizionamentoAl;
	public String dtPosizionamentoDal;
	public String dtProssimaManutenzione;
	public String dtProssimaVerifica;
	public String gpsx;
	public String gpsy;
	public String idGioco;
	public String idModello;
	public String idTipoGioco;
	public String note;
	public int    numeroFotografie;
	public String numeroSerie;
	public String posizioneRfid;
	public String rfid;
	public String rfidArea;
	
	

	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return descrizioneArea;
		case 1:
			return descrizioneGioco;
		case 2:
			return descrizioneMarca;
		case 3:
			return dtAcquisto;
		case 4:
			return dtInstallazione;
		case 5:
			return dtPosizionamentoAl;
		case 6:
			return dtPosizionamentoDal;
		case 7:
			return dtProssimaManutenzione;
		case 8:
			return dtProssimaVerifica;
		case 9:
			return gpsx;
		case 10:
			return gpsy;
		case 11:
			return idGioco;
		case 12:
			return idModello;
		case 13:
			return idTipoGioco;
		case 14:
			return note;
		case 15:
			return numeroFotografie;
		case 16:
			return numeroSerie;
		case 17:
			return posizioneRfid;
		case 18:
			return rfid;
		case 19:
			return rfidArea;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 20;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
		arg2.type = PropertyInfo.STRING_CLASS;
		switch(arg0){
		case 0:
			arg2.name = "descrizioneArea";
			break;
		case 1:
			arg2.name = "descrizioneGioco";
			break;
		case 2:
			arg2.name = "descrizioneMarca";
			break;
		case 3:
			arg2.name = "dtAcquisto";
			break;
		case 4:
			arg2.name = "dtInstallazione";
			break;
		case 5:
			arg2.name = "dtPosizionamentoAl";
			break;
		case 6:
			arg2.name = "dtPosizionamentoDal";
			break;
		case 7:
			arg2.name = "dtProssimaManutenzione";
			break;
		case 8:
			arg2.name = "dtProssimaVerifica";
			break;
		case 9:
			arg2.name = "gpsx";
			break;
		case 10:
			arg2.name = "gpsy";
			break;
		case 11:
			arg2.name = "idGioco";
			break;
		case 12:
			arg2.name = "idModello";
			break;
		case 13:
			arg2.name = "idTipoGioco";
			break;
		case 14:
			arg2.name = "note";
			break;
		case 15:
			arg2.name = "numeroFotografie";
			arg2.type = PropertyInfo.INTEGER_CLASS;
			break;
		case 16:
			arg2.name = "posizioneRfid";
			break;
		case 17:
			arg2.name = "numeroSerie";
			break;
		case 18:
			arg2.name = "rfid";
			break;
		case 19:
			arg2.name = "rfidArea";
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
			descrizioneArea = arg1.toString();
			break;
		case 1:
			descrizioneGioco= arg1.toString();
			break;
		case 2:
			descrizioneMarca= arg1.toString();
			break;
		case 3:
			dtAcquisto= arg1.toString();
			break;
		case 4:
			dtInstallazione= arg1.toString();
			break;
		case 5:
			dtPosizionamentoAl= arg1.toString();
			break;
		case 6:
			dtPosizionamentoDal= arg1.toString();
			break;
		case 7:
			dtProssimaManutenzione = arg1.toString();
			break;
		case 8:
			dtProssimaVerifica= arg1.toString();
			break;
		case 9:
			gpsx= arg1.toString();
			break;
		case 10:
			gpsy= arg1.toString();
			break;
		case 11:
			idGioco= arg1.toString();
			break;
		case 12:
			idModello= arg1.toString();
			break;
		case 13:
			idTipoGioco= arg1.toString();
			break;
		case 14:
			note= arg1.toString();
			break;
		case 15:
			numeroFotografie= Integer.parseInt(arg1.toString());
			break;
		case 16:
			numeroSerie= arg1.toString();
			break;
		case 17:
			posizioneRfid= arg1.toString();
			break;
		case 18:
			rfid= arg1.toString();
			break;
		case 19:
			rfidArea= arg1.toString();
			break;
		}		
	}

}