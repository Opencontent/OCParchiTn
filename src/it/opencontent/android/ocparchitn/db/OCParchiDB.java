package it.opencontent.android.ocparchitn.db;

import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.RecordTabellaSupporto;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.db.entities.StruttureEnum;
import it.opencontent.android.ocparchitn.utils.FileNameCreator;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "parchitn";
	private final OCParchiOpenHelper mDatabaseOpenHelper;
	@SuppressWarnings("unused")
	private Context context;

	private static final HashMap<String, Struttura> mSchemaMap = buildSchemaMap();

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

		Struttura table = mSchemaMap.get(tableName);
		Field[] fields = table.getClass().getFields();
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
	
	public void TabelleSupportoUpdate(RecordTabellaSupporto[] records){
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
			if (data.size() > 0) {
				g = new Gioco(data.entrySet(), null);
			}
			return g;
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

	public long salvaGiocoLocally(Gioco gioco) {
		ContentValues cv = new ContentValues();

		cv.put("sincronizzato", false);
		cv.put("rfid", gioco.rfid);
		cv.put("descrizioneMarca", gioco.descrizioneMarca);
		cv.put("idGioco", gioco.idGioco);
		cv.put("numeroserie", gioco.numeroSerie);
		cv.put("gpsx", gioco.gpsx);
		cv.put("gpsy", gioco.gpsy);
		cv.put("note", gioco.note);
		cv.put("foto0",gioco.foto0);
		cv.put("foto1",gioco.foto1);
		cv.put("foto2",gioco.foto2);
		cv.put("foto3",gioco.foto3);
		cv.put("foto4",gioco.foto4);

		long id = -1;
		try {
			id = mDatabaseOpenHelper.getWritableDatabase().insertWithOnConflict(
					StruttureEnum.GIOCHI.tipo, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		} catch (SQLiteConstraintException e) {
			Log.e(TAG, e.getMessage());
			id = -2;
		}
		return id;
	}
	
	public void marcaStrutturaSincronizzata(Gioco gioco){
		ContentValues cv = new ContentValues();

		cv.put(" sincronizzato ", true);
				
		try{
			int res = mDatabaseOpenHelper.getWritableDatabase().update(StruttureEnum.GIOCHI.tipo, cv, "idGioco = ?", new String[]{""+gioco.idGioco});
			Log.d(TAG,"Aggiornate "+res+" righe");
		}catch(SQLiteConstraintException e){
			Log.e(TAG, e.getMessage());
		}
	}	

	/**
	 * Returns a Cursor over all words that match the given query
	 * 
	 * @param query
	 *            The string to search for
	 * @param columns
	 *            The columns to include, if null then all are included
	 * @return Cursor over all words that match, or null if none found.
	 */
	public int getPendingSynchronizations() {
		Iterator<Entry<String, Struttura>> strutture = mSchemaMap.entrySet()
				.iterator();

		String[] selectionArgs = new String[] { "0" };
		int result = 0;

		while (strutture.hasNext()) {

			Entry<String, Struttura> entry = strutture.next();
			String[] columns = new String[] { " sincronizzato as sincronizzato " };
			String tableName = entry.getKey();
			String selection = " sincronizzato = ? "; // TODO: trovare un modo
														// per metterli in
														// qualche costante,
														// probabilmente in
														// values

			Cursor c = query(tableName, selection, selectionArgs, columns, null);
			if (c != null && c.moveToFirst()) {
				result += c.getCount();
			}
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
					} else {
						s = new Area();
					}
					
					s.gpsx = Float.parseFloat(c.getString(c
							.getColumnIndex("gpsx")));
					s.gpsy = Float.parseFloat(c.getString(c
							.getColumnIndex("gpsy")));
					s.note = c.getString(c.getColumnIndex("note"));
					s.idGioco = c.getInt(c.getColumnIndex("idGioco"));
					s.rfid = c.getInt(c.getColumnIndex("rfid"));
					s.foto0 = c.getString(c.getColumnIndex("foto0"));
					s.foto1 = c.getString(c.getColumnIndex("foto1"));
					s.foto2 = c.getString(c.getColumnIndex("foto2"));
					s.foto3 = c.getString(c.getColumnIndex("foto3"));
					s.foto4 = c.getString(c.getColumnIndex("foto4"));
					res.put(tableName + "_" + s.idGioco, s);

				} while (c.moveToNext());
			}
		}

		return res;
	}

	private class OCParchiOpenHelper extends SQLiteOpenHelper {

		private final String TAG = OCParchiOpenHelper.class.getSimpleName();
		// private final Context mHelperContext;
		private SQLiteDatabase mDatabase;

		public OCParchiOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// mHelperContext = context;

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			mDatabase = db;
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
				sqlCreateCode += ",UNIQUE (idGioco,rfid) "; // TODO fare
																// qualcosa per
																// non
																// hardcodarli
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
			sqlCreateCode += " ) ";
			Log.d(TAG, sqlCreateCode);
			mDatabase.execSQL(sqlCreateCode);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}

}
