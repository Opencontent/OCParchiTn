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
		descrizioneMarca = remote.descrizioneMarca;
		descrizioneArea = remote.descrizioneArea;
		descrizione_gioco = remote.descrizioneGioco;
		rfid = Integer.parseInt(remote.rfid);
		rfidArea = remote.rfidArea;
		idGioco = Integer.parseInt(remote.idGioco);
		note = remote.note;
		numeroFotografie = remote.numeroFotografie;
		numeroSerie = remote.numeroSerie;
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
