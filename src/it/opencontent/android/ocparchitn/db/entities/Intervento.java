package it.opencontent.android.ocparchitn.db.entities;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPIntervento;
import android.content.Context;

public class Intervento extends Struttura {

	public String posizioneRfid;

	public int codEsito;
	public int codTipologia;
	public String descEsito;
	public String descTipologia;
	public String dtFineItervento;
	public String dtInizioItervento;
	public int idIntervento;
	public int intervento;
	public String noteEsecuzione;
	public String noteRichiesta;
	public String oraFineItervento;
	public String oraInizioItervento;
	public int stato;
	public int tipoIntervento;
	public String idRiferimento;
	

	
	
	public Intervento() {
		tipo = "intervento";
	}

	public Intervento(SOAPIntervento remote) {
		super();
		tipo = "intervento";

		codEsito = remote.codEsito;
		codTipologia = remote.codTipologia;
		descEsito = remote.descEsito;
		descTipologia = remote.descTipologia;
		dtFineItervento = remote.dtFineItervento.toString();
		dtInizioItervento = remote.dtInizioItervento.toString();
		idGioco = remote.idGioco;
		idIntervento = remote.idIntervento;
		idRiferimento = remote.idRiferimento;
		intervento = remote.intervento;
		noteEsecuzione = remote.noteEsecuzione;
		noteRichiesta = remote.noteRichiesta;
		oraFineItervento = remote.oraFineItervento;
		oraInizioItervento = remote.oraInizioItervento;
		try{
			rfid = Integer.parseInt(remote.rfid);
		} catch (NumberFormatException e){
			e.printStackTrace();
			rfid = 0;
		}
		stato = remote.stato;
		tipoIntervento = remote.tipoIntervento;
	}

	public Intervento(Set<Entry<String, Object>> entrySet, Context context) {
		super(entrySet, context);
		tipo = "intervento";
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			if (e.getKey().equals("codEsito")) {

				codEsito = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("codTipologia")) {
				codTipologia = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("descEsito")) {
				descEsito = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("descTipologia")) {
				descTipologia = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("dtFineItervento")) {
				dtFineItervento = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("dtInizioItervento")) {
				dtInizioItervento = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("idGioco")) {
				idGioco = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("idIntervento")) {
				idIntervento = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("idRiferimento")) {
				idRiferimento = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("intervento")) {
				intervento = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("noteEsecuzione")) {
				noteEsecuzione = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("noteEsecuzione")) {
				noteRichiesta = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("oraFineItervento")) {
				oraFineItervento = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("oraInizioItervento")) {
				oraInizioItervento = bindStringToProperty(e.getValue(),
						e.getKey());
			}

			if (e.getKey().equals("stato")) {
				stato = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("tipoIntervento")) {
				tipoIntervento = bindIntToProperty(e.getValue(), e.getKey());
			}
		}

	}

}
