package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.Fotografia;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class Struttura {

	private static final String TAG = Struttura.class.getSimpleName();

	public Struttura() {

	}

	public void addImmagine(Set<Entry<String, Object>> set) {
		Iterator<Entry<String, Object>> i = set.iterator();
		Bitmap bmp = null;
		String raw = "";
		int index = 0;
		while (i.hasNext()) {
			Entry<String, Object> e = i.next();
			Log.d(TAG, e.getKey());
			
			if (e.getValue().getClass().equals(Fotografia.class)) {
				Fotografia f = (Fotografia) e.getValue();
				raw = f.immagine;
				try{
					String indexes = (String) f.nomeImmagine.subSequence(0, f.nomeImmagine.indexOf("."));
					
					String[] indexes2 = indexes.split("_");
					
					index = Integer.parseInt(indexes2[3]);
				} catch (Exception ex){
					ex.printStackTrace();
				}
				// Log.d(TAG, raw);

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

			if (!raw.equals("")) {
				switch (index) {
				case 0:
					foto0 = raw;
					break;
				case 1:
					foto1 = raw;
					break;
				case 2:
					foto2 = raw;
					break;
				case 3:
					foto3 = raw;
					break;
				case 4:
					foto4 = raw;
					break;
				}

			}
			
		}

	}

	public Struttura(Set<Entry<String, Object>> set, Context context) {
		this(set, -1, context);
	}

	/*
	 * public void recoverLocalImages(Context context){ Bitmap bmp = null; for
	 * (int i = 0; i < Constants.MAX_SNAPSHOTS_AMOUNT; i++) { try { String
	 * filename = FileNameCreator.getSnapshotFullPath(rfid, i); bmp =
	 * BitmapFactory.decodeStream(context .openFileInput(filename));
	 * Log.d(TAG,filename+" Aperto correttamente"); switch (i) { case 0: foto0 =
	 * bmp; break; case 1: foto1 = bmp; break; case 2: foto2 = bmp; break; case
	 * 3: foto3 = bmp; break; case 4: foto4 = bmp; break; } } catch
	 * (FileNotFoundException e) { // TODO Auto-generated catch block bmp =
	 * null; e.printStackTrace(); } } }
	 */

	public Struttura(Set<Entry<String, Object>> set, int rfid_argomento,
			Context context) {

		Iterator<Entry<String, Object>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			Log.d(TAG, e.getKey());
			if (e.getKey().equals("descrizione_marca")) {
				descrizione_marca = bindStringToProperty(e.getValue(),
						e.getKey());
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
			if (e.getKey().equals("foto0") && e.getValue() != null) {
				foto0 = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("foto1") && e.getValue() != null) {
				foto1 = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("foto2") && e.getValue() != null) {
				foto2 = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("foto3") && e.getValue() != null) {
				foto3 = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("foto4") && e.getValue() != null) {
				foto4 = bindStringToProperty(e.getValue(), e.getKey());
			}

		}

		/*
		 * if (rfid > 0 && context != null) { recoverLocalImages(context); }
		 */

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

	public static String tipo;

	public boolean sincronizzato = false;
	public boolean hasDirtyData = false;
	public int ultimaSincronizzazione = 0;

	public String dirtyElements = "0";
	public int rfid = 0;
	public String rfid_area = "0";
	public int id_gioco = 0;

	public float gpsx = 0.0f;
	public float gpsy = 0.0f;

	public String numeroserie = "0";
	public String descrizione_marca = "";

	public String note = "";
	public String foto0 = "";
	public String foto1 = "";
	public String foto2 = "";
	public String foto3 = "";
	public String foto4 = "";

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
