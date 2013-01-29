package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGioco;

import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

public class Intervento extends Struttura {

	public String posizioneRfid;
	  
	
	public Intervento() {
		tipo = "gioco";
	}

	public Intervento(SOAPGioco remote,Context context){
		super();
		tipo="gioco";
		gpsx = Float.parseFloat(remote.gpsx);
		gpsy = Float.parseFloat(remote.gpsy);
		descrizioneMarca = remote.descrizioneMarca;
		descrizioneArea = remote.descrizioneArea;
		descrizioneGioco = remote.descrizioneGioco;
		rfid = Integer.parseInt(remote.rfid);
		rfidArea = Integer.parseInt(remote.rfidArea);
		idGioco = Integer.parseInt(remote.idGioco);
		note = remote.note;
		numeroFotografie = remote.numeroFotografie;
		numeroSerie = remote.numeroSerie;
		posizioneRfid = remote.posizioneRfid;
	}
	
	public Intervento(Set<Entry<String, Object>> entrySet,int rfid,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,rfid,context);
		tipo = "gioco";	
	}
	public Intervento(Set<Entry<String, Object>> entrySet,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,context);
		tipo = "gioco";
	}
}
