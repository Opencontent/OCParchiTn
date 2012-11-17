package it.opencontent.android.ocparchitn.fragments;

import java.util.zip.Inflater;

import it.opencontent.android.ocparchitn.Intents;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.activities.BaseActivity;
import it.opencontent.android.ocparchitn.activities.CameraActivity;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.utils.DrawableOverlayWriter;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author marco
 *         getGiocoResponse{getGiocoReturn=anyType{descrizione_area=MATTARELLO -
 *         Parco Mattarello piazzale Ergolding; descrizione_gioco=3 casette;
 *         descrizione_marca=TLF; dt_acquisto=1900-01-01;
 *         dt_installazione=1999-01-01; dt_posizionamento_al=1999-01-01;
 *         dt_posizionamento_dal=1999-01-01; dt_prossimointervento=2050-12-31;
 *         gpsx=0E-9; gpsy=0E-9; id_gioco=null; id_modello=anyType{};
 *         id_tipogioco=null; note=anyType{}; numeroserie=0;
 *         posizione_rfid=anyType{}; rfid=5; rfid_area=anyType{}; }; }
 */

public class MainFragment extends Fragment {

	private static final String TAG = MainFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.activity_main, container, false);
		return view;
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
		if(MainActivity.getCurrentGioco() != null){
			showGiocoData(MainActivity.getCurrentGioco());
		}
	}



	@Override
	public void onActivityResult(int requestCode, int returnCode, Intent intent) {
		switch (requestCode) {

		
		default:
			break;
		}
	}

	public void showGiocoData(Gioco gioco) {
		TextView v;
		v = (TextView) getActivity().findViewById(R.id.display_gioco_id);
		v.setText(gioco.id_gioco + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_modello);
		v.setText(gioco.id_modello + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_tipo);
		v.setText(gioco.id_tipogioco + "");
		v = (TextView) getActivity()
				.findViewById(R.id.display_descrizione_area);
		v.setText(gioco.descrizione_area);
		v = (TextView) getActivity().findViewById(
				R.id.display_gioco_descrizione);
		v.setText(gioco.descrizione_gioco);
		v = (TextView) getActivity().findViewById(
				R.id.display_gioco_data_acquisto);
		v.setText(gioco.dt_acquisto);
		v = (TextView) getActivity().findViewById(
				R.id.display_gioco_data_installazione);
		v.setText(gioco.dt_installazione);
		v = (TextView) getActivity().findViewById(
				R.id.display_gioco_data_posizionamento);
		v.setText(gioco.dt_posizionamento);
		v = (TextView) getActivity().findViewById(
				R.id.display_gioco_data_prossimo_intervento);
		v.setText(gioco.dt_prossimointervento);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_marca);
		v.setText(gioco.marca_1);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_nota);
		v.setText(gioco.note);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_seriale);
		v.setText(gioco.numeroserie);
		v = (TextView) getActivity().findViewById(
				R.id.display_gioco_posizione_rfid);
		v.setText(gioco.posizione_rfid);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_rfid);
		v.setText(gioco.rfid);
		v = (TextView) getActivity().findViewById(R.id.display_area_rfid);
		v.setText(gioco.rfid_area);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_tipo);
		v.setText(gioco.tipo);
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsx);
		v.setText(gioco.gpsx + "");
		v = (TextView) getActivity().findViewById(R.id.display_gioco_gpsy);
		v.setText(gioco.gpsy + "");
		setupSnapshots(gioco);
	}
	
	private void setupSnapshots(Gioco gioco) {
		Bitmap bmp;
		ImageView v;
		String text;
		for (int i = 0; i < Intents.MAX_SNAPSHOTS_AMOUNT; i++) {
			text = "Foto "+(i+1);
			switch(i){
			case 0:
				if(gioco == null || gioco.foto0==null){
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(), R.drawable.snapshot_teaser, text)
							.getBitmap();
				} else {
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(),
							gioco.foto0.copy(Bitmap.Config.ARGB_8888, true), text,
							70, 50).getBitmap();
				}
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_0);
				v.setImageBitmap(bmp);
				break;
			case 1:
				if(gioco == null || gioco.foto1==null){
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(), R.drawable.snapshot_teaser, text)
							.getBitmap();
				} else {
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(),
							gioco.foto1.copy(Bitmap.Config.ARGB_8888, true), text,
							70, 50).getBitmap();
				}
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_1);
				v.setImageBitmap(bmp);
				break;
			case 2:
				if(gioco == null || gioco.foto2==null){
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(), R.drawable.snapshot_teaser, text)
							.getBitmap();
				} else {
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(),
							gioco.foto2.copy(Bitmap.Config.ARGB_8888, true), text,
							70, 50).getBitmap();
				}
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_2);
				v.setImageBitmap(bmp);
				break;
			case 3:
				if(gioco == null || gioco.foto3==null){
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(), R.drawable.snapshot_teaser, text)
							.getBitmap();
				} else {
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(),
							gioco.foto3.copy(Bitmap.Config.ARGB_8888, true), text,
							70, 50).getBitmap();
				}
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_3);
				v.setImageBitmap(bmp);
				break;
			case 4:
				if(gioco == null || gioco.foto4==null){
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(), R.drawable.snapshot_teaser, text)
							.getBitmap();
				} else {
					bmp = new DrawableOverlayWriter().writeOnDrawable(
							getResources(),
							gioco.foto4.copy(Bitmap.Config.ARGB_8888, true), text,
							70, 50).getBitmap();
				}
				v = (ImageView) getActivity().findViewById(R.id.snapshot_gioco_4);
				v.setImageBitmap(bmp);
				break;
			
			}
		}
	}

}