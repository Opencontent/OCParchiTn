package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.db.entities.Struttura;

import java.util.HashMap;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

public class SettingsFragment extends PreferenceFragment  implements ICustomFragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public void salvaModifiche(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editMe(View v) {
		// TODO Auto-generated method stub
		
	}
	public void showError(HashMap<String,String> map){
		
	}

	@Override
	public void showStrutturaData(Struttura struttura) {
		// TODO Auto-generated method stub
		
	}	
	@Override
	public void clickedMe(View v) {
		// TODO Auto-generated method stub
		
	}	

}
