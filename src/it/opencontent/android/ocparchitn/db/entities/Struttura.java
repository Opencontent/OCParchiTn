package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.utils.FileNameCreator;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class Struttura {

	private static final String TAG = Struttura.class.getSimpleName();

	public Struttura() {

	}

	public void addImmagine(int index, Set<Entry<String, Object>> set) {
		Iterator<Entry<String, Object>> i = set.iterator();
		Bitmap bmp = null;
		while (i.hasNext()) {
			Entry<String, Object> e = i.next();
			Log.d(TAG, e.getKey());
			if (e.getKey().equals("immagine")) {
				String raw = bindStringToProperty(e.getValue(), e.getKey());
				Log.d(TAG, raw);
				byte[] decoded = Base64.decode(raw, Base64.DEFAULT);

				bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// bmp.compress(CompressFormat.PNG, 0, baos);
				// String out = Base64.encodeToString(baos.toByteArray(),
				// Base64.DEFAULT);
			} else {
				Log.d(TAG,
						e.getKey()
								+ " "
								+ bindStringToProperty(e.getValue(), e.getKey()));
			}
		}
		if (bmp != null) {
			switch (index) {
			case 0:
				foto0 = bmp;
				break;
			}
		}

	}

	public Struttura(Set<Entry<String, Object>> set, Context context) {
		this(set, -1, context);
	}

	public Struttura(Set<Entry<String, Object>> set, int rfid, Context context) {
		Bitmap bmp = null;
		if (rfid > 0 && context != null) {
			for (int i = 0; i < Constants.MAX_SNAPSHOTS_AMOUNT; i++) {
				try {
					String filename = FileNameCreator.getSnapshotFullPath(rfid,
							i);
					bmp = BitmapFactory.decodeStream(context
							.openFileInput(filename));
					switch (i) {
					case 0:
						foto0 = bmp;
						break;
					case 1:
						foto1 = bmp;
						break;
					case 2:
						foto2 = bmp;
						break;
					case 3:
						foto3 = bmp;
						break;
					case 4:
						foto4 = bmp;
						break;
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					bmp = null;
					e.printStackTrace();
				}
			}
		}

		Iterator<Entry<String, Object>> i = set.iterator();

		while (i.hasNext()) {
			Entry<String, Object> e = i.next();
			Log.d(TAG, e.getKey());
			if (e.getKey().equals("descrizione_marca")) {
				descrizione_marca = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("gpsx") && e.getValue() != null) {
				gpsx = bindFloatToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("gpsy") && e.getValue() != null) {
				gpsy = bindFloatToProperty(e.getValue(), e.getKey());
			}

			if (e.getKey().equals("note")) {
				note = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("numeroserie")) {
				numeroserie = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("rfid")) {
				rfid = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("rfid_area")) {
				rfid_area = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("id_gioco") && e.getValue() != null) {
				id_gioco = bindIntToProperty(e.getValue(), e.getKey());
			}

			// Campi inutilizzati per il momento

			// if (e.getKey().equals("descrizione_gioco")) {
			// descrizione_gioco = bindStringToProperty(
			// e.getValue(), e.getKey());
			// }
			// if (e.getKey().equals("descrizione_area")) {
			// descrizione_area = bindStringToProperty(
			// e.getValue(), e.getKey());
			// }
			// if (e.getKey().equals("dt_acquisto")) {
			// dt_acquisto = bindStringToProperty( e.getValue(),
			// e.getKey());
			// }
			// if (e.getKey().equals("dt_installazione")) {
			// dt_installazione = bindStringToProperty(
			// e.getValue(), e.getKey());
			// }
			// if (e.getKey().equals("dt_posizionamento")) {
			// dt_posizionamento = bindStringToProperty(
			// e.getValue(), e.getKey());
			// }
			// if (e.getKey().equals("dt_prossimointervento")) {
			// dt_prossimointervento = bindStringToProperty(
			// e.getValue(), e.getKey());
			// }
			// if (e.getKey().equals("id_modello") && e.getValue() != null) {
			// id_modello = bindIntToProperty( e.getValue(),
			// e.getKey());
			// }
			// if (e.getKey().equals("id_tipogioco") && e.getValue() != null) {
			// id_tipogioco = bindIntToProperty( e.getValue(),
			// e.getKey());
			// }
			// if (e.getKey().equals("posizione_rfid")) {
			// posizione_rfid = bindStringToProperty(
			// e.getValue(), e.getKey());
			//
			// }

		}

	}

	private int bindIntToProperty(Object value, String key) {
		int res = 0;
		if (value.getClass().equals(Integer.class)) {
			res = ((Integer) value).intValue();
		} else {

			try {
				res = Integer.parseInt((String) value);

			} catch (Exception e) {
				Log.e(TAG, "integer not parsed");
				if (value.getClass().equals(SoapObject.class)) {
					Log.d(TAG, "Arrivato un valore non gestibile, " + key);
				} else if (value.getClass().equals(SoapPrimitive.class)) {
					SoapPrimitive v = (SoapPrimitive) value;
					Log.d(TAG, v.getAttributeCount()
							+ " sottoelementi da sbobinare per " + key);
					res = Integer.parseInt(v.toString());
				}
			}
		}
		return res;
	}

	private String bindStringToProperty(Object value, String key) {
		String res = "";
		if (value != null && value.getClass().equals(SoapObject.class)) {
			Log.d(TAG, "Arrivato un valore non gestibile, " + key);
		} else if (value != null
				&& value.getClass().equals(SoapPrimitive.class)) {
			SoapPrimitive v = (SoapPrimitive) value;
			Log.d(TAG, v.getAttributeCount()
					+ " sottoelementi da sbobinare per " + key);
			if (v.getAttributeCount() > 0) {
				res = v.toString();
			} else {
				res = value.toString();
				// return "INDEFINITO";
			}
		} else {
			res = (String) value;
		}
		return res;
	}

	private float bindFloatToProperty(Object value, String key) {
		float res = 0;
		if (value.getClass().equals(Integer.class)) {
			res = ((Integer) value).floatValue();
		} else {
			try {
				res = Float.parseFloat((String) value);

			} catch (Exception e) {
				Log.e(TAG, "float not parsed");
				if (value.getClass().equals(SoapObject.class)) {
					Log.d(TAG, "Arrivato un valore non gestibile, " + key);
				} else if (value.getClass().equals(SoapPrimitive.class)) {
					SoapPrimitive v = (SoapPrimitive) value;
					Log.d(TAG, v.getAttributeCount()
							+ " sottoelementi da sbobinare per " + key);
					res = Float.parseFloat(v.toString());
				}
			}
		}
		return res;
	}

	public String tipo;

	public boolean sincronizzato;
	public boolean hasDirtyData;
	public int ultimaSincronizzazione;

	public String dirtyElements;
	public int rfid;
	public String rfid_area;
	public int id_gioco;

	public float gpsx;
	public float gpsy;

	public String numeroserie;
	public String descrizione_marca;

	public String note;
	public Bitmap foto0;
	public Bitmap foto1;
	public Bitmap foto2;
	public Bitmap foto3;
	public Bitmap foto4;

	// public String descrizione_gioco;
	// public String descrizione_area;
	// public String dt_acquisto;
	// public String dt_installazione;
	// public String dt_posizionamento;
	// public String dt_prossimointervento;
	// public int id_modello;
	// public int id_tipogioco;
	// public String posizione_rfid;

}
