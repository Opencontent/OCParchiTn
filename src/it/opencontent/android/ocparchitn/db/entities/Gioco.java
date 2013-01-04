package it.opencontent.android.ocparchitn.db.entities;

import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

public class Gioco extends Struttura {

	  
	
	public Gioco() {
		tipo = "gioco";
	}

	public Gioco(it.opencontent.android.ocparchitn.SOAPMappings.SOAPGioco remote,Context context){
		super();
		tipo="gioco";
		gpsx = Float.parseFloat(remote.gpsx);
		gpsy = Float.parseFloat(remote.gpsy);
		descrizione_marca = remote.descrizione_marca;
		descrizione_area = remote.descrizione_area;
		descrizione_gioco = remote.descrizione_gioco;
		rfid = Integer.parseInt(remote.rfid);
		rfid_area = remote.rfid_area;
		id_gioco = Integer.parseInt(remote.id_gioco);
		note = remote.note;
		numero_fotografie = remote.numero_fotografie;
		numeroserie = remote.numeroserie;
	}
	
	public Gioco(Set<Entry<String, Object>> entrySet,int rfid,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,rfid,context);
		tipo = "gioco";	
	}
	public Gioco(Set<Entry<String, Object>> entrySet,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,context);
		tipo = "gioco";
	}
}
