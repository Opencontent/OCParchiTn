package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkAutException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkGiochiException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkSrvException;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.activities.SynchroSoapActivity;
import it.opencontent.android.ocparchitn.adapters.SyncItemAdapter;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Intervento;
import it.opencontent.android.ocparchitn.db.entities.Struttura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SyncFragment extends Fragment  implements ICustomFragment{

	private final static String TAG = SyncFragment.class.getSimpleName();
	private ArrayList<Struttura> sincronizzazioniList;
	private SyncItemAdapter adapterSincronizzazioni;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.sync_fragment, container, false);		
		return view;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		OCParchiDB db = new OCParchiDB(getActivity());
		LinkedHashMap<String, Struttura> sincronizzazioni = db.getStruttureDaSincronizzare(true);
		db.close();
		sincronizzazioniList  = new ArrayList<Struttura>(sincronizzazioni.values());
		adapterSincronizzazioni = new SyncItemAdapter(getActivity(), R.layout.sincronizzazione_list_item, sincronizzazioniList);
		
		ListView lv = (ListView) getActivity().findViewById(R.id.sync_main_listview);
		lv.setAdapter(adapterSincronizzazioni);		
	}
	
	
	@Override
	public void salvaModifiche(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editMe(View v) {
		// TODO Auto-generated method stub
		
	}
	public void showError(HashMap<String,String> map){
		
	}

	@Override
	public void showStrutturaData(Struttura struttura) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clickedMe(View v) {
		Object struttura;
		switch(v.getId()){
		case R.id.pulsante_invia_foto_singola_sincronizzazione:
			//TODO: sincronizziamo le foto
			struttura = v.getTag(R.integer.controllo_button_tag);
			if(struttura != null && struttura instanceof Struttura){
				Intent serviceIntent = new Intent();
				serviceIntent.setClass(getActivity(),
						SynchroSoapActivity.class);
				serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME,
						Constants.EXTRAKEY_SYNC_ONE_FOTO);
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				String tipo = ((Struttura) struttura).tipo;
				String id = "";
				map.put(Constants.EXTRAKEY_STRUCTURE_TYPE, tipo);
				if(struttura.getClass().equals(Gioco.class)){
					id=((Gioco) struttura).idGioco+"";
				} else if(struttura.getClass().equals(Area.class)){
					id = ((Area) struttura).idArea+"";
				} else if(struttura.getClass().equals(Controllo.class)){
					id = ((Controllo) struttura).idRiferimento;
				} else if(struttura.getClass().equals(Intervento.class)){
					id = ((Intervento) struttura).idRiferimento;
				}
				map.put(Constants.EXTRAKEY_STRUCTURE_ID, id);
				serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
				
				String mapId = Constants.PREFISSO_SINCRONIZZAZIONE+tipo+"_"+id;
				serviceIntent.putExtra(Constants.EXTRAKEY_MAPID, mapId);
				serviceIntent.putExtra(Constants.EXTRAKEY_SYNC_INDEX, Integer.parseInt(v.getTag(R.integer.controllo_button_tag_posizione)+"" ));				
				startActivityForResult(serviceIntent, Constants.SOAP_SINCRONIZZA_SINGOLO_FOTO_REQUEST_CODE);
			}
			break;
		case R.id.pulsante_lancia_singola_sincronizzazione:
			struttura = v.getTag(R.integer.controllo_button_tag);
			if(struttura != null && struttura instanceof Struttura){
				Intent serviceIntent = new Intent();
				serviceIntent.setClass(getActivity(),
						SynchroSoapActivity.class);
				serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME,
						Constants.EXTRAKEY_SYNC_ONE);
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				String tipo = ((Struttura) struttura).tipo;
				String id = "";
				map.put(Constants.EXTRAKEY_STRUCTURE_TYPE, tipo);
				if(struttura.getClass().equals(Gioco.class)){
					id=((Gioco) struttura).idGioco+"";
				} else if(struttura.getClass().equals(Area.class)){
					id = ((Area) struttura).idArea+"";
				} else if(struttura.getClass().equals(Controllo.class)){
					id = ((Controllo) struttura).idRiferimento;
				} else if(struttura.getClass().equals(Intervento.class)){
					id = ((Intervento) struttura).idRiferimento;
				}
				map.put(Constants.EXTRAKEY_STRUCTURE_ID, id);
				serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
	
				String mapId = Constants.PREFISSO_SINCRONIZZAZIONE+tipo+"_"+id;
				serviceIntent.putExtra(Constants.EXTRAKEY_MAPID, mapId);
				serviceIntent.putExtra(Constants.EXTRAKEY_SYNC_INDEX, Integer.parseInt(v.getTag(R.integer.controllo_button_tag_posizione)+"" ));				
				startActivityForResult(serviceIntent, Constants.SOAP_SINCRONIZZA_SINGOLO_REQUEST_CODE);
			}
			break;
		case R.id.pulsante_elimina_singola_sincronizzazione:
			OCParchiDB db = new OCParchiDB(getActivity());
			struttura = v.getTag(R.integer.controllo_button_tag);
			int posizione = Integer.parseInt(v.getTag(R.integer.controllo_button_tag_posizione)+"");
			sincronizzazioniList.remove(posizione);
			adapterSincronizzazioni.notifyDataSetChanged();
			db.eliminaCopiaLocaleDiStrutturaSincronizzata((Struttura) struttura);
			db.close();
			((MainActivity) getActivity()).updateCountDaSincronizzare();
			break;
		}
		
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		HashMap<String, Object> res;
		switch(requestCode){
		case Constants.SOAP_SINCRONIZZA_SINGOLO_REQUEST_CODE:
			if(resultCode == Activity.RESULT_OK){
				String mapId = intent.getExtras().getString(Constants.EXTRAKEY_MAPID);
				res = SynchroSoapActivity.getRes(mapId);
				OCParchiDB db = new OCParchiDB(getActivity());
				HashMap<String,Object> map = (HashMap<String,Object>) intent.getExtras().get(Constants.EXTRAKEY_DATAMAP);
				String tipo = (String) map.get(Constants.EXTRAKEY_STRUCTURE_TYPE);
				String id = (String) map.get(Constants.EXTRAKEY_STRUCTURE_ID);
				int posizione = intent.getExtras().getInt(Constants.EXTRAKEY_SYNC_INDEX);
				if(res!= null && res.containsKey("success")){
					if(res.containsKey("exception")){

						Object exception = res.get("exception");
						String messaggio = "";
						if(exception.getClass().equals(SOAPSrvGiocoArkGiochiException.class)){
							 messaggio = ((SOAPSrvGiocoArkGiochiException) exception).message;
						} else if(exception.getClass().equals(SOAPSrvGiocoArkSrvException.class)){
							messaggio = ((SOAPSrvGiocoArkSrvException) exception).message;						
						} else if(exception.getClass().equals(SOAPSrvGiocoArkAutException.class)){
							messaggio = ((SOAPSrvGiocoArkAutException) exception).message;
						}
							
							
						sincronizzazioniList.get(posizione).erroreRemoto = messaggio;
						db.segnaEccezioneRemotaSuUpdate(tipo, id, messaggio);
						adapterSincronizzazioni.notifyDataSetChanged();
					}
					Log.d(TAG,"res non nullo");
				} else if(res != null){
					sincronizzazioniList.get(posizione).sincronizzato = true;
					db.marcaCopiaLocaleDiStrutturaSincronizzata(tipo,id);
					sincronizzazioniList.get(posizione).erroreRemoto = "";
					db.segnaEccezioneRemotaSuUpdate(tipo, id, "");
					adapterSincronizzazioni.notifyDataSetChanged();
					
				}else {
					Log.d(TAG,"res nullo");
				}
				db.close();
			}			
			break;
		}
	}
	
}
