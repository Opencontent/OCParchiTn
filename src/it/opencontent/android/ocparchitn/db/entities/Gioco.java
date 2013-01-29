package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGioco;

import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;

public class Gioco extends Struttura {

	public String posizioneRfid;
	  
	
	public Gioco() {
		tipo = "gioco";
	}

	public Gioco(SOAPGioco remote,Context context){
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
	
	public Gioco(Set<Entry<String, Object>> entrySet,int rfid,Context context) {
		super(entrySet,rfid,context);
		tipo = "gioco";	
	}
	public Gioco(Set<Entry<String, Object>> entrySet,Context context) {
		super(entrySet,context);
		tipo = "gioco";
		
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			
			if (e.getKey().equals("posizioneRfid")) {
				posizioneRfid = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			
			
		}			
	}
}
