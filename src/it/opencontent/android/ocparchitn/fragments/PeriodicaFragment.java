package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PeriodicaFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.periodica_fragment, container, false);
		return view;
	}
}
