package it.opencontent.android.ocparchitn.db;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAreaUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGiocoUpdate;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.db.entities.StruttureEnum;
import it.opencontent.android.ocparchitn.utils.FileNameCreator;

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

	/**
	 * Performs a database query.
	 * 
	 * @param table
	 *            The table
	 * @param selection
	 *            The selection clause
	 * @param selectionArgs
	 *            Selection arguments for "?" components in the selection
	 * @param columns
	 *            The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	private Cursor query(String table, String selection,
			String[] selectionArgs, String[] columns, String ascending) {

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE <selection> MATCH <selectionArgs> which is an FTS3 search for
		 * the query text (plus a wildcard) inside the word column.
		 * 
		 * - "rowid" is the unique id for all rows but we need this value for
		 * the "_id" column in order for the Adapters to work, so the columns
		 * need to make "_id" an alias for "rowid" - "rowid" also needs to be
		 * used by the SUGGEST_COLUMN_INTENT_DATA alias in order for suggestions
		 * to carry the proper intent data. These aliases are defined in the
		 * DictionaryProvider when queries are made. - This can be revised to
		 * also search the definition text with FTS3 by changing the selection
		 * clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
		 * the entire table, but sorting the relevance could be difficult.
		 */

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
		
		String[] selectionArgs = new String[] { tabella+"" , codice+"" };
		Cursor c = mDatabaseOpenHelper.getReadableDatabase().query(
				RecordTabellaSupporto.class.getSimpleName(), getDefaultColumns(RecordTabellaSupporto.class), selection,
				selectionArgs, null, null, null);
		if(c.moveToFirst()){
			RecordTabellaSupporto r = new RecordTabellaSupporto();
			r.tipo = "";
			r.validita = c.getLong(c.getColumnIndex("validita"));
			r.codice = c.getInt(c.getColumnIndex("codice"));
			r.numeroTabella = tabella;
			r.descrizione = c.getString(c.getColumnIndex("descrizione"));
			
			return r;
		}else {
			return null;
		}
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
					"the deserializeGiocoSingolo must be used only with a 1row resultset. Use the deserielizeGiocoMultipli for multiple rows");
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
	private Area deserializeAreaSingola(Cursor c)
			throws InvalidParameterException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Area a = null;
		if (c.getCount() > 1) {
			c.close();
			throw new InvalidParameterException(
					"the deserializeAreaSingola must be used only with a 1row resultset. Use the deserielizeAreaMultipli for multiple rows");
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

	public long insertScannedGioco(int rfid) {
		ContentValues cv = new ContentValues();
		cv.put("rfid", rfid);

		return mDatabaseOpenHelper.getWritableDatabase().insert(
				StruttureEnum.GIOCHI.tipo, null, cv);
	}

	public void addFotoToGioco(int rfid, int whichOne) {
		ContentValues cv = new ContentValues();
		cv.put("foto" + whichOne,
				FileNameCreator.getSnapshotFullPath(rfid, whichOne));
		cv.put("sincronizzato", false);

		String whereClause = " rfid = ? ";

		String[] whereArgs = new String[] { rfid + "" };
		mDatabaseOpenHelper.getWritableDatabase().update(
				StruttureEnum.GIOCHI.tipo, cv, whereClause, whereArgs);
	}

	public long salvaStrutturaLocally(Struttura struttura) {
		ContentValues cv = new ContentValues();
		String tabella = "";
		if(struttura.getClass().equals(Gioco.class)){
			tabella = StruttureEnum.GIOCHI.tipo;
			Gioco g = (Gioco) struttura;
			cv.put("sincronizzato", false);
			cv.put("rfid", g.rfid);
			cv.put("descrizioneMarca", g.descrizioneMarca);
			cv.put("idGioco", g.idGioco);
			cv.put("numeroserie", g.numeroSerie);
			cv.put("rfidArea", g.rfidArea);
			cv.put("gpsx", g.gpsx);
			cv.put("gpsy", g.gpsy);
			cv.put("note", g.note);
			cv.put("foto0",g.foto0);
			cv.put("foto1",g.foto1);
			cv.put("foto2",g.foto2);
			cv.put("foto3",g.foto3);
			cv.put("foto4",g.foto4);
		} else if(struttura.getClass().equals(Area.class)){
			tabella = StruttureEnum.AREE.tipo;
			Area a = (Area) struttura;
			cv.put("sincronizzato", false);
			cv.put("rfidArea", a.rfidArea);
			cv.put("descrizioneMarca", a.descrizioneMarca);
			cv.put("idArea", a.idArea);
			cv.put("idParco", a.idParco);
			cv.put("idGioco", a.idGioco);
			cv.put("spessore", a.spessore);
			cv.put("superficie", a.superficie);
			cv.put("tipoPavimentazione", a.tipoPavimentazione);
			cv.put("descrizioneArea", a.descrizioneArea);
			
			cv.put("numeroserie", a.numeroSerie);
			cv.put("gpsx", a.gpsx);
			cv.put("gpsy", a.gpsy);
			cv.put("note", a.note);
			cv.put("foto0",a.foto0);
			cv.put("foto1",a.foto1);
			cv.put("foto2",a.foto2);
			cv.put("foto3",a.foto3);
			cv.put("foto4",a.foto4);
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
		cv.put("tipoControllo",c.tipoControllo);
		cv.put("foto",c.foto);
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
	
	public void eliminaCopiaLocaleDiStrutturaSincronizzata(HashMap<String,Object> map){
		ContentValues cv = new ContentValues();
		cv.put(" sincronizzato ", true);
			
		String campo = "";
		String id = "";
		String tipo = "";
		Entry<String,Object> entry = null;
		if(!map.isEmpty()){
		 entry = map.entrySet().iterator().next();
		}
		if( entry != null && entry.getValue().getClass().equals(SOAPAreaUpdate.class)){
			SOAPAreaUpdate sau = (SOAPAreaUpdate) map.entrySet().iterator().next().getValue();
			campo = "idArea = ? ";
			id = sau.idArea;
			tipo =StruttureEnum.AREE.tipo;
		} else if( entry != null && entry.getValue().getClass().equals(SOAPGiocoUpdate.class)){
			SOAPGiocoUpdate sgu = (SOAPGiocoUpdate) map.entrySet().iterator().next().getValue();
			campo = "idGioco = ? ";
			id = sgu.idGioco;
			tipo =StruttureEnum.GIOCHI.tipo;
		}
		
		try{
//			int res = mDatabaseOpenHelper.getWritableDatabase().update(tipo, cv, campo, new String[]{id});
			int res = mDatabaseOpenHelper.getWritableDatabase().delete(tipo, campo, new String[]{ id }); 
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
			String selection = " sincronizzato = ? AND ( rfid > 0 OR rfidArea >  0 ) "; // TODO: trovare un modo
														// per metterli in
														// qualche costante,
														// probabilmente in
														// values

			c = query(tableName, selection, selectionArgs, columns, null);
			if (c != null && c.moveToFirst()) {
				result += c.getCount();
			}
		}
		c = null;
		
		String[] columns = new String[] { " rfid as rfid " };
		String tableName = Controllo.class.getSimpleName();		
		c = query(tableName, null, null, columns, null);
		if(c != null && c.moveToFirst()){
			result += c.getCount();
			c.close();
		}
		return result;
	}
	


	public LinkedHashMap<String,Struttura> getStruttureDaSincronizzare() {
		LinkedHashMap<String, Struttura> res = new LinkedHashMap<String, Struttura>();

		Iterator<Entry<String, Struttura>> strutture = mSchemaMap.entrySet()
				.iterator();

		String[] selectionArgs = new String[] { "0" };

		while (strutture.hasNext()) {

			Entry<String, Struttura> entry = strutture.next();

			String[] columns = getDefaultColumns(entry.getValue().getClass());

			String tableName = entry.getKey();
			String selection = " sincronizzato = ? "; // TODO: trovare un modo
														// per metterli in
														// qualche costante,
														// probabilmente in
														// values

			Cursor c = query(tableName, selection, selectionArgs, columns, null);
			if (c != null && c.moveToFirst()) {
				do {
					Struttura s;
					if (tableName.equals(StruttureEnum.GIOCHI.tipo)) {
						s = new Gioco();
					} else if(tableName.equals(StruttureEnum.AREE.tipo)) {
						s = new Area();
						((Area) s).spessore = c.getFloat(c.getColumnIndex("spessore"));
						((Area) s).superficie = c.getFloat(c.getColumnIndex("superficie"));
						((Area) s).tipoPavimentazione = c.getInt(c.getColumnIndex("tipoPavimentazione"));
						((Area) s).idArea = c.getInt(c.getColumnIndex("idArea"));	
						((Area) s).rfidArea = c.getInt(c.getColumnIndex("rfidArea"));	
					} else {
						s = new Struttura();
					}
					
					s.descrizioneArea = c.getString(c.getColumnIndex("descrizioneArea"));
					s.gpsx = Float.parseFloat(c.getString(c
							.getColumnIndex("gpsx")));
					s.gpsy = Float.parseFloat(c.getString(c
							.getColumnIndex("gpsy")));
					s.note = c.getString(c.getColumnIndex("note"));
					s.idGioco = c.getInt(c.getColumnIndex("idGioco"));
					
					s.rfid = c.getInt(c.getColumnIndex("rfid"));
					s.rfidArea = c.getInt(c.getColumnIndex("rfidArea"));
					s.foto0 = c.getString(c.getColumnIndex("foto0"));
					s.foto1 = c.getString(c.getColumnIndex("foto1"));
					s.foto2 = c.getString(c.getColumnIndex("foto2"));
					s.foto3 = c.getString(c.getColumnIndex("foto3"));
					s.foto4 = c.getString(c.getColumnIndex("foto4"));
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
				sqlCreateCode += ",UNIQUE (idGioco) "; // TODO fare qualcosa per non hardcodarli
				} else if(entry.getKey().equals(StruttureEnum.AREE.tipo)){
					sqlCreateCode += ",UNIQUE (idArea) "; // TODO fare qualcosa per non hardcodarli
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

			//Creazione tabella dei controlli
			sqlCreateCode = "CREATE TABLE "+Controllo.class.getSimpleName()+ " ( ";
			fields = Controllo.class.getFields();
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
			
			sqlCreateCode += ",UNIQUE (idRiferimento) ) ";
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
