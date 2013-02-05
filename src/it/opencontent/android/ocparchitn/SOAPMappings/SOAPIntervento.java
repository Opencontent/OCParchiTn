package it.opencontent.android.ocparchitn.SOAPMappings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class SOAPIntervento implements KvmSerializable {

	
	public int codEsito;
	public int codTipologia;
	
	public String descEsito;
	public String descTipologia;
	
	public Date dtFineItervento;
	public Date dtInizioItervento;
	public int idGioco;
	public int idIntervento;
	public String idRiferimento;
	public int intervento;
	public String noteEsecuzione;
	public String noteRichiesta;
	public String oraFineItervento;
	public String oraInizioItervento;
	public String rfid;
	public int stato;
	public int tipoIntervento;
	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0){
		case 0:
			return codEsito;
		case 1:
			return descEsito;
		case 2:
			return codTipologia;
		case 3:
			return dtFineItervento;
		case 4:
			return dtInizioItervento;
		case 5:
			return idGioco;
		case 6:
			return idIntervento;
		case 7:
			return idRiferimento;
		case 8:
			return intervento;
		case 9:
			return noteEsecuzione;
		case 10:
			return noteRichiesta;
		case 11:
			return oraFineItervento;
		case 12:
			return oraInizioItervento;
		case 13:
			return rfid;
		case 14:
			return stato;
		case 15:
			return tipoIntervento;
		case 16:
			return descTipologia;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 17;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		switch(arg0){
		case 0:
			arg2.name = "codEsito";
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			break ;
		case 1:
			arg2.name = "descEsito";
			arg2.type = PropertyInfo.STRING_CLASS ;
			break ;
		case 2:
			arg2.name = "codTipologia";
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			break ;
		case 3:
			arg2.name = "dtFineItervento";
			arg2.type = PropertyInfo.OBJECT_CLASS ;
			break ;
		case 4:
			arg2.name = "dtInizioItervento";
			arg2.type = PropertyInfo.OBJECT_CLASS ;
			break ;
		case 5:
			arg2.name = "idGioco";
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			break ;
		case 6:
			arg2.name = "idIntervento";
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			break ;
		case 7:
			arg2.name = "idRiferimento";
			arg2.type = PropertyInfo.STRING_CLASS ;
			break ;
		case 8:
			arg2.name = "intervento";
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			break ;		
		case 9:
			arg2.name = "noteEsecuzione";
			arg2.type = PropertyInfo.STRING_CLASS ;
			break ;
		case 10:
			arg2.name = "noteRichiesta";
			arg2.type = PropertyInfo.STRING_CLASS ;
			break ;
		case 11:
			arg2.name = "oraFineItervento";
			arg2.type = PropertyInfo.STRING_CLASS ;
			break ;
		case 12:
			arg2.name = "oraInizioItervento";
			arg2.type = PropertyInfo.STRING_CLASS ;
			break ;
		case 13:
			arg2.name = "rfid";
			arg2.type = PropertyInfo.STRING_CLASS ;
			break ;
		case 14:
			arg2.name = "stato";
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			break ;
		case 15:
			arg2.name = "tipoIntervento";
			arg2.type = PropertyInfo.INTEGER_CLASS ;
			break ;
		case 16:
			arg2.name = "descTipologia";
			arg2.type = PropertyInfo.STRING_CLASS;
			break ;
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern,Locale.US);
		if(arg1!=null){
		switch(arg0){
		case 0:
			codEsito = Integer.parseInt(arg1.toString());
			break ;
		case 1:
			descEsito = arg1.toString();
			break ;
		case 2:
			codTipologia = Integer.parseInt(arg1.toString());
			break ;
		case 3:
			try {
				dtFineItervento = sdf.parse(arg1+"");
			} catch (ParseException e) {
				e.printStackTrace();
			} 
			break ;
		case 4:
			try {
				dtInizioItervento = sdf.parse(arg1+"");
			} catch (ParseException e) {
				e.printStackTrace();
			} 
			break ;
		case 5:
			idGioco = Integer.parseInt(arg1.toString());
			break ;
		case 6:
			idIntervento = Integer.parseInt(arg1.toString());
			break ;
		case 7:
			
			idRiferimento =arg1.toString();
			break ;
		case 8:
			intervento = Integer.parseInt(arg1.toString());
			break ;
		case 9:
			noteEsecuzione =arg1.toString();
			break ;
		case 10:
			noteRichiesta =arg1.toString();
			break ;
		case 11:
			oraFineItervento =arg1.toString();
			break ;
		case 12:
			oraInizioItervento =arg1.toString();
			break ;
		case 13:
			rfid =arg1.toString();
			break ;
		case 14:
			stato = Integer.parseInt(arg1.toString());
			break ;
		case 15:
			tipoIntervento = Integer.parseInt(arg1.toString());
			break ;			
		case 16:
			descTipologia = arg1.toString();
			break ;			
		}
		}
	}

}
