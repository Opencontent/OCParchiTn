package it.opencontent.android.ocparchitn.db.entities;

import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

public class Gioco extends Struttura {

	  
	
	public Gioco() {
		tipo = "gioco";
	}

	public Gioco(Set<Entry<String, Object>> entrySet,int rfid,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,rfid,context);
		tipo = "gioco";	
	}
	public Gioco(Set<Entry<String, Object>> entrySet,Context context) {
		// TODO Auto-generated constructor stub
		super(entrySet,context);
		tipo = "gioco";
	}
}
