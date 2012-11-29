package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.Intents;
import it.opencontent.android.ocparchitn.utils.FileNameCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
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
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				bmp.compress(CompressFormat.PNG, 0, baos);
//				String out = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
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

	public Struttura(Set<Entry<String, Object>> set, int rfid,Context context) {
		Bitmap bmp = null;
		if (rfid > 0) {
			for (int i = 0; i < Intents.MAX_SNAPSHOTS_AMOUNT; i++) {
				try {
					String filename = FileNameCreator.getSnapshotFullPath(rfid, i);
					bmp = BitmapFactory.decodeStream(context.openFileInput(filename));
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
			if (e.getKey().equals("marca_1")) {
				marca_1 = bindStringToProperty(e.getValue(), e.getKey());
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

	private String bindStringToProperty(Object value, String key) {
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
					return value.toString();
					//return "INDEFINITO";
				}
			} else {
				return (String) value;
			}
		} else {
			return "INDEFINITO";
		}
	}

	private float bindFloatToProperty(Object value, String key) {
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
	public boolean hasDirtyData;
	public int ultimaSincronizzazione;

	public String dirtyElements;
	public int rfid;
	public String rfid_area;
	public int id_gioco;

	public float gpsx;
	public float gpsy;

	public String numeroserie;
	public String marca_1;

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
