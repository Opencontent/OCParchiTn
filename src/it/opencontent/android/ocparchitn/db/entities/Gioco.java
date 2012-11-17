package it.opencontent.android.ocparchitn.db.entities;

import java.util.Map.Entry;
import java.util.Set;

public class Gioco extends Struttura {

	public Gioco() {
		this.tipo = "gioco";
	}

	public Gioco(Set<Entry<String, Object>> entrySet) {
		// TODO Auto-generated constructor stub
		super(entrySet);
		this.tipo = "gioco";
	}
}
