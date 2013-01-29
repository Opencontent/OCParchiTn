package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Struttura;

import java.util.HashMap;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ControlloFragment extends Fragment implements ICustomFragment {

	private static final String TAG = ControlloFragment.class.getSimpleName();
	
	public static int tipoStruttura = Constants.CODICE_STRUTTURA_GIOCO;
	public static String methodName = Constants.GET_GIOCO_METHOD_NAME;
	public static int soapMethodName = Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID;
	public static int tipoControllo = Constants.CODICE_STRUTTURA_CONTROLLO_VISIVO;
	private Controllo controllo;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.controllo_fragment, container, false);
		controllo = new Controllo();
		return view;
	}

	@Override
	public void salvaModifiche(View v) {
		Log.d(TAG,"salva modifiche alla periodica");
		
	}
	private long saveLocal(Controllo c){
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		long id = db.salvaControlloLocally(c);
		if(id > 0){
			Toast.makeText(getActivity().getApplicationContext(),"Operazione salvata localmente", Toast.LENGTH_SHORT).show();
			MainActivity ma = (MainActivity) getActivity();
			ma.updateCountDaSincronizzare();
		} else if (id == -2){
			//constraint error
		}
		return id;
	}
	@Override
	public void editMe(View v) {
		//Esiti controlli
		
	}
	
	public void showError(HashMap<String,String> map){
		
	}

	@Override
	public void showStrutturaData(Struttura struttura) {
		Log.d(TAG,struttura.toString());
	}	
	@Override
	public void clickedMe(View v) {
		switch(v.getId()){
		case R.id.radio_gioco:
			tipoStruttura = Constants.CODICE_STRUTTURA_GIOCO;
			methodName = Constants.GET_GIOCO_METHOD_NAME;
			soapMethodName = Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID;
			break;
		case R.id.radio_area:
			tipoStruttura = Constants.CODICE_STRUTTURA_AREA;
			methodName = Constants.GET_AREA_METHOD_NAME;
			soapMethodName = Constants.SOAP_GET_AREA_REQUEST_CODE_BY_RFID;
			break;
		case R.id.radio_controllo:
			tipoControllo = Constants.CODICE_STRUTTURA_CONTROLLO_VISIVO;
			break;
		case R.id.radio_verifica:
			tipoControllo = Constants.CODICE_STRUTTURA_VERIFICA;
			break;
		case R.id.radio_intervento:
			tipoControllo = Constants.CODICE_STRUTTURA_INTERVENTO;
			break;
		case R.id.radio_manutenzione:
			tipoControllo = Constants.CODICE_STRUTTURA_MANUTENZIONE;
			break;
			
		}
	}	
}
