package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Intervento;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.xml.sax.DTDHandler;

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

public class InterventoFragment extends Fragment implements ICustomFragment {

	private static final String TAG = InterventoFragment.class.getSimpleName();
	
	public static int tipoStruttura = Constants.CODICE_STRUTTURA_GIOCO;
	public static String methodName = Constants.GET_GIOCO_METHOD_NAME;
	public static int soapMethodName = Constants.SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID;
	public static int tipoControllo = Constants.CODICE_STRUTTURA_CONTROLLO_VISIVO;
	private ArrayAdapter<RecordTabellaSupporto> adapterEsitiControllo;
	private ArrayAdapter<RecordTabellaSupporto> adapterTipiSegnalazione;
	private static Intervento currentIntervento;

	/**
	 * Il primo controlo sullo stack sarà sempre quello attivo
	 * cui faranno riferimento i metodi delle altre classi
	 * Se la lista è vuota ne sarà creato uno
	 */
	private static List<Intervento> elencoInterventi = new ArrayList<Intervento>();
	
	public static void appendControllo(Intervento intervento){
		elencoInterventi.add(intervento);		
	}
	
	public final void abilitaControllo(Intervento c){
		TextView controlloTeaser = (TextView) getActivity().findViewById(R.id.display_controllo_selezionato_text);
		controlloTeaser.setText(getString(R.string.controllo_mostra_controllo_attuale_prefisso)+" "+c.descTipologia);
		if(elencoInterventi.contains(c)){
			Log.d(TAG,c.toString());
			currentIntervento = c;
		} 
			OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
			Intervento cc = db.readInterventoLocallyByID(c.idIntervento+"");
			if(cc!=null){
				currentIntervento = cc;
				Log.d(TAG,cc.toString());
			}
		
		resetInterfaccia();
	}
	
	public static void aggiungiSnapshotAControlloCorrente(String base64, int indice){
		if(elencoInterventi.size()>0){
			switch(indice){
			case 0:
				elencoInterventi.get(0).foto0 = base64;
				break;
			case 1:
				elencoInterventi.get(0).foto1 = base64;
				break;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		View view = inflater.inflate(R.layout.intervento_fragment, container, false);
		List<RecordTabellaSupporto> records = db.tabelleSupportoGetAllRecords(Constants.TABELLA_ESITI_INTERVENTO);
		if(records != null){
		 setupSpinnerEsitiControllo(view, records);
		}		
		db.close();
		return view;
	}
	
	
	
	
	public void setupScrollViewInterventi(){
		LinearLayout controlli = (LinearLayout) getActivity().findViewById(R.id.scroll_view_controlli);
		Iterator<Intervento> iterator = elencoInterventi.iterator();
		while(iterator.hasNext()){
			Button controlloButton = new Button(getActivity());
			Intervento c = iterator.next();
			

			OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
			RecordTabellaSupporto rts = db.tabelleSupportoGetRecord(Constants.TABELLA_RIFERIMENTO_INTERVENTO, c.tipoIntervento);
			db.close();
			
			controlloButton.setText(rts.descrizione+" "+c.idIntervento);
			controlloButton.setTag(R.integer.controllo_button_tag, c);
			controlloButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Button b = (Button) v;
					Object o = b.getTag(R.integer.controllo_button_tag);
					abilitaControllo((Intervento) o);
				}
			});
			controlli.addView(controlloButton);		
			
		}
		
	}
	
	private void setupSpinnerEsitiControllo(View view, List<RecordTabellaSupporto> records) {
		adapterEsitiControllo = new ArrayAdapter<RecordTabellaSupporto>(getActivity(), R.layout.default_spinner_layout,records);
		adapterEsitiControllo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinnerEsitiControllo = (Spinner) view.findViewById(R.id.display_controllo_spinner_esito);
		spinnerEsitiControllo.setAdapter(adapterEsitiControllo);
		
		
		spinnerEsitiControllo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				RecordTabellaSupporto r = (RecordTabellaSupporto) spinnerEsitiControllo.getAdapter().getItem(arg2);
				if(elencoInterventi.size()>0){
					elencoInterventi.get(0).codEsito = r.codice;
					saveLocal(elencoInterventi.get(0));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	
	
	private void resetInterfaccia(){
		if(currentIntervento != null){
			
			String DATE_FORMAT = "yyyy-MM-dd";
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT,Locale.US);


			
			Button pulsanteInizia = (Button) getActivity().findViewById(R.id.pulsanteIniziaIntervento);
			Button pulsanteTermina = (Button) getActivity().findViewById(R.id.pulsanteTerminaIntervento);

			if(currentIntervento.dtInizioItervento == null){
				pulsanteInizia.setEnabled(true);
			} else {
				pulsanteInizia.setEnabled(false);
			}
			if(currentIntervento.dtFineItervento == null){
				pulsanteTermina.setEnabled(true);
			} else {
				pulsanteTermina.setEnabled(false);
			}
			
			TextView note = (TextView) getActivity().findViewById(R.id.display_controllo_nota);
			note.setText(currentIntervento.noteEsecuzione);
			Spinner spinnerEsitiControllo = (Spinner) getActivity().findViewById(R.id.display_controllo_spinner_esito);
			for(int i = 0; i < adapterEsitiControllo.getCount(); i++ ){
				if(adapterEsitiControllo.getItem(i).codice == currentIntervento.codEsito){
					spinnerEsitiControllo.setSelection(i);					
				}
			}
			TextView noteRichiesta = (TextView) getActivity().findViewById(R.id.note_richiesta_intervento);
			
			noteRichiesta.setText(getString(R.string.intervento_mostra_note_richiesta_prefisso)+" "+currentIntervento.noteRichiesta);
			
			ImageView v;
			int width=100;
			int height = 100;
			if(currentIntervento.foto0!=null){
				v = (ImageView) getActivity().findViewById(R.id.snapshot_controllo_0);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(currentIntervento.foto0, Base64.DEFAULT),getResources(),1,width,height));
			}
			if(currentIntervento.foto1!=null){
				v = (ImageView) getActivity().findViewById(R.id.snapshot_controllo_1);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(currentIntervento.foto1, Base64.DEFAULT),getResources(),2,width,height));
			}
			
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void salvaModifiche(View v) {
		Log.d(TAG,"salva modifiche alla periodica");
		if(elencoInterventi.size()>0){
			long id = saveLocal(elencoInterventi.get(0));
//			if(id>0){
//				elencoControlli.remove(0);
//			}
		}
	}
	
	private long saveLocal(Intervento c){
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		long id = db.salvaInterventoLocally(c);
		if(id > 0){
			String message  = "Operazione salvata localmente";
			if(c.noteEsecuzione == null || c.noteEsecuzione.equals("")){
				message += "\nMANCA LA NOTA";
			}
			Toast.makeText(getActivity().getApplicationContext(),message, Toast.LENGTH_LONG).show();
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
						if(elencoInterventi.size()>0){
							elencoInterventi.get(0).noteEsecuzione =value;
							saveLocal(elencoInterventi.get(0));
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
	private void displayArea(Area area){
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_area_id);
		v.setText(""+area.idArea);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText(area.note);
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(area.rfidArea+"");
		/*
		v = (TextView) getActivity().findViewById(R.id.display_area_spessore);
		v.setText(area.spessore + "");
		v = (TextView) getActivity().findViewById(R.id.display_area_superficie);
		v.setText(area.superficie + "");
		 */
		v = (TextView) getActivity().findViewById(R.id.display_area_descrizione);
		v.setText(area.descrizioneArea + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_posizione_rfid);
		v.setText(area.posizioneRfid + "");
		
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		RecordTabellaSupporto tipoPavimentazione = db.tabelleSupportoGetRecord(Constants.TABELLA_TIPO_PAVIMENTAZIONI, area.tipoPavimentazione);
		
		v  = (TextView) getActivity().findViewById(R.id.display_area_tipoPavimentazione_fissa);
		v.setText(tipoPavimentazione.descrizione);
	}
	
	public void showError(HashMap<String,String> map){
		
	}

	@Override
	public void showStrutturaData(Struttura struttura) {
		if(struttura.getClass().equals(Gioco.class)){
			displayGioco((Gioco) struttura);
		}	else  if(struttura.getClass().equals(Area.class)){
			displayArea((Area) struttura);
		}
		setupSnapshots(struttura);
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
			break;
		case R.id.radio_area:
			tipoStruttura = Constants.CODICE_STRUTTURA_AREA;
			methodName = Constants.GET_AREA_METHOD_NAME;
			soapMethodName = Constants.SOAP_GET_AREA_REQUEST_CODE_BY_RFID;
			break;		
		case R.id.pulsanteIniziaIntervento:
			if(elencoInterventi.size()>0){
				String DATE_FORMAT = "yyyy-MM-dd";
				String TIME_FORMAT = "HH:mm:ss";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT,Locale.US);
				SimpleDateFormat hdf = new SimpleDateFormat(TIME_FORMAT,Locale.US);
				Calendar calendar = Calendar.getInstance(); 			
				elencoInterventi.get(0).dtInizioItervento = sdf.format(calendar.getTime()); 
				elencoInterventi.get(0).oraInizioItervento = hdf.format(calendar.getTime()); 
				resetInterfaccia();
				salvaModifiche(null);
			}
			break;
		case R.id.pulsanteTerminaIntervento:
			if(elencoInterventi.size()>0){
				String DATE_FORMAT = "yyyy-MM-dd";
				String TIME_FORMAT = "HH:mm:ss";
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT,Locale.US);
				SimpleDateFormat hdf = new SimpleDateFormat(TIME_FORMAT,Locale.US);
				Calendar calendar = Calendar.getInstance(); 			
				elencoInterventi.get(0).dtFineItervento = sdf.format(calendar.getTime()); 
				elencoInterventi.get(0).oraFineItervento = hdf.format(calendar.getTime());
				resetInterfaccia();
				salvaModifiche(null);
			}
			break;
		}
	}	
}
