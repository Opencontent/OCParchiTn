package it.opencontent.android.ocparchitn.db;

import android.app.SearchManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OCParchiDB {

	private static final int DATABASE_VERSION = 0;
	private static final String DATABASE_NAME = "parchitn";	
	private final OCParchiOpenHelper mDatabaseOpenHelper;
	
	
	
	
	public OCParchiDB(Context context){
		mDatabaseOpenHelper = new OCParchiOpenHelper(context);
		
	} 
	
	
	private class OCParchiOpenHelper extends SQLiteOpenHelper{



		public OCParchiOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
}
