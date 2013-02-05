package it.opencontent.android.ocparchitn.db.entities;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import android.content.Context;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPControllo;


public class Controllo extends Struttura {

	public int controllo;
	public String dtScadenzaControllo;
	public String noteControllo;
	public String idRiferimento;
	public int tipoControllo;
	public int tipoEsito;
	public int tipoSegnalazione;
	public String descrizioneControllo;
	
	
	public Controllo() {
		tipoControllo = 1;
		tipoEsito = 1;
		tipoSegnalazione = 1;
	}
	
	public Controllo(SOAPControllo sc){
		controllo = sc.controllo;
		dtScadenzaControllo = sc.dtScadenzaControllo;
		noteControllo = sc.noteControllo;
		idRiferimento = sc.idRiferimento;
		rfid = sc.rfid;
		tipoControllo = sc.tipoControllo;
		tipoEsito = 1;
		tipoSegnalazione = 1;
		foto0=null;
		foto1=null;
	}
	
	public Controllo(Set<Entry<String, Object>> entrySet,Context context) {
		super(entrySet,context);
		tipo = "area";
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			if (e.getKey().equals("controllo")) {
				controllo = bindIntToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("tipoControllo")) {
				tipoControllo = bindIntToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("tipoEsito")) {
				tipoEsito = bindIntToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("idRiferimento")) {
				idRiferimento = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("tipoSegnalazione")) {
				tipoSegnalazione = bindIntToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("noteControllo")) {
				noteControllo = bindStringToProperty(e.getValue(),
						e.getKey());
			}
		}		
	}
	
}
