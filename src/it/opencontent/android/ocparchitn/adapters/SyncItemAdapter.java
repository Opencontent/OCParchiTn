package it.opencontent.android.ocparchitn.adapters;

import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Intervento;
import it.opencontent.android.ocparchitn.db.entities.Struttura;

import java.util.List;

import org.nfctools.snep.GetResponseListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SyncItemAdapter extends ArrayAdapter<Struttura> {

	private final static String TAG = SyncItemAdapter.class.getSimpleName();
	private List<Struttura> list; 
	
	public SyncItemAdapter(Context context, int textViewResourceId, List<Struttura> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		list = objects;
	}




	private Context mContext;
	
	

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if(row == null){
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		row=inflater.inflate(R.layout.sincronizzazione_list_item, parent, false);
		}
		
		Struttura s = list.get(position);
		
		Button b = (Button) row.findViewById(R.id.pulsante_lancia_singola_sincronizzazione);
		b.setTag(R.integer.controllo_button_tag,s);
		b.setTag(R.integer.controllo_button_tag_posizione, position);
		b = (Button) row.findViewById(R.id.pulsante_elimina_singola_sincronizzazione);
		b.setTag(R.integer.controllo_button_tag,s);
		b.setTag(R.integer.controllo_button_tag_posizione, position);
		b = (Button) row.findViewById(R.id.pulsante_invia_foto_singola_sincronizzazione);
		b.setTag(R.integer.controllo_button_tag,s);
		b.setTag(R.integer.controllo_button_tag_posizione, position);
		b = (Button) row.findViewById(R.id.pulsante_lancia_edit_singola_sincronizzazione);
		b.setTag(R.integer.controllo_button_tag,s);
		b.setTag(R.integer.controllo_button_tag_posizione, position);
		
		
		CheckBox cb = (CheckBox) row.findViewById(R.id.checkbox_sincronizzazione_ok);
		cb.setChecked(s.sincronizzato);
		
		TextView t;
		t = (TextView) row.findViewById(R.id.testo_tipo_singola_sincronizzazione);
		if(s.getClass().equals(Controllo.class)){
			b = (Button) row.findViewById(R.id.pulsante_lancia_edit_singola_sincronizzazione);
			b.setVisibility(View.INVISIBLE);
			t.setText(s.getClass().getSimpleName()+" id:"+((Controllo) s).idRiferimento);
			row.setBackgroundColor(mContext.getResources().getColor(R.color.syncbg_controllo));
		}else if(s.getClass().equals(Gioco.class)){
			b = (Button) row.findViewById(R.id.pulsante_lancia_edit_singola_sincronizzazione);
			b.setVisibility(View.VISIBLE);
			t.setText(s.getClass().getSimpleName()+" id:"+((Gioco) s).idGioco);
			row.setBackgroundColor(mContext.getResources().getColor(R.color.syncbg_gioco));
		} else if(s.getClass().equals(Area.class)){
			b = (Button) row.findViewById(R.id.pulsante_lancia_edit_singola_sincronizzazione);
			b.setVisibility(View.VISIBLE);
			t.setText(s.getClass().getSimpleName()+" id:"+((Area) s).idArea);
			row.setBackgroundColor(mContext.getResources().getColor(R.color.syncbg_area));
		} else if(s.getClass().equals(Intervento.class)){
			b = (Button) row.findViewById(R.id.pulsante_lancia_edit_singola_sincronizzazione);
			b.setVisibility(View.INVISIBLE);
			t.setText(s.getClass().getSimpleName()+" "+((Intervento) s).idRiferimento);
			row.setBackgroundColor(mContext.getResources().getColor(R.color.syncbg_intervento));
		}	
		

		t = (TextView) row.findViewById(R.id.testo_errore_singola_sincronizzazione);
		t.setText(s.erroreRemoto);

		t = (TextView) row.findViewById(R.id.testo_rfidgioco_singola_sincronizzazione);
		t.setText("RFID Gioco: "+s.rfid);
		t = (TextView) row.findViewById(R.id.testo_rfidarea_singola_sincronizzazione );
		t.setText("RFID Area: "+s.rfidArea);
	
		return row;		
		
	}
	
}
