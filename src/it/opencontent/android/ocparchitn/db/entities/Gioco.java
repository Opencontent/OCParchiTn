package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGioco;

import java.util.Map.Entry;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;

public class Gioco extends Struttura {

	public String posizioneRfid;
	public int spostamento;
	  
	
	public Gioco() {
		tipo = "gioco";
	}

	public Gioco(SOAPGioco remote,Context context){
		super();
		tipo="gioco";
		gpsx = remote.gpsx;
		gpsy = remote.gpsy;
		descrizioneMarca = remote.descrizioneMarca;
		descrizioneArea = remote.descrizioneArea;
		descrizioneGioco = remote.descrizioneGioco;
		try{
			rfid = Integer.parseInt(remote.rfid);
		} catch(NumberFormatException e){
			rfid=0;
		}
		try{
			rfidArea = Integer.parseInt(remote.rfidArea);
		} catch(NumberFormatException e){
			rfidArea=0;
		}
		try{
			idGioco = Integer.parseInt(remote.idGioco);
		} catch(NumberFormatException e){
			idGioco=0;
		}
		
		
		note = remote.note;
		numeroFotografie = remote.numeroFotografie;
		numeroSerie = remote.numeroSerie;
		posizioneRfid = remote.posizioneRfid;
		spostamento = 0;
	}
	
	public Gioco(Set<Entry<String, Object>> entrySet,int rfid,Context context) {
		super(entrySet,rfid,context);
		tipo = "gioco";	
		spostamento = 0;
	}
	public Gioco(Set<Entry<String, Object>> entrySet,Context context) {
		super(entrySet,context);
		tipo = "gioco";
		spostamento = 0;
		
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			
			if (e.getKey().equals("posizioneRfid")) {
				posizioneRfid = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("spostamento")) {
				spostamento = bindIntToProperty(e.getValue(), e.getKey());
			}
			
			
		}			
	}
}
