package it.opencontent.android.ocparchitn.db.entities;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.util.Log;

public class Struttura {

	private static final String TAG = Struttura.class.getSimpleName();

	public Struttura() {

	}

	public Struttura(Set<Entry<String, Object>> set) {
		Iterator<Entry<String, Object>> i = set.iterator();

		while (i.hasNext()) {
			Entry<String, Object> e = i.next();
			Log.d(TAG, e.getKey());
			if (e.getKey().equals("descrizione_gioco")) {
				descrizione_gioco = bindStringToProperty(descrizione_gioco,
						e.getValue(), e.getKey());
			}
			if (e.getKey().equals("descrizione_area")) {
				descrizione_area = bindStringToProperty(descrizione_area,
						e.getValue(), e.getKey());
			}
			if (e.getKey().equals("marca_1")) {
				marca_1 = bindStringToProperty(marca_1, e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("dt_acquisto")) {
				dt_acquisto = bindStringToProperty(dt_acquisto, e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("dt_installazione")) {
				dt_installazione = bindStringToProperty(dt_installazione,
						e.getValue(), e.getKey());
			}
			if (e.getKey().equals("dt_posizionamento")) {
				dt_posizionamento = bindStringToProperty(dt_posizionamento,
						e.getValue(), e.getKey());
			}
			if (e.getKey().equals("dt_prossimointervento")) {
				dt_prossimointervento = bindStringToProperty(
						dt_prossimointervento, e.getValue(), e.getKey());
			}
			if (e.getKey().equals("gpsx") && e.getValue() != null) {
				gpsx = bindFloatToProperty(gpsx, e.getValue(), e.getKey());
			}
			if (e.getKey().equals("gpsy") && e.getValue() != null) {
				gpsy = bindFloatToProperty(gpsy, e.getValue(), e.getKey());
			}
			if (e.getKey().equals("id_gioco") && e.getValue() != null) {
				id_gioco = bindIntToProperty(id_gioco, e.getValue(), e.getKey());
			}
			if (e.getKey().equals("id_modello") && e.getValue() != null) {
				id_modello = bindIntToProperty(id_modello, e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("id_tipogioco") && e.getValue() != null) {
				id_tipogioco = bindIntToProperty(id_tipogioco, e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("note")) {
				note = bindStringToProperty(note, e.getValue(), e.getKey());
			}
			if (e.getKey().equals("numeroserie")) {
				numeroserie = bindStringToProperty(numeroserie, e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("posizione_rfid")) {
				posizione_rfid = bindStringToProperty(posizione_rfid,
						e.getValue(), e.getKey());

			}
			if (e.getKey().equals("rfid")) {
				rfid = bindStringToProperty(rfid, e.getValue(), e.getKey());
			}
			if (e.getKey().equals("rfid_area")) {
				rfid_area = bindStringToProperty(rfid_area, e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("foto0")) {
				foto0 = bindStringToProperty(foto0, e.getValue(), e.getKey());
			}

		}

	}

	private int bindIntToProperty(Object property, Object value, String key) {
		if (value != null) {
			if (value.getClass().equals(SoapObject.class)) {
				Log.d(TAG, "Arrivato un valore non gestibile, " + key);
				return 0;
			} else if (value.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive v = (SoapPrimitive) value;
				Log.d(TAG, v.getAttributeCount()
						+ " sottoelementi da sbobinare per " + key);
				if (v.getAttributeCount() > 0) {
					return Integer.parseInt(v.toString());
				} else {
					return 0;
				}
			} else {
				return Integer.parseInt((String) value);
			}
		} else {
			return 0;
		}
	}

	private String bindStringToProperty(Object property, Object value,
			String key) {
		if (value != null) {
			if (value.getClass().equals(SoapObject.class)) {
				Log.d(TAG, "Arrivato un valore non gestibile, " + key);
				return "";
			} else if (value.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive v = (SoapPrimitive) value;
				Log.d(TAG, v.getAttributeCount()
						+ " sottoelementi da sbobinare per " + key);
				if (v.getAttributeCount() > 0) {
					return v.toString();
				} else {
					return "INDEFINITO";
				}
			} else {
				return (String) value;
			}
		} else {
			return "INDEFINITO";
		}
	}

	private float bindFloatToProperty(Object property, Object value, String key) {
		if (value != null) {
			if (value.getClass().equals(SoapObject.class)) {
				Log.d(TAG, "Arrivato un valore non gestibile, " + key);
				return 0;
			} else if (value.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive v = (SoapPrimitive) value;
				Log.d(TAG, v.getAttributeCount()
						+ " sottoelementi da sbobinare per " + key);
				if (v.getAttributeCount() > 0) {
					return Float.parseFloat(v.toString());
				} else {
					return 0;
				}
			} else {
				return Float.parseFloat((String) value);
			}
		} else {
			return 0;
		}
	}

	public String tipo;

	public boolean sincronizzato;
	public int ultimaSincronizzazione;

	public String dirtyElements;
	public String descrizione_gioco;
	public String descrizione_area;
	public String marca_1;
	public String dt_acquisto;
	public String dt_installazione;
	public String dt_posizionamento;
	public String dt_prossimointervento;
	public float gpsx;
	public float gpsy;
	public int id_gioco;
	public int id_modello;
	public int id_tipogioco;
	public String note;
	public String numeroserie;
	public String posizione_rfid;
	public String rfid;
	public String rfid_area;
	public String foto0;
	public String foto1;
	public String foto2;
	public String foto3;
	public String foto4;

}
