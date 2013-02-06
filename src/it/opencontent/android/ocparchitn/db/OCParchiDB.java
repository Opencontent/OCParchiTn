package it.opencontent.android.ocparchitn.db;

import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Intervento;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.db.entities.StruttureEnum;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class OCParchiDB {

	private final static String TAG = OCParchiDB.class.getSimpleName();
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "parchitn";
	private final OCParchiOpenHelper mDatabaseOpenHelper;
	@SuppressWarnings("unused")
	private Context context;

	private static final HashMap<String, Struttura> mSchemaMap = buildSchemaMap();

	public  void close(){
		mDatabaseOpenHelper.close();
	} 
	
	public OCParchiDB(Context context) {
		mDatabaseOpenHelper = new OCParchiOpenHelper(context);
		this.context = context;
//		 mDatabaseOpenHelper.getWritableDatabase();
//		 context.deleteDatabase(DATABASE_NAME);
	}

	private static HashMap<String, Struttura> buildSchemaMap() {
		HashMap<String, Struttura> map = new HashMap<String, Struttura>();

		StruttureEnum[] strutture = StruttureEnum.class.getEnumConstants();
		for (StruttureEnum struttura : strutture) {
			map.put(struttura.tipo, struttura.istanza);
		}

		return map;
	}

	private HashMap<String, String> buildColumnMap(String tableName) {
		HashMap<String, String> map = new HashMap<String, String>();
		Field[] fields;
		if(tableName.equals(Controllo.class.getSimpleName())){
			fields = Controllo.class.getFields();			
		} else {
			Struttura table = mSchemaMap.get(tableName);
			fields = table.getClass().getFields();
		}
		for (int i = 0; i < fields.length; i++) {
			map.put(fields[i].getName(), "rowid AS " + fields[i].getName());
		}
		return map;
	}


	private Cursor query(String table, String selection,
			String[] selectionArgs, String[] columns, String ascending) {


		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(table);
		builder.setProjectionMap(buildColumnMap(table));

		Cursor cursor = builder.query(
				mDatabaseOpenHelper.getReadableDatabase(), columns, selection,
				selectionArgs, null, null, ascending);
		Log.d(TAG,SQLiteQueryBuilder.buildQueryString(false, table, columns, selection, null, null, null, null));
		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	private String[] getDefaultColumns(Class<?> classe) {
		Field[] campi = classe.getFields();
		String res = "";
		for (int i = 0; i < campi.length; i++) {
			if (i > 0) {
				res += "  , ";
			}
			String name = campi[i].getName();
			res += name + " as " + name;
		}
		return new String[] { res };
	}
	private String[] getDefaultColumnsWithoutFoto(Class<?> classe) {
		Field[] campi = classe.getFields();
		String res = "";
		for (int i = 0; i < campi.length; i++) {
			String name = campi[i].getName();
			if(!name.contains("foto")){
				if (i > 0) {
					res += "  , ";
				}
				res += name + " as " + name;
			}
		}
		return new String[] { res };
	}

	public Gioco readGiocoLocallyByRFID(int rfid) {
		String selection = " rfid  = ? ";
		String[] selectionArgs = new String[] { rfid + "" };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				StruttureEnum.GIOCHI.tipo, getDefaultColumns(Gioco.class), selection,
				selectionArgs, null, null, null);
		try {
			return deserializeGiocoSingolo(c);
		} catch (InvalidParameterException ipe) {
			ipe.printStackTrace();
			return null;
		}
	}

	public Gioco readGiocoLocallyByID(int id) {
		String selection = " idGioco  = ? ";

		String[] selectionArgs = new String[] { id + "" };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				StruttureEnum.GIOCHI.tipo, getDefaultColumns(Gioco.class), selection,
				selectionArgs, null, null, null);
		try {
			return deserializeGiocoSingolo(c);
		} catch (InvalidParameterException ipe) {
			ipe.printStackTrace();
			return null;
		}
	}
	public Area readAreaLocallyByID(int id) {
		String selection = " idArea  = ? ";
		
		String[] selectionArgs = new String[] { id + "" };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				StruttureEnum.AREE.tipo, getDefaultColumns(Area.class), selection,
				selectionArgs, null, null, null);
		try {
			return deserializeAreaSingola(c);
		} catch (InvalidParameterException ipe) {
			ipe.printStackTrace();
			return null;
		}
	}
	public Controllo readControlloLocallyByID(String id) {
		String selection = " idRiferimento  = ? ";
		
		String[] selectionArgs = new String[] { id };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				StruttureEnum.CONTROLLO.tipo, getDefaultColumns(Controllo.class), selection,
				selectionArgs, null, null, null);
		try {
			return deserializeControlloSingolo(c);
		} catch (InvalidParameterException ipe) {
			ipe.printStackTrace();
			return null;
		}
	}
	public Intervento readInterventoLocallyByID(String id) {
		String selection = " idRiferimento  = ? ";
		
		String[] selectionArgs = new String[] { id };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				StruttureEnum.INTERVENTO.tipo, getDefaultColumns(Intervento.class), selection,
				selectionArgs, null, null, null);
		try {
			return deserializeInterventoSingolo(c);
		} catch (InvalidParameterException ipe) {
			ipe.printStackTrace();
			return null;
		}
	}
	
	public void tabelleSupportoUpdate(RecordTabellaSupporto[] records){
		mDatabaseOpenHelper.getWritableDatabase().beginTransaction();
		for(RecordTabellaSupporto r : records){
			ContentValues cv  = new ContentValues();
			cv.put("numeroTabella", r.numeroTabella);
			cv.put("codice", r.codice);
			cv.put("descrizione", r.descrizione);
			cv.put("validita", r.validita);
			cv.put("tipo", r.tipo);
			mDatabaseOpenHelper.getWritableDatabase().insertWithOnConflict(
					RecordTabellaSupporto.class.getSimpleName(), null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		}
		mDatabaseOpenHelper.getWritableDatabase().setTransactionSuccessful();
		mDatabaseOpenHelper.getWritableDatabase().endTransaction();
	}
	public List<RecordTabellaSupporto> tabelleSupportoGetAllRecords(int tabella){
		String selection = " numeroTabella  = ?  ";
		String[] selectionArgs = new String[] { tabella+""  };
		String orderBy = " codice ASC ";
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				RecordTabellaSupporto.class.getSimpleName(), getDefaultColumns(RecordTabellaSupporto.class), selection,
				selectionArgs, null, null, orderBy);
		if(c.moveToFirst()){
			List<RecordTabellaSupporto> output = new ArrayList<RecordTabellaSupporto>();
			do{		
				RecordTabellaSupporto r = new RecordTabellaSupporto();
				r.tipo = "";
				r.validita = c.getLong(c.getColumnIndex("validita"));
				r.codice = c.getInt(c.getColumnIndex("codice"));
				r.numeroTabella = tabella;
				r.descrizione = c.getString(c.getColumnIndex("descrizione"));
				output.add(r);
			}while(c.moveToNext());
			c.close();
			return output;
		}else {
			c.close();
			return null;
		}
	}
	public RecordTabellaSupporto tabelleSupportoGetRecord(int tabella, int codice){
		String selection = " numeroTabella  = ? AND codice = ? ";
		RecordTabellaSupporto r=null;
		String[] selectionArgs = new String[] { tabella+"" , codice+"" };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				RecordTabellaSupporto.class.getSimpleName(), getDefaultColumns(RecordTabellaSupporto.class), selection,
				selectionArgs, null, null, null);
		if(c.moveToFirst()){
			r = new RecordTabellaSupporto();
			r.tipo = "";
			r.validita = c.getLong(c.getColumnIndex("validita"));
			r.codice = c.getInt(c.getColumnIndex("codice"));
			r.numeroTabella = tabella;
			r.descrizione = c.getString(c.getColumnIndex("descrizione"));
		}
		c.close();
		return r;
	}
	
	public boolean tabelleSupportoScadute(){
		String timestamp = new Date().getTime() + "";
		String selection = " validita  < ? ";
		String[] selectionArgs = new String[] {  timestamp };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				RecordTabellaSupporto.class.getSimpleName(), getDefaultColumns(RecordTabellaSupporto.class), selection,
				selectionArgs, null, null, null);
		boolean result = c.getCount() > 0;
		c.close();
		return result;
	}
	public boolean tabelleSupportoPopolate(){
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query (
				RecordTabellaSupporto.class.getSimpleName(), getDefaultColumns(RecordTabellaSupporto.class), null,
				null, null, null, null);
		boolean result = c.getCount() >0;
		c.close();
		return result;
	}

	private Gioco deserializeGiocoSingolo(Cursor c)
			throws InvalidParameterException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Gioco g = null;
		if (c.getCount() > 1) {
			c.close();
			throw new InvalidParameterException(
					"the deserializeGiocoSingolo must be used only with a 1row resultset");
		} else {
			if (c != null && c.moveToFirst()) {
				do {
					int cc = c.getColumnCount();
					for (int i = 0; i < cc; i++) {
						int type = c.getType(i);
						switch (type) {
						case Cursor.FIELD_TYPE_INTEGER:
							data.put(c.getColumnName(i), c.getInt(i));
							break;
						default:
							data.put(c.getColumnName(i), c.getString(i));
							break;

						}

					}
				} while (c.moveToNext());
			}
			c.close();
			if (data.size() > 0) {
				g = new Gioco(data.entrySet(), null);
			}
			return g;
		}
	}
	
	
	public String recuperaFoto(Struttura s, int posizione){
		String selection;
		String[] selectionArgs;
		String tipo = "";
		if(s.getClass().equals(Controllo.class)){
			selection = " idRiferimento LIKE ? ";
			selectionArgs = new String[] { ((Controllo) s).idRiferimento+"" };
			tipo=StruttureEnum.CONTROLLO.tipo;
		}else if(s.getClass().equals(Gioco.class)){
			selection = " rfid  = ? ";
			selectionArgs = new String[] { s.rfid+"" };
			tipo=StruttureEnum.GIOCHI.tipo;
		} else if(s.getClass().equals(Area.class)){
			selection = " rfidArea  = ? ";
			selectionArgs = new String[] { s.rfidArea+"" };
			tipo=StruttureEnum.AREE.tipo;
		} else {
			selection = " ";
			selectionArgs = new String[] { "" };
			tipo=StruttureEnum.AREE.tipo;			
		}
		String[] columns = new String[]{"foto"+posizione+" as foto"+posizione};
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				tipo, columns, selection,
				selectionArgs, null, null, null);
		
		if(c.moveToFirst()){
			String base64 = c.getString(c.getColumnIndex("foto"+posizione));
			c.close();
			return base64;
		}else {
			return null;
		}
	}
	
	
	private Area deserializeAreaSingola(Cursor c)
			throws InvalidParameterException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Area a = null;
		if (c.getCount() > 1) {
			c.close();
			throw new InvalidParameterException(
					"the deserializeAreaSingola must be used only with a 1row resultset");
		} else {
			
			if (c != null && c.moveToFirst()) {
				do {
					int cc = c.getColumnCount();
					for (int i = 0; i < cc; i++) {
						int type = c.getType(i);
						switch (type) {
						case Cursor.FIELD_TYPE_INTEGER:
							data.put(c.getColumnName(i), c.getInt(i));
							break;
						default:
							data.put(c.getColumnName(i), c.getString(i));
							break;
							
						}
						
					}
				} while (c.moveToNext());
			}
			c.close();
			if (data.size() > 0) {
				a = new Area(data.entrySet(), null);
			}
			return a;
		}
	}

	private Controllo deserializeControlloSingolo(Cursor c)
			throws InvalidParameterException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Controllo controllo = null;
		if (c.getCount() > 1) {
			c.close();
			throw new InvalidParameterException(
					"the deserializeControlloSingolo must be used only with a 1row resultset.");
		} else {
			
			if (c != null && c.moveToFirst()) {
				do {
					int cc = c.getColumnCount();
					for (int i = 0; i < cc; i++) {
						int type = c.getType(i);
						switch (type) {
						case Cursor.FIELD_TYPE_INTEGER:
							data.put(c.getColumnName(i), c.getInt(i));
							break;
						default:
							data.put(c.getColumnName(i), c.getString(i));
							break;
							
						}
						
					}
				} while (c.moveToNext());
			}
			c.close();
			if (data.size() > 0) {
				controllo = new Controllo(data.entrySet(), null);
			}
			return controllo;
		}
	}

	private Intervento deserializeInterventoSingolo(Cursor c)
			throws InvalidParameterException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Intervento intervento = null;
		if (c.getCount() > 1) {
			c.close();
			throw new InvalidParameterException(
					"the deserializeInterventoSingolo must be used only with a 1row resultset.");
		} else {
			
			if (c != null && c.moveToFirst()) {
				do {
					int cc = c.getColumnCount();
					for (int i = 0; i < cc; i++) {
						int type = c.getType(i);
						switch (type) {
						case Cursor.FIELD_TYPE_INTEGER:
							data.put(c.getColumnName(i), c.getInt(i));
							break;
						default:
							data.put(c.getColumnName(i), c.getString(i));
							break;
							
						}
						
					}
				} while (c.moveToNext());
			}
			c.close();
			if (data.size() > 0) {
				intervento = new Intervento(data.entrySet(), null);
			}
			return intervento;
		}
	}

	public long insertScannedGioco(int rfid) {
		ContentValues cv = new ContentValues();
		cv.put("rfid", rfid);

		return mDatabaseOpenHelper.getWritableDatabase().insert(
				StruttureEnum.GIOCHI.tipo, null, cv);
	}
	/*
	public void addFotoToGioco(int rfid, int whichOne) {
		ContentValues cv = new ContentValues();
		cv.put("foto" + whichOne,
				FileNameCreator.getSnapshotFullPath(rfid, whichOne));
		cv.put("sincronizzato", false);

		String whereClause = " rfid = ? ";

		String[] whereArgs = new String[] { rfid + "" };
		mDatabaseOpenHelper.getWritableDatabase().update(
				StruttureEnum.GIOCHI.tipo, cv, whereClause, whereArgs);
	}*/

	public long salvaStrutturaLocally(Struttura struttura) {
		ContentValues cv = new ContentValues();
		String tabella = "";
		cv.put("gpsx", struttura.gpsx);
		cv.put("gpsy", struttura.gpsy);
		cv.put("note", struttura.note);
		cv.put("foto0", struttura.foto0);
		cv.put("foto1", struttura.foto1);
		cv.put("numeroserie", struttura.numeroSerie);
		if(struttura.getClass().equals(Gioco.class)){
			tabella = StruttureEnum.GIOCHI.tipo;
			Gioco g = (Gioco) struttura;
			cv.put("sincronizzato", false);
			cv.put("rfid", g.rfid);
			cv.put("descrizioneMarca", g.descrizioneMarca);
			cv.put("idGioco", g.idGioco);
			cv.put("posizioneRfid", g.posizioneRfid);
			cv.put("rfidArea", g.rfidArea);
			cv.put("spostamento", g.spostamento);
		} else if(struttura.getClass().equals(Area.class)){
			tabella = StruttureEnum.AREE.tipo;
			Area a = (Area) struttura;
			cv.put("sincronizzato", false);
			cv.put("rfidArea", a.rfidArea);
			cv.put("rfid", a.rfidArea);
			cv.put("descrizioneMarca", a.descrizioneMarca);
			cv.put("idArea", a.idArea);
			cv.put("idParco", a.idParco);
			cv.put("idGioco", a.idGioco);
			cv.put("spessore", a.spessore);
			cv.put("superficie", a.superficie);
			cv.put("tipoPavimentazione", a.tipoPavimentazione);
			cv.put("descrizioneArea", a.descrizioneArea);
			cv.put("posizioneRfid", a.posizioneRfid);
		}
		long id = -1;
		try {
			id = mDatabaseOpenHelper.getWritableDatabase().insertWithOnConflict(
					tabella, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		} catch (SQLiteConstraintException e) {
			Log.e(TAG, e.getMessage());
			id = -2;
		}
		
		return id;
	}
	
	public long salvaControlloLocally(Controllo c){
		ContentValues cv = new ContentValues();
		String tabella = Controllo.class.getSimpleName();
		
		cv.put("controllo",c.controllo);
		cv.put("dtScadenzaControllo",c.dtScadenzaControllo);
		cv.put("noteControllo",c.noteControllo);
		cv.put("idRiferimento",c.idRiferimento);
		cv.put("rfid",c.rfid);
		cv.put("rfidArea",c.rfidArea);
		cv.put("tipoControllo",c.tipoControllo);
		cv.put("tipoEsito",c.tipoEsito);
		cv.put("tipoSegnalazione",c.tipoSegnalazione);
		cv.put("foto0",c.foto0);
		cv.put("foto1",c.foto1);
		cv.put("sincronizzato",false);
		long id = -1;
		try {
			id = mDatabaseOpenHelper.getWritableDatabase().insertWithOnConflict(
					tabella, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		} catch (SQLiteConstraintException e) {
			Log.e(TAG, e.getMessage());
			id = -2;
		}
		
		return id;		
	}
	public long salvaInterventoLocally(Intervento i){
		ContentValues cv = new ContentValues();
		String tabella = Intervento.class.getSimpleName();
		
		
		cv.put("codEsito",i.codEsito);
		cv.put("codTipologia",i.codTipologia);
		cv.put("descEsito",i.descEsito);
		cv.put("descTipologia",i.descTipologia);
		cv.put("dtFineItervento",i.dtFineItervento);
		cv.put("dtInizioItervento",i.dtInizioItervento);
		cv.put("idGioco",i.idGioco);
		cv.put("idIntervento",i.idIntervento);
		cv.put("idRiferimento",i.idRiferimento);
		cv.put("intervento",i.intervento);
		cv.put("noteEsecuzione",i.noteEsecuzione);
		cv.put("noteRichiesta",i.noteRichiesta);
		cv.put("oraFineItervento",i.oraFineItervento);
		cv.put("oraInizioItervento",i.oraInizioItervento);
		cv.put("rfid",i.rfid);
		cv.put("stato",i.stato);
		cv.put("tipoIntervento",i.tipoIntervento);
		
		cv.put("foto0",i.foto0);
		cv.put("foto1",i.foto1);
		cv.put("sincronizzato",false);
		

		long id = -1;
		try {
			id = mDatabaseOpenHelper.getWritableDatabase().insertWithOnConflict(
					tabella, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		} catch (SQLiteConstraintException e) {
			Log.e(TAG, e.getMessage());
			id = -2;
		}
		
		return id;		
	}
	
	public void eliminaCopiaLocaleDiStrutturaSincronizzata(Struttura s){

			String selection = "";
			String[] selectionArgs = null;
			String tipo = "";
			if(s.getClass().equals(Intervento.class)){
				selection = " idRiferimento LIKE ? ";
				selectionArgs = new String[] { ((Intervento) s).idRiferimento+"" };
				tipo=StruttureEnum.INTERVENTO.tipo;
			}else if(s.getClass().equals(Controllo.class)){
				selection = " idRiferimento LIKE ? ";
				selectionArgs = new String[] { ((Controllo) s).idRiferimento+"" };
				tipo=StruttureEnum.CONTROLLO.tipo;
			}else if(s.getClass().equals(Gioco.class)){
				selection = " rfid  = ? ";
				selectionArgs = new String[] { s.rfid+"" };
				tipo=StruttureEnum.GIOCHI.tipo;
			} else if(s.getClass().equals(Area.class)){
				selection = " rfidArea  = ? ";
				selectionArgs = new String[] { s.rfidArea+"" };
				tipo=StruttureEnum.AREE.tipo;
			} 
		
		try{
			int res = mDatabaseOpenHelper.getWritableDatabase().delete(tipo, selection, selectionArgs); 
			Log.d(TAG,"Aggiornate "+res+" righe");
		}catch(SQLiteConstraintException e){
			Log.e(TAG, e.getMessage());
		}
	}	

	public int getPendingSynchronizations() {
		Iterator<Entry<String, Struttura>> strutture = mSchemaMap.entrySet()
				.iterator();

		String[] selectionArgs = new String[] { "0" };
		int result = 0;
		Cursor c = null;
		while (strutture.hasNext()) {

			Entry<String, Struttura> entry = strutture.next();
			String[] columns = new String[] { " sincronizzato as sincronizzato " };
			String tableName = entry.getKey();
			String selection = " sincronizzato = ?  "; 
			if(entry.getValue().getClass().equals(Controllo.class)){
				selection = " sincronizzato = ? and idRiferimento > 0 ";
			}else if(entry.getValue().getClass().equals(Gioco.class)){
				selection = " sincronizzato = ? and rfid > 0 and rfidArea > 0";
			}else if(entry.getValue().getClass().equals(Area.class)){
				selection = " sincronizzato = ? and rfidArea > 0 ";
			}else if(entry.getValue().getClass().equals(Intervento.class)){
				selection = " sincronizzato = ? and idIntervento > 0 ";
			}

			c = query(tableName, selection, selectionArgs, columns, null);
			if (c != null && c.moveToFirst()) {
				result += c.getCount();
			}
		}
		c = null;

		return result;
	}
	


	public LinkedHashMap<String,Object> getStruttureDaSincronizzare() {
		LinkedHashMap<String, Object> res = new LinkedHashMap<String, Object>();

		Iterator<Entry<String, Struttura>> strutture = mSchemaMap.entrySet()
				.iterator();

		String[] selectionArgs = new String[] { "0" };

		while (strutture.hasNext()) {

			Entry<String, Struttura> entry = strutture.next();

			String[] columns = getDefaultColumnsWithoutFoto(entry.getValue().getClass());

			String tableName = entry.getKey();
			String selection = " sincronizzato = ?  "; 
			if(entry.getValue().getClass().equals(Controllo.class)){
				selection = " sincronizzato = ? and idRiferimento > 0 ";
			}else if(entry.getValue().getClass().equals(Gioco.class)){
				selection = " sincronizzato = ? and rfid > 0 and rfidArea > 0";
			}else if(entry.getValue().getClass().equals(Area.class)){
				selection = " sincronizzato = ? and rfidArea > 0 ";
			}else if(entry.getValue().getClass().equals(Intervento.class)){
				selection = " sincronizzato = ? and idIntervento > 0 ";
			}
			

			Cursor c = query(tableName, selection, selectionArgs, columns, null);
			if (c != null && c.moveToFirst()) {
				do {
					Struttura s;
					if (tableName.equals(StruttureEnum.GIOCHI.tipo)) {
						s = new Gioco();
						((Gioco) s).posizioneRfid = c.getString(c.getColumnIndex("posizioneRfid"));
						((Gioco) s).spostamento = c.getInt(c.getColumnIndex("spostamento"));
						
					} else if(tableName.equals(StruttureEnum.AREE.tipo)) {
						s = new Area();
						((Area) s).spessore = c.getString(c.getColumnIndex("spessore"));
						((Area) s).superficie = c.getString(c.getColumnIndex("superficie"));
						((Area) s).tipoPavimentazione = c.getInt(c.getColumnIndex("tipoPavimentazione"));
						((Area) s).idArea = c.getInt(c.getColumnIndex("idArea"));	
						((Area) s).rfidArea = c.getInt(c.getColumnIndex("rfidArea"));	
						((Area) s).posizioneRfid = c.getString(c.getColumnIndex("posizioneRfid"));
					} else if(tableName.equals(StruttureEnum.CONTROLLO.tipo)){
						s = new Controllo();						
						((Controllo) s).controllo = c.getInt(c.getColumnIndex("controllo"));
						((Controllo) s).dtScadenzaControllo = c.getString(c.getColumnIndex("dtScadenzaControllo")); 
						((Controllo) s).idRiferimento = c.getString(c.getColumnIndex("idRiferimento"));
						((Controllo) s).idGioco = Integer.parseInt(c.getString(c.getColumnIndex("idRiferimento")));
						((Controllo) s).noteControllo = c.getString(c.getColumnIndex("noteControllo"));
						((Controllo) s).rfid = c.getInt(c.getColumnIndex("rfid"));
						((Controllo) s).tipoControllo = c.getInt(c.getColumnIndex("tipoControllo"));
						((Controllo) s).tipoEsito = c.getInt(c.getColumnIndex("tipoEsito"));
						((Controllo) s).tipoSegnalazione = c.getInt(c.getColumnIndex("tipoSegnalazione"));
					}else if(tableName.equals(StruttureEnum.INTERVENTO.tipo)){
						s = new Intervento();
						((Intervento) s).codEsito = c.getInt(c.getColumnIndex("codEsito"));
						((Intervento) s).codTipologia = c.getInt(c.getColumnIndex("codTipologia"));
						((Intervento) s).descEsito = c.getString(c.getColumnIndex("descEsito"));
						((Intervento) s).descTipologia = c.getString(c.getColumnIndex("descTipologia"));
						((Intervento) s).dtFineItervento = c.getString(c.getColumnIndex("dtFineItervento"));
						((Intervento) s).dtInizioItervento = c.getString(c.getColumnIndex("dtInizioItervento"));
						((Intervento) s).idGioco = c.getInt(c.getColumnIndex("idRiferimento"));
						((Intervento) s).idIntervento = c.getInt(c.getColumnIndex("idIntervento"));
						((Intervento) s).idRiferimento = c.getString(c.getColumnIndex("idRiferimento"));
						((Intervento) s).intervento = c.getInt(c.getColumnIndex("intervento"));
						((Intervento) s).noteEsecuzione = c.getString(c.getColumnIndex("noteEsecuzione"));
						((Intervento) s).noteRichiesta = c.getString(c.getColumnIndex("noteRichiesta"));
						((Intervento) s).oraFineItervento = c.getString(c.getColumnIndex("oraFineItervento"));
						((Intervento) s).oraInizioItervento = c.getString(c.getColumnIndex("oraInizioItervento"));
						((Intervento) s).stato = c.getInt(c.getColumnIndex("stato"));
						((Intervento) s).tipoIntervento = c.getInt(c.getColumnIndex("tipoIntervento"));
					}else{
						s = new Struttura();
					}
					
					s.descrizioneArea = c.getString(c.getColumnIndex("descrizioneArea"));
					s.gpsx = c.getString(c
							.getColumnIndex("gpsx"));
					s.gpsy = c.getString(c
							.getColumnIndex("gpsy"));
					s.note = c.getString(c.getColumnIndex("note"));
					s.idGioco = c.getInt(c.getColumnIndex("idGioco"));
					
					s.rfid = c.getInt(c.getColumnIndex("rfid"));
					s.rfidArea = c.getInt(c.getColumnIndex("rfidArea"));
					res.put(tableName + "_" + s.idGioco, s);

				} while (c.moveToNext());
				c.close();
			}
		}
		
		return res;
	}

	private class OCParchiOpenHelper extends SQLiteOpenHelper {

		private final String TAG = OCParchiOpenHelper.class.getSimpleName();
		private final Context mHelperContext;
		private SQLiteDatabase mDatabase;

		public OCParchiOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mHelperContext = context;

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			mDatabase = db;
			setupDBFromScratch();

		}

		/**
		 * 
		 */
		private void setupDBFromScratch() {
			Iterator<Entry<String, Struttura>> strutture = mSchemaMap
					.entrySet().iterator();

			while (strutture.hasNext()) {

				Entry<String, Struttura> entry = strutture.next();
				String sqlCreateCode = "CREATE TABLE " + entry.getKey() + " ( ";
				Struttura s = entry.getValue();
				Field[] fields = s.getClass().getFields();
				for (int i = 0; i < fields.length; i++) {
					@SuppressWarnings("rawtypes")
					Class c = fields[i].getType();

					if (!c.equals(Enum.class)) {

						if (i > 0) {
							sqlCreateCode += ",";
						}

						sqlCreateCode += fields[i].getName();

						if (c.equals(int.class) || c.equals(long.class)) {
							sqlCreateCode += " INT ";
						} else {
							sqlCreateCode += " TEXT ";
						}
					}
				}
				
				if(entry.getKey().equals(StruttureEnum.GIOCHI.tipo)){
					sqlCreateCode += ",UNIQUE (idGioco) "; 
				} else if(entry.getKey().equals(StruttureEnum.AREE.tipo)){
					sqlCreateCode += ",UNIQUE (idArea) "; 
				}else if(entry.getKey().equals(StruttureEnum.CONTROLLO.tipo)){
					sqlCreateCode += ",UNIQUE (idRiferimento) "; 
				} else if(entry.getKey().equals(StruttureEnum.INTERVENTO.tipo)){
					sqlCreateCode += ",UNIQUE (idIntervento) ";					
				}
				sqlCreateCode += ")";
				Log.d(TAG, sqlCreateCode);
				mDatabase.execSQL(sqlCreateCode);
			}
			
			//Creazione tabella dei recordTabellaSupporto
			String sqlCreateCode = "CREATE TABLE "+RecordTabellaSupporto.class.getSimpleName()+ " ( ";
			Field[] fields = RecordTabellaSupporto.class.getFields();
			for (int i = 0; i < fields.length; i++) {
				@SuppressWarnings("rawtypes")
				Class c = fields[i].getType();

				if (!c.equals(Enum.class)) {

					if (i > 0) {
						sqlCreateCode += ",";
					}

					sqlCreateCode += fields[i].getName();

					if (c.equals(int.class) || c.equals(long.class)) {
						sqlCreateCode += " INT ";
					} else {
						sqlCreateCode += " TEXT ";
					}
				}
			}
			
			sqlCreateCode += ",UNIQUE (numeroTabella,codice) ) ";
			Log.d(TAG, sqlCreateCode);
			mDatabase.execSQL(sqlCreateCode);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//Seghiamo tutto e non se ne parla più
			Log.d(TAG,"Upgrade del db da versione "+oldVersion+" a versione "+newVersion);
			mDatabase = db;
			Log.d(TAG,"Cancellazione del vecchio DB");
			mHelperContext.deleteDatabase(DATABASE_NAME);
			Log.d(TAG,"Creazione del nuovo DB");
			setupDBFromScratch();
		}

	}

}
