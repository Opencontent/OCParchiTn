package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPCodTabella;

import java.util.Date;

public class RecordTabellaSupporto {
	public int numeroTabella;
	public int codice;
	public String descrizione;
	public long validita; 
	public String tipo;
	
	public RecordTabellaSupporto(int nt, SOAPCodTabella soap){
		tipo = "";
		validita = new Date().getTime() + 600000;
		codice = soap.codice;
		numeroTabella = nt;
		descrizione = soap.descrizione;
	}
}
