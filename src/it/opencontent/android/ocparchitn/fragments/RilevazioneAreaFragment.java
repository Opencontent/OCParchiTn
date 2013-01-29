package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.utils.Utils;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Marco Albarelli <info@marcoalbarelli.eu>
 */

public class RilevazioneAreaFragment extends Fragment implements ICustomFragment{

	private static final String TAG = RilevazioneAreaFragment.class.getSimpleName();

	private ArrayAdapter<RecordTabellaSupporto> adapterTipoPavimentazione;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		View view = inflater.inflate(R.layout.rilevazione_area, container, false);
		
		/**
		 * Setup spinners
		 */
		List<RecordTabellaSupporto> records = db.tabelleSupportoGetAllRecords(Constants.TABELLA_TIPO_PAVIMENTAZIONI);
		if(records != null){
		setupSpinnerTipiPavimentazione(view, records);
		}
		return view;
	}


	private void setupSpinnerTipiPavimentazione(View view, List<RecordTabellaSupporto> records) {
		adapterTipoPavimentazione = new ArrayAdapter<RecordTabellaSupporto>(getActivity(), R.layout.default_spinner_layout,records);
		adapterTipoPavimentazione.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinnerTipoPavimentazione = (Spinner) view.findViewById(R.id.display_area_tipoPavimentazione);
		spinnerTipoPavimentazione.setAdapter(adapterTipoPavimentazione);
		
		
		spinnerTipoPavimentazione.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				RecordTabellaSupporto r = (RecordTabellaSupporto) spinnerTipoPavimentazione.getAdapter().getItem(arg2);
				Area a = MainActivity.getCurrentArea();
				if(a!=null){
					a.tipoPavimentazione = r.codice;
					MainActivity.setCurrentArea(a);
					removeEmptyItemFromAdapterTipoPavimentazione();
//					salvaModifiche(null);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(MainActivity.getCurrentArea() != null){
			showStrutturaData(MainActivity.getCurrentArea());
		}
	}


	public void salvaModifiche(View v){
		Log.d(TAG,"salvamodifiche nel fragment");
		saveLocal(MainActivity.getCurrentArea());
	}
	
	private void updateText(int viewId, String text){
		TextView t = (TextView) getActivity().findViewById(viewId);
		if(t != null){
			t.setText(text);
		}
	}
	
	public void editMe(View v){
		Log.d(TAG,"editme nel fragment");	
		switch(v.getId()){
		case R.id.display_area_tipoPavimentazione:
			/**
			 * TODO: creare il listAdapter per popolare la lista di scelta
			 */
			break;
		default: 
			TextView t = (TextView) v;
			changeTextValueThroughAlert(t);
			break;
		}
	}

	/**
	 * @param t La textview creata nel @see editMe 
	 */
	private void changeTextValueThroughAlert(TextView t) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Modifica il dato");
		alert.setMessage("ID");

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		input.setText(t.getText());
		input.setTag(R.integer.tag_view_id, t.getId());
		
		
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				int viewId;
				try {
					viewId = Integer.parseInt(input.getTag(R.integer.tag_view_id).toString());
					updateText(viewId,value);
					Area a = MainActivity.getCurrentArea();
					switch(viewId){
					case R.id.display_gioco_nota:
						a.note = value;
						break;
					case R.id.display_gioco_gpsx:
						a.gpsx = Float.parseFloat(value);
						break;
					case R.id.display_gioco_gpsy:
						a.gpsy = Float.parseFloat(value);
						break;
					}
					saveLocal(a);
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
	public void placeMe(View v){
		Area a = MainActivity.getCurrentArea();
		a.gpsx = MainActivity.getCurrentLon();
		a.gpsy = MainActivity.getCurrentLat();
		a.hasDirtyData=true;
		a.sincronizzato = false;
		saveLocal(a);
	}
	public void showError(HashMap<String,String> map){
//		TextView errorView = (TextView) getActivity().findViewById(R.id.display_gioco_warning);
//		errorView.setText("");
//		Iterator<Entry<String,String>> i = map.entrySet().iterator();
//		while(i.hasNext()){
//			Entry<String,String> n = (Entry<String, String>) i.next();
//			errorView.append("\n"+n.getValue());
//		}
	}

	@Override
	public void onActivityResult(int requestCode, int returnCode, Intent intent) {
		switch (requestCode) {

		
		default:
			break;
		}
	}
	
	private long saveLocal(Area area){
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		long id = db.salvaStrutturaLocally(area);
		if(id > 0){
			Toast.makeText(getActivity().getApplicationContext(),"Area salvata localmente", Toast.LENGTH_SHORT).show();
			MainActivity ma = (MainActivity) getActivity();
			ma.updateCountDaSincronizzare();
		} else if (id == -2){
			//constraint error
		}
		return id;
	}
	

	public void showStrutturaData(Struttura a) {
		Area area = (Area) a;
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_gioco_id);
		v.setText(""+area.idGioco);
		v = (TextView) getActivity().findViewById(R.id.display_area_id);
		v.setText(""+area.idArea);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_marca);
		v.setText(area.descrizioneMarca);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText(area.note);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_seriale);
		v.setText(area.numeroSerie);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_rfid);
		v.setText(area.rfid+"");
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(area.rfidArea+"");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsx);
		v.setText(area.gpsx + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsy);
		v.setText(area.gpsy + "");
		v = (TextView) getActivity().findViewById(R.id.display_area_descrizione);
		v.setText(area.descrizioneArea + "");
		
		Spinner s = (Spinner) getActivity().findViewById(R.id.display_area_tipoPavimentazione);
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		RecordTabellaSupporto tipoPavimentazione = db.tabelleSupportoGetRecord(Constants.TABELLA_TIPO_PAVIMENTAZIONI, area.tipoPavimentazione);
		int position = getAdapterRecordPosition(tipoPavimentazione);
		removeEmptyItemFromAdapterTipoPavimentazione();
		s.setSelection(position);
		
		setupSnapshots(area);
	}

	/**
	 * 
	 */
	private void removeEmptyItemFromAdapterTipoPavimentazione() {
		if(adapterTipoPavimentazione.getItem(0).codice == -1){
			//Facciamo pulizia del segnaposto vuoto, così da non poterlo impostare per un oggetto esistente
			adapterTipoPavimentazione.remove(adapterTipoPavimentazione.getItem(0));
		}
	}
	
	private int getAdapterRecordPosition(RecordTabellaSupporto record){
		if(record!=null){
			int count = adapterTipoPavimentazione.getCount();
			for(int i = 0; i < count ; i++){
				RecordTabellaSupporto r = adapterTipoPavimentazione.getItem(i);
				if( r.codice == record.codice){
					return i;
				}			
			}
		}
		return -1;
	}
	

	private void setupSnapshots(Struttura gioco) {
		int width = 150;
		int height= 150;
		ImageView v;
		for (int i = 0; i < Constants.MAX_SNAPSHOTS_AMOUNT; i++) {
			switch(i){
			case 0:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_0);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto0, Base64.DEFAULT),getResources(),1,width,height));
				break;
			case 1:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_1);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto1, Base64.DEFAULT),getResources(),2,width,height));
				break;
			
			case 2:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_2);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto2, Base64.DEFAULT),getResources(),3,width,height));
				break;
				
			case 3:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_3);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto3, Base64.DEFAULT),getResources(),4,width,height));
				break;
				
			case 4:
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_4);
				v.setImageBitmap(Utils.decodeSampledBitmapFromResource(Base64.decode(gioco.foto4, Base64.DEFAULT),getResources(),5,width,height));
				break;
				
			
			}
			
		}
	}

	@Override
	public void clickedMe(View v) {
		// TODO Auto-generated method stub
		
	}
	
	

}