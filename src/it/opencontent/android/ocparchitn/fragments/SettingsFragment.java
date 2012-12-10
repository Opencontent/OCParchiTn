package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.R;

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

}
