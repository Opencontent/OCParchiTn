package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPCodTabella;

import java.util.Date;

public class RecordTabellaSupporto {
	public int numeroTabella;
	public int codice;
	public String descrizione;
	public long validita; 
	public String tipo;
	
	public String toString(){
		return descrizione;
	}
	
	public RecordTabellaSupporto(int nt, SOAPCodTabella soap){
		tipo = "";
		validita = soap.dtValidita.getTime();
		codice = soap.codice;
		numeroTabella = nt;
		descrizione = soap.descrizione;
	}
	
	public RecordTabellaSupporto(){
		tipo = "";
		validita = 0;
		codice = -1;
		numeroTabella = -1;
		descrizione = "";
	}
}
