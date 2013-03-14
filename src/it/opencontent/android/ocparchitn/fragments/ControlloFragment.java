package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Intervento;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ControlloFragment extends Fragment implements ICustomFragment {

	private static final String TAG = ControlloFragment.class.getSimpleName();
	
	public static int tipoStruttura = Constants.CODICE_STRUTTURA_GIOCO;
	public static String methodName = Constants.GET_GIOCO_METHOD_NAME;
	public static int soapMethodName = Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID;
	private ArrayAdapter<RecordTabellaSupporto> adapterEsitiControllo;
	private ArrayAdapter<RecordTabellaSupporto> adapterTipiSegnalazione;
	private static Controllo currentControllo;

	private static List<Controllo> elencoControlli = new ArrayList<Controllo>();
	
	public static void appendControllo(Controllo controllo){
		elencoControlli.add(controllo);		
	}
	
	public static Controllo getCurrentIntervento(){
		return currentControllo;
	}
	
	public final void abilitaControllo(Controllo c){
		TextView controlloTeaser = (TextView) getActivity().findViewById(R.id.display_controllo_selezionato_text);
		controlloTeaser.setText(getString(R.string.controllo_mostra_controllo_attuale_prefisso)+" "+c.descrizioneControllo);
		if(elencoControlli.contains(c)){
			Log.d(TAG,c.toString());
			currentControllo = c;
		} 
			OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
			Controllo cc = db.readControlloLocallyByID(c.idRiferimento,true);
			if(cc!=null){
				currentControllo = cc;
				Log.d(TAG,cc.toString());
			}
			
			Spinner view = (Spinner) getActivity().findViewById(R.id.display_controllo_spinner_segnalazione);
			
			int min = 0;
			int max = 0;
			
			switch(currentControllo.tipoControllo){
			case 1: 
				min = 1;
				max = 10;
				break;
			case 2: 
				min = 11;
				max = 19;
				break;
			case 3: 
				min = 21;
				max = 29;
				break;
			}
			
			List<RecordTabellaSupporto> records = db.tabelleSupportoGetAllRecordsFiltered(Constants.TABELLA_SEGNALAZIONI,min,max);
			db.close();
			if(records != null){
				setupSpinnerTipiSegnalazione( view , records);
			}			
		
		resetInterfaccia();
	}
	
	public static void aggiungiSnapshotAControlloCorrente(String base64, int indice){
		if(currentControllo != null){
			switch(indice){
			case 0:
				currentControllo.foto0 = base64;
				break;
			case 1:
				currentControllo.foto1 = base64;
				break;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		currentControllo = null;
		elencoControlli.clear();
		View view = inflater.inflate(R.layout.controllo_fragment, container, false);
		
		
		return view;
	}
	
	public void setupScrollViewControlli(){
		LinearLayout controlli = (LinearLayout) getActivity().findViewById(R.id.scroll_view_controlli);
		Iterator<Controllo> iterator = elencoControlli.iterator();
		while(iterator.hasNext()){
			Button controlloButton = new Button(getActivity());
			Controllo c = iterator.next();
			

			OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
			RecordTabellaSupporto rts = db.tabelleSupportoGetRecord(Constants.TABELLA_RIFERIMENTO_INTERVENTO, c.tipoControllo);
			db.close();
			c.descrizioneControllo = rts.descrizione;
			String testoBottone = rts.descrizione;
			if(c.dtScadenzaControllo != null && c.dtScadenzaControllo.length() > 0){
				testoBottone+="\nEntro il "+c.dtScadenzaControllo;
			}
			controlloButton.setText(testoBottone);
			controlloButton.setTag(R.integer.controllo_button_tag, c);
			controlloButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Button b = (Button) v;
					Object o = b.getTag(R.integer.controllo_button_tag);
					abilitaControllo((Controllo) o);
				}
			});
			controlli.addView(controlloButton);		
			
		}
		
	}
	
	private void setupSpinnerTipiSegnalazione(Spinner view, List<RecordTabellaSupporto> records) {
		adapterTipiSegnalazione = new ArrayAdapter<RecordTabellaSupporto>(getActivity(), R.layout.default_spinner_layout,records);
		adapterTipiSegnalazione.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinnerTipiSegnalazione = view;
		spinnerTipiSegnalazione.setAdapter(adapterTipiSegnalazione);
		
		
		spinnerTipiSegnalazione.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				RecordTabellaSupporto r = (RecordTabellaSupporto) spinnerTipiSegnalazione.getAdapter().getItem(arg2);
				if(currentControllo != null){
					currentControllo.tipoSegnalazione = r.codice;
					saveLocal(currentControllo);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
		});
	}
	
	private void resetInterfaccia(){
		if(currentControllo != null){
			TextView note = (TextView) getActivity().findViewById(R.id.display_controllo_nota);
			note.setText(currentControllo.noteControllo);

			Spinner spinnerTipiSegnalazione = (Spinner) getActivity().findViewById(R.id.display_controllo_spinner_segnalazione);
			spinnerTipiSegnalazione.setSelection(0);	
			for(int i = 0; i < adapterTipiSegnalazione.getCount(); i++ ){
				if(adapterTipiSegnalazione.getItem(i).codice == currentControllo.tipoSegnalazione){
					spinnerTipiSegnalazione.setSelection(i);					
				}
			}
			
			ImageView v;
			int width=100;
			int height = 100;
			if(currentControllo.foto0!=null){
				v = (ImageView) getActivity().findViewById(R.id.snapshot_controllo_0);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(currentControllo.foto0, Base64.DEFAULT),getResources(),1,width,height));
			}
			if(currentControllo.foto1!=null){
				v = (ImageView) getActivity().findViewById(R.id.snapshot_controllo_1);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(currentControllo.foto1, Base64.DEFAULT),getResources(),2,width,height));
			}
			
		} else {
			TextView controlloTeaser = (TextView) getActivity().findViewById(R.id.display_controllo_selezionato_text);
			controlloTeaser.setText(getString(R.string.controllo_mostra_controllo_attuale_prefisso));
			
			TextView note = (TextView) getActivity().findViewById(R.id.display_controllo_nota);
			note.setText("");
				
			Spinner spinnerTipiSegnalazione = (Spinner) getActivity().findViewById(R.id.display_controllo_spinner_segnalazione);
			spinnerTipiSegnalazione.setSelection(0);					
			
			
			ImageView v;
			v = (ImageView) getActivity().findViewById(R.id.snapshot_controllo_0);
			v.setImageBitmap(null);
			v = (ImageView) getActivity().findViewById(R.id.snapshot_controllo_1);
			v.setImageBitmap(null);
			LinearLayout l = (LinearLayout) getActivity().findViewById(R.id.scroll_view_controlli);
			l.removeAllViews();
			clearDisplayGioco();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void salvaModifiche(View v) {
		Log.d(TAG,"salva modifiche alla periodica");
		if(elencoControlli.size()>0){
			saveLocal(elencoControlli.get(0));
		}
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
	public void editMe(View v){
		Log.d(TAG,"editme nel fragment");	
		switch(v.getId()){
		case R.id.display_area_tipoPavimentazione:
			break;
		default: 
			TextView t = (TextView) v;
			changeTextValueThroughAlert(t);
			break;
		}
	}


	private void changeTextValueThroughAlert(TextView t) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Modifica il dato");
		alert.setMessage("Controllo: ");

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		input.setText(t.getText());
		input.setTag(R.integer.tag_view_id, t.getId());
		final int viewId =Integer.parseInt(input.getTag(R.integer.tag_view_id).toString());
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				try {
					switch(viewId){
					case R.id.display_controllo_nota:
						if(currentControllo != null){
							currentControllo.noteControllo =value;
							saveLocal(currentControllo);
							updateText(viewId,value);
						}
						break;
					}
				} catch (NumberFormatException nfe) {

				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		alert.show();
	}
	
	private void updateText(int viewId, String text){
		TextView t = (TextView) getActivity().findViewById(viewId);
		if(t != null){
			t.setText(text);
		}
	}	
	
	private void displayGioco(Gioco gioco){
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_gioco_id);
		v.setText(""+gioco.idGioco);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_marca);
		v.setText(gioco.descrizioneMarca);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_descrizione);
		v.setText(gioco.descrizioneGioco);
		v = (TextView) getActivity().findViewById(R.id.display_area_descrizione);
		v.setText(gioco.descrizioneArea);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText(gioco.note);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_seriale);
		v.setText(gioco.numeroSerie);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_posizione_rfid);
		v.setText(((Gioco) gioco).posizioneRfid);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_rfid);
		v.setText(gioco.rfid+"");
		
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(gioco.rfidArea+"");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsx);
		v.setText(gioco.gpsx + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsy);
		v.setText(gioco.gpsy + "");		
		
		
	}
	private void clearDisplayGioco(){
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_gioco_id);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_marca);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_descrizione);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_area_descrizione);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_seriale);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_posizione_rfid);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_rfid);
		v.setText("");
		
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsx);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsy);
		v.setText("");		
		v = (TextView) getActivity().findViewById(R.id.display_area_id);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText("");
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText("");
		v  = (TextView) getActivity().findViewById(R.id.display_area_tipoPavimentazione_fissa);
		v.setText("");		
		
	}
	private void displayArea(Area area){
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_area_id);
		v.setText(""+area.idArea);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText(area.note);
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(area.rfidArea+"");
		v = (TextView) getActivity().findViewById(R.id.display_area_descrizione);
		v.setText(area.descrizioneArea + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_posizione_rfid);
		v.setText(area.posizioneRfid + "");
		
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		RecordTabellaSupporto tipoPavimentazione = db.tabelleSupportoGetRecord(Constants.TABELLA_TIPO_PAVIMENTAZIONI, area.tipoPavimentazione);
		db.close();
		v  = (TextView) getActivity().findViewById(R.id.display_area_tipoPavimentazione_fissa);
		v.setText(tipoPavimentazione.descrizione);
	}
	
	
	public void showError(HashMap<String,String> map){
		
	}

	@Override
	public void showStrutturaData(Struttura struttura) {
		if(struttura!=null){
		if(struttura.getClass().equals(Gioco.class)){
			displayGioco((Gioco) struttura);
		}	else  if(struttura.getClass().equals(Area.class)){
			displayArea((Area) struttura);
		}
		setupSnapshots(struttura);
		}
	}
	
	private void setupSnapshots(Struttura struttura) {
		int width = 100;
		int height= 100;
		ImageView v;
		for (int i = 0; i < Constants.MAX_SNAPSHOTS_AMOUNT; i++) {
			switch(i){
			case 0:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_0);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(struttura.foto0, Base64.DEFAULT),getResources(),1,width,height));
				break;
			case 1:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_1);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(struttura.foto1, Base64.DEFAULT),getResources(),2,width,height));
				break;
			}
		}
	}	


	@Override
	public void clickedMe(View v) {
		switch(v.getId()){
		case R.id.radio_gioco:
			tipoStruttura = Constants.CODICE_STRUTTURA_GIOCO;
			methodName = Constants.GET_GIOCO_METHOD_NAME;
			soapMethodName = Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID;
			elencoControlli.clear();
			currentControllo = null;
			resetInterfaccia();
			break;
		case R.id.radio_area:
			tipoStruttura = Constants.CODICE_STRUTTURA_AREA;
			methodName = Constants.GET_AREA_METHOD_NAME;
			soapMethodName = Constants.SOAP_GET_AREA_REQUEST_CODE_BY_RFID;
			elencoControlli.clear();
			currentControllo = null;
			resetInterfaccia();
			break;			
		}
	}	
}
