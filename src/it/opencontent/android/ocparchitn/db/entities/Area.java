package it.opencontent.android.ocparchitn.db.entities;

import java.util.Set;
import java.util.Map.Entry;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPArea;
import android.content.Context;

public class Area extends Struttura {

	
	public int idArea;
	public int idParco;
	public float spessore;
	public float superficie;
	public int tipoPavimentazione;	

	public Area() {
		this.tipo = "area";
	}
	public Area(SOAPArea remote,Context context){
		super();
		tipo="area";
		descrizioneArea = remote.descrizione;
		idArea = Integer.parseInt(remote.idArea);
		idParco = Integer.parseInt(remote.idParco);
		note = remote.note;
		numeroFotografie = remote.numeroFotografie;
		rfid = Integer.parseInt(remote.rfid);
		spessore = Float.parseFloat(remote.spessore);
		superficie = Float.parseFloat(remote.superficie);
		tipoPavimentazione = Integer.parseInt(remote.tipoPavimentazione);		
	}
	public Area(Set<Entry<String, Object>> entrySet,int rfid,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,rfid,context);
		tipo = "area";	
	}
	public Area(Set<Entry<String, Object>> entrySet,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,context);
		tipo = "area";
	}
}
