package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.db.entities.Struttura;

import java.util.Comparator;
import java.util.Map;

public class StruttureComparator implements Comparator<String>{
	
	private Map<String,Struttura> mMap; 
	
	public StruttureComparator(Map<String,Struttura> map){
		this.mMap = map;
	}
	
	
	@Override
	public int compare(String lhs, String rhs) {
		if(mMap.get(lhs).ultimaSincronizzazione > mMap.get(rhs).ultimaSincronizzazione){
			return -1;
		}
		return 1;
	}
	
}