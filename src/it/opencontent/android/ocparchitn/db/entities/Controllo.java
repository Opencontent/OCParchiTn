package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPControllo;


public class Controllo extends Struttura {

	public int controllo;
	public String dtScadenzaControllo;
	public String noteControllo;
	public String idRiferimento;
	public int tipoControllo;
	public int tipoEsito;
	public int tipoSegnalazione;
	
	
	public Controllo() {
		
	}
	
	public Controllo(SOAPControllo sc){
		controllo = sc.controllo;
		dtScadenzaControllo = sc.dtScadenzaControllo;
		noteControllo = sc.noteControllo;
		idRiferimento = sc.idRiferimento;
		rfid = sc.rfid;
		tipoControllo = sc.tipoControllo;
	}

	
}
