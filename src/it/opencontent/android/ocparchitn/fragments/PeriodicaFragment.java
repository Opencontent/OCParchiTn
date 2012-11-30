package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PeriodicaFragment extends Fragment implements ICustomFragment {

	private static final String TAG = PeriodicaFragment.class.getSimpleName();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.periodica_fragment, container, false);
		return view;
	}

	@Override
	public void salvaModifiche(View v) {
		Log.d(TAG,"salva modifiche alla periodica");
		
	}

	@Override
	public void editMe(View v) {
		// TODO Auto-generated method stub
		
	}
}
