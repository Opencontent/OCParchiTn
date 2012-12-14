package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.db.entities.Struttura;

import java.util.HashMap;

import android.view.View;

public interface ICustomFragment {
	public void salvaModifiche(View v);
	public void editMe(View v);
	public void showError(HashMap<String,String> map);
	public void showStrutturaData(Struttura struttura);
}
