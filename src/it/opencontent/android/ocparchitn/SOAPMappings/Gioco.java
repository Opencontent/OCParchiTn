package it.opencontent.android.ocparchitn.SOAPMappings;

import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Gioco implements KvmSerializable {
	
	public String descrizione_area;
	public String descrizione_gioco;
	public String descrizione_marca;
	public String dt_acquisto;
	public String dt_installazione;
	public String dt_posizionamento_al;
	public String dt_posizionamento_dal;
	public String dt_prossimointervento;
	public String gpsx;
	public String gpsy;
	public String id_gioco;
	public String id_modello;
	public String id_tipogioco;
	public String note;
	public int    numero_fotografie;
	public String numeroserie;
	public String posizione_rfid;
	public String rfid;
	public String rfid_area;
	

	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return descrizione_area;
		case 1:
			return descrizione_gioco;
		case 2:
			return descrizione_marca;
		case 3:
			return dt_acquisto;
		case 4:
			return dt_installazione;
		case 5:
			return dt_posizionamento_al;
		case 6:
			return dt_posizionamento_dal;
		case 7:
			return dt_prossimointervento;
		case 8:
			return gpsx;
		case 9:
			return gpsy;
		case 10:
			return id_gioco;
		case 11:
			return id_modello;
		case 12:
			return id_tipogioco;
		case 13:
			return note;
		case 14:
			return numero_fotografie;
		case 15:
			return numeroserie;
		case 16:
			return posizione_rfid;
		case 17:
			return rfid;
		case 18:
			return rfid_area;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 19;
	}

	@Override
	public void getPropertyInfo(int arg0, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo arg2) {
		arg2.type = PropertyInfo.STRING_CLASS;
		switch(arg0){
		case 0:
			arg2.name = "descrizione_area";
		case 1:
			arg2.name = "descrizione_gioco";
		case 2:
			arg2.name = "descrizione_marca";
		case 3:
			arg2.name = "dt_acquisto";
		case 4:
			arg2.name = "dt_installazione";
		case 5:
			arg2.name = "dt_posizionamento_al";
		case 6:
			arg2.name = "dt_posizionamento_dal";
		case 7:
			arg2.name = "dt_prossimointervento";
		case 8:
			arg2.name = "gpsx";
		case 9:
			arg2.name = "gpsy";
		case 10:
			arg2.name = "id_gioco";
		case 11:
			arg2.name = "id_modello";
		case 12:
			arg2.name = "id_tipogioco";
		case 13:
			arg2.name = "note";
		case 14:
			arg2.name = "numero_fotografie";
			arg2.type = PropertyInfo.INTEGER_CLASS;
		case 15:
			arg2.name = "posizione_rfid";
		case 16:
			arg2.name = "numeroserie";
		case 17:
			arg2.name = "rfid";
		case 18:
			arg2.name = "rfid_area";
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0){
		case 0:
			descrizione_area = arg1.toString();
		case 1:
			descrizione_gioco= arg1.toString();
		case 2:
			descrizione_marca= arg1.toString();
		case 3:
			dt_acquisto= arg1.toString();
		case 4:
			dt_installazione= arg1.toString();
		case 5:
			dt_posizionamento_al= arg1.toString();
		case 6:
			dt_posizionamento_dal= arg1.toString();
		case 7:
			dt_prossimointervento= arg1.toString();
		case 8:
			gpsx= arg1.toString();
		case 9:
			gpsy= arg1.toString();
		case 10:
			id_gioco= arg1.toString();
		case 11:
			id_modello= arg1.toString();
		case 12:
			id_tipogioco= arg1.toString();
		case 13:
			note= arg1.toString();
		case 14:
			numero_fotografie= Integer.parseInt(arg1.toString());
		case 15:
			numeroserie= arg1.toString();
		case 16:
			posizione_rfid= arg1.toString();
		case 17:
			rfid= arg1.toString();
		case 18:
			rfid_area= arg1.toString();
		}		
	}

}