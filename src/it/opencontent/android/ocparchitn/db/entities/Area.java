package it.opencontent.android.ocparchitn.db.entities;

import java.util.Iterator;
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
	public String posizioneRfid;	

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
		posizioneRfid = remote.posizioneRfid;
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
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			if (e.getKey().equals("idArea")) {
				idArea = bindIntToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("tipoPavimentazione")) {
				tipoPavimentazione = bindIntToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("superficie")) {
				superficie = bindFloatToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("spessore")) {
				spessore = bindFloatToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("idParco") && e.getValue() != null) {
				idParco = bindIntToProperty(e.getValue(), e.getKey());
			}
			
		}		
	}
}
