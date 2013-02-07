package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.utils.Utils;

import java.util.HashMap;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SpostamentoFragment extends RilevazioneGiocoFragment  implements ICustomFragment{

	private static final String TAG = RilevazioneGiocoFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_main, container, false);
		return view;
	}	
	
	@Override
	public void salvaModifiche(View v) {
		super.salvaModifiche(v);
	}

	@Override
	public void editMe(View v) {
		switch(v.getId()){
		case R.id.display_gioco_nota:
		case R.id.display_gioco_gpsx:
		case R.id.display_gioco_gpsy:
			super.actualEditing (v);
			break;
		}
	}
	public void showError(HashMap<String,String> map){
		
	}

	@Override
	public void showStrutturaData(Struttura gioco) {
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
		setupSnapshots(gioco);
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
			}
			
		}
	}
		
	@Override
	public void clickedMe(View v) {
		
	}

	private long saveLocal(Gioco gioco){
		OCParchiDB db = new OCParchiDB(getActivity().getApplicationContext());
		gioco.spostamento = 1;
		long id = db.salvaStrutturaLocally(gioco);
		if(id > 0){
			Toast.makeText(getActivity().getApplicationContext(),"Gioco salvato localmente", Toast.LENGTH_SHORT).show();
			MainActivity ma = (MainActivity) getActivity();
			ma.updateCountDaSincronizzare();
		} else if (id == -2){
			//constraint error
		}
		return id;
	}
	
}
