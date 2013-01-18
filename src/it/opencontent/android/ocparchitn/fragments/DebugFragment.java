package it.opencontent.android.ocparchitn.fragments;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAutGiochi;
import it.opencontent.android.ocparchitn.activities.MainActivity;
import it.opencontent.android.ocparchitn.activities.SettingsActivity;
import it.opencontent.android.ocparchitn.activities.SynchroSoapActivity;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.utils.AuthCheck;

import java.util.HashMap;

import org.kxml2.kdom.Element;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DebugFragment extends Fragment  implements ICustomFragment{

	private final static String TAG = DebugFragment.class.getSimpleName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.debug_fragment, container, false);
		return view;
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
		
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(getActivity().getApplicationContext(),
				SynchroSoapActivity.class);
		HashMap<String, Object> map = new HashMap<String, Object>();
		serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
		switch(v.getId()){
		case R.id.status_token_button:
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_TOKEN_STATUS_METHOD_NAME);
			startActivityForResult(serviceIntent, Constants.SOAP_GET_TOKEN_STATUS_REQUEST_CODE);			
			break;
		case R.id.invalidate_token_button:
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_INVALIDATE_TOKEN_METHOD_NAME);
			startActivityForResult(serviceIntent, Constants.SOAP_GET_INVALIDATE_TOKEN_REQUEST_CODE);			
			break;
		case R.id.refresh_token_button:
			serviceIntent.putExtra(Constants.EXTRAKEY_METHOD_NAME, Constants.GET_LOGINUSER_METHOD_NAME);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String username = prefs.getString(getString(R.string.settings_key_username), "UNSET");
			String password = prefs.getString(getString(R.string.settings_key_password), "UNSET");
			
			map.put("args0", username);
			map.put("args1", password);
			serviceIntent.putExtra(Constants.EXTRAKEY_DATAMAP, map);
			startActivityForResult(serviceIntent, Constants.SOAP_GET_TOKEN_REQUEST_CODE);							
			break;
		}
		
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		HashMap<String, Object> res;
		switch(requestCode){
		case Constants.SOAP_GET_TOKEN_STATUS_REQUEST_CODE:
			res = SynchroSoapActivity.getRes(Constants.GET_TOKEN_STATUS_METHOD_NAME);
			if(res != null){
				if(res.containsKey("primitive")){
			
				boolean status = Boolean.parseBoolean((String) res.get("primitive"));
				Log.d(TAG, "Status: "+status);
				} else if(res.containsKey("success")){
					Log.d(TAG, "Status: "+res.get("string"));					
				}
			}
			Log.d(TAG,"Status request response");
			break;
		case Constants.SOAP_GET_INVALIDATE_TOKEN_REQUEST_CODE:
			res = SynchroSoapActivity.getRes(Constants.GET_INVALIDATE_TOKEN_METHOD_NAME);
			if(res != null && res.containsKey("primitive")){
				boolean status = Boolean.parseBoolean((String) res.get("primitive"));
				Log.d(TAG, "Invalidate: "+status);
			}
			Log.d(TAG,"invalidate request response");
			break;
		case Constants.SOAP_GET_TOKEN_REQUEST_CODE:
			res = SynchroSoapActivity.getRes(Constants.GET_LOGINUSER_METHOD_NAME);
			if(res != null && res.containsKey("success") ){
				String faultString = res.get("string").toString();
				Toast.makeText(getActivity().getApplicationContext(), faultString, Toast.LENGTH_SHORT).show();
				AlertDialog.Builder changeCredentials = new AlertDialog.Builder(getActivity());
				changeCredentials.setTitle("Credenziali errate");
				changeCredentials.setMessage(faultString+"\nClicca su OK per modificare le credenziali");
				changeCredentials.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(getActivity().getApplicationContext(), SettingsActivity.class);
						startActivity(intent);
						return;
					}
					
				});
				changeCredentials.show();
				
			} else if(res != null && res.containsKey("mapped")){
				SOAPAutGiochi auth = (SOAPAutGiochi) res.get("mapped");
				int a = auth.autComune;
			}
			
			if(res!=null && res.containsKey("headerIn")){
				AuthCheck.setHeaderOut((Element[]) res.get("headerIn"));
			}
			
			break;			
		}
	}
	
	
}
