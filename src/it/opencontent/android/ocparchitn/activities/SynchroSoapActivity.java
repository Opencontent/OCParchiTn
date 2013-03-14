package it.opencontent.android.ocparchitn.activities;

import it.opencontent.android.ocparchitn.Constants;
import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAreaUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPControlloUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPFotoupdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGiocoUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPInterventoUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkAutException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkGiochiException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkSrvException;
import it.opencontent.android.ocparchitn.db.OCParchiDB;
import it.opencontent.android.ocparchitn.db.entities.Area;
import it.opencontent.android.ocparchitn.db.entities.Controllo;
import it.opencontent.android.ocparchitn.db.entities.Gioco;
import it.opencontent.android.ocparchitn.db.entities.Intervento;
import it.opencontent.android.ocparchitn.db.entities.Struttura;
import it.opencontent.android.ocparchitn.db.entities.StruttureEnum;
import it.opencontent.android.ocparchitn.services.IRemoteConnection;
import it.opencontent.android.ocparchitn.utils.PlatformChecks;
import it.opencontent.android.ocparchitn.utils.SoapConnector;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SynchroSoapActivity extends Activity implements IRemoteConnection {

	private static final String TAG = SynchroSoapActivity.class.getSimpleName();
	private String methodName;
	private HashMap<String, Object> requestParameters;
	private HashMap<String, Object> res;
	private Thread remoteThread;
	private static OCParchiDB db;
	private static HashMap<String,HashMap<String,Object>> allmaps = new HashMap<String, HashMap<String,Object>>();

	private static int queueLength = 0;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.remote_loading_dialog);

		Intent intent = getIntent();
		queueLength = 0;

		db = new OCParchiDB(getApplicationContext());
		methodName = (String) intent.getExtras().get(
				Constants.EXTRAKEY_METHOD_NAME);
		requestParameters = (HashMap<String, Object>) intent.getExtras().get(
				Constants.EXTRAKEY_DATAMAP);
		
		if (methodName.equals(Constants.EXTRAKEY_SYNC_ALL)) {
			
//			LinkedHashMap<String, Struttura> set = db.getStruttureDaSincronizzare(false);
			TreeMap<String, Struttura> set = db.getStruttureDaSincronizzare(false);
			if (!set.isEmpty() && PlatformChecks.siamoOnline(getApplicationContext())) {
				Iterator<String> keyIterator = set.keySet().iterator();
				while (keyIterator.hasNext()) {
					String k = keyIterator.next();
					if(set.get(k).getClass().equals(Gioco.class)){
						Gioco g = (Gioco) set.get(k);
						sincronizzaLaStruttura(g,null,true);						
					}else if(set.get(k).getClass().equals(Intervento.class)){
						Intervento i = (Intervento) set.get(k);
						sincronizzaLaStruttura(i,null,true);						
					}else if(set.get(k).getClass().equals(Area.class)){
						Area a = (Area) set.get(k);
						sincronizzaLaStruttura(a,null,true);												
					} else if( set.get(k).getClass().equals(Controllo.class)){
						Controllo c = (Controllo) set.get(k);
						sincronizzaLaStruttura(c,null,true);
					}
				}
			} else {
				setResult(RESULT_OK,getIntent());
				finish();
			}
			// Messo nel thread e comandato dalla queuelength
			// finish();
		} else if(methodName.equals(Constants.GET_TABELLA_METHOD_NAME)){
			//facciamo un loop delle tabelle e le facciamo accumulare
			for(int tid : Constants.ID_TABELLE_SUPPORTO){
				HashMap<String, Object> thismap = new HashMap<String, Object>();
				thismap.put("args0", "" + tid);
				/**
				 * Il @methodName in questo ciclo deve essere allineato con il methodname 
				 * nella main activity che va a sbobinare i risultati di questo stesso ciclo
				 * L'idea è che qui facciamo partire N chiamate asincrone
				 * L'ultima lancia il finish() di questa activity
				 * L'onResult della main si va a prendere i risultati con un ciclo identico
				 */
				getRemoteResponse(methodName, thismap, false,methodName+"_"+tid);	
			}
			
		}else if(methodName.equals(Constants.EXTRAKEY_SYNC_ONE)){
			if(requestParameters.containsKey(Constants.EXTRAKEY_STRUCTURE_TYPE)){
				String tipo = (String) requestParameters.get(Constants.EXTRAKEY_STRUCTURE_TYPE);
				String id = (String) requestParameters.get(Constants.EXTRAKEY_STRUCTURE_ID);
				Struttura s = null; 
				String mapId = (String) intent.getExtras().get(Constants.EXTRAKEY_MAPID);
				if(tipo.equals(StruttureEnum.GIOCHI.tipo)){
					s = db.readGiocoLocallyByID(Integer.parseInt(id),false);
				} else if(tipo.equals(StruttureEnum.AREE.tipo)){
					s = db.readAreaLocallyByID(Integer.parseInt(id),false);
				} else if(tipo.equals(StruttureEnum.CONTROLLO.tipo)){
					s = db.readControlloLocallyByID(id,false);
				} else if(tipo.equals(StruttureEnum.INTERVENTO.tipo)){
					s = db.readInterventoLocallyByID(id,false);
				} 
				if(s!=null){
					sincronizzaLaStruttura(s, mapId,false);
				} else {
					setResult(RESULT_CANCELED);
					finish();
				}
			} else {
				setResult(RESULT_CANCELED);
				finish();
			}
		} else if(methodName.equals(Constants.EXTRAKEY_SYNC_ONE_FOTO)){
			if(requestParameters.containsKey(Constants.EXTRAKEY_STRUCTURE_TYPE)){
				String tipo = (String) requestParameters.get(Constants.EXTRAKEY_STRUCTURE_TYPE);
				String id = (String) requestParameters.get(Constants.EXTRAKEY_STRUCTURE_ID);
				Struttura s = null; 
//				String mapId = (String) intent.getExtras().get(Constants.EXTRAKEY_MAPID);
				if(tipo.equals(StruttureEnum.GIOCHI.tipo)){
					s = db.readGiocoLocallyByID(Integer.parseInt(id),false);
				} else if(tipo.equals(StruttureEnum.AREE.tipo)){
					s = db.readAreaLocallyByID(Integer.parseInt(id),false);
				} else if(tipo.equals(StruttureEnum.CONTROLLO.tipo)){
					s = db.readControlloLocallyByID(id,false);
				} else if(tipo.equals(StruttureEnum.INTERVENTO.tipo)){
					s = db.readInterventoLocallyByID(id,false);
				} 
				if(s!=null){
					sincronizzaTutteLeFoto(s);
				} else {
					setResult(RESULT_CANCELED);
					finish();
				}
			} else {
				setResult(RESULT_CANCELED);
				finish();
			}
		}else {
			getRemoteResponse(methodName, requestParameters, true,methodName);			
		}
		db.close();
	}


	private void sincronizzaLaStruttura(Struttura s,String mapId,boolean ancheLeFoto) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(s.getClass().equals(Gioco.class)){
			Gioco g = (Gioco) s;

			SOAPGiocoUpdate gu = new SOAPGiocoUpdate();
			gu.idGioco = "" + g.idGioco;
			//TODO: controllare i check di congruenza dato che me li hanno cambiati
			if (g.rfid > 0) {
				gu.rfid = "" + g.rfid;
				gu.gpsx = "" + g.gpsx;
				gu.gpsy = "" + g.gpsy;
				gu.note = g.note;
				gu.numeroSerie = g.numeroSerie;
				gu.rifCartografia = g.rifCartografia;
				if(g.rfidArea > 0){
				gu.rfidArea = "" + g.rfidArea;
				}
				gu.posizioneRfid =  g.posizioneRfid;
				map.put("Giocoupdate", gu);
				if(g.spostamento>0){
					if(mapId == null){
						mapId = Constants.SET_SPOSTAMENTO_METHOD_NAME;
					}
					getRemoteResponse(Constants.SET_SPOSTAMENTO_METHOD_NAME, map, false,mapId);
				} else {
					if(mapId == null){
						mapId = Constants.SET_GIOCO_METHOD_NAME;
					}
					getRemoteResponse(Constants.SET_GIOCO_METHOD_NAME, map, false,mapId);
				}
				if(ancheLeFoto){
				sincronizzaTutteLeFoto(g);
				}
			}else {
				Log.d(TAG,"Gioco senza rfid, non sincronizzato "+g.idGioco);
				Toast.makeText(this,"Gioco senza rfid, non sincronizzato "+g.idGioco,Toast.LENGTH_SHORT).show();
				if (queueLength <= 0) {
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		} else if(s.getClass().equals(Area.class)){
			Area a = (Area) s;
			SOAPAreaUpdate au = new SOAPAreaUpdate();
			if(a.rfidArea>0){
				au.idArea = "" + a.idArea;
				au.descrizione = "" + a.descrizioneArea ;
				au.note = a.note;
				au.rfid = "" + a.rfidArea;
				au.spessore = "" + a.spessore;
				au.superficie = "" + a.superficie;
				au.tipoPavimentazione = "" + a.tipoPavimentazione;
				au.posizioneRfid = a.posizioneRfid;			
				map.put("Areaupdate", au);
				if(mapId == null){
					mapId = Constants.SET_AREA_METHOD_NAME;
				}
				getRemoteResponse(Constants.SET_AREA_METHOD_NAME, map, false,mapId);
				if(ancheLeFoto){
				sincronizzaTutteLeFoto(a);
				}			
			}else {
				Log.d(TAG,"Area senza rfid, non sincronizzato "+a.idArea);
				Toast.makeText(this,"Area senza rfid, non sincronizzato "+a.idArea,Toast.LENGTH_SHORT).show();
				if (queueLength <= 0) {
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		}else if(s.getClass().equals(Controllo.class)){
			Controllo c = (Controllo) s;
			if (c.idRiferimento != null && c.noteControllo.length() > 0) {
				
				SOAPControlloUpdate cu = new SOAPControlloUpdate();
				cu.controllo = c.controllo;
				String DATE_FORMAT = "yyyy-MM-dd";
				String TIME_FORMAT = "HH:mm:ss";
			    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT,Locale.US);
			    SimpleDateFormat hdf = new SimpleDateFormat(TIME_FORMAT,Locale.US);
			    Calendar c1 = Calendar.getInstance(); 
				cu.dtControllo = sdf.format(c1.getTime());
				cu.noteEsito = c.noteControllo;
				cu.oraControllo = hdf.format(c1.getTime());
				cu.rfid = c.rfid;
				cu.tipoControllo = c.tipoControllo;
				cu.idRiferimento = c.idRiferimento;
				cu.tipoSegnalazione = c.tipoSegnalazione;				
				
				map.put("Controlloupdate", cu);
				if(mapId == null){
					mapId = Constants.SET_CONTROLLO_METHOD_NAME;
				}
				getRemoteResponse(Constants.SET_CONTROLLO_METHOD_NAME, map, false,mapId);
				if(ancheLeFoto){
				sincronizzaTutteLeFoto(c);
				}
			}else {
				Log.d(TAG,"Controllo senza dati necessari, non sincronizzato "+c.idRiferimento);
				Toast.makeText(this,"Controllo senza note, non sincronizzato "+c.idRiferimento,Toast.LENGTH_SHORT).show();
				if (queueLength <= 0) {
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		}else if(s.getClass().equals(Intervento.class)){
			Intervento c = (Intervento) s;
			if (c.idRiferimento != null && c.noteEsecuzione.length() > 0) {
				
				SOAPInterventoUpdate iu = new SOAPInterventoUpdate();

				iu.idRiferimento = c.idRiferimento+"";
				iu.dtInizioIntervento = c.dtInizioItervento;
				iu.oraInizioIntervento = c.oraInizioItervento;
				iu.dtFineIntervento = c.dtFineItervento;
				iu.oraFineIntervento = c.oraFineItervento;
				iu.noteEsito = c.noteEsecuzione;
				if(c.rfid > 0){
					iu.rfid = c.rfid+"";
				} else if(c.rfidArea > 0){
					iu.rfid = c.rfidArea+"";
				}
				iu.tipoEsito = c.codEsito+"";

								
				map.put("Interventoupdate", iu);
				if(mapId == null){
					mapId = Constants.SET_INTERVENTO_METHOD_NAME;
				}				
				getRemoteResponse(Constants.SET_INTERVENTO_METHOD_NAME, map, false,mapId);
				if(ancheLeFoto){
				sincronizzaTutteLeFoto(s);
				}
			}else {
				Log.d(TAG,"Intervento senza dati necessari, non sincronizzato "+c.idRiferimento);
				Toast.makeText(this,"Intervento senza note, non sincronizzato "+c.idRiferimento,Toast.LENGTH_SHORT).show();
				if (queueLength <= 0) {
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		}
	}



	private void sincronizzaTutteLeFoto(Struttura g) {
		HashMap<String, Object> map;
		// cicliamo le foto eventuali
		String tipoFoto = "";
		int idRiferimento = 0;
		String suffissoImmagine = "";
		if(g.getClass().equals(Gioco.class)){
			tipoFoto = Constants.CODICE_STRUTTURA_GIOCO+"";
			idRiferimento = g.idGioco ;
			suffissoImmagine = "gioco_";
			g = db.readGiocoLocallyByID(idRiferimento,true);
		}else if(g.getClass().equals(Area.class)){
			tipoFoto = Constants.CODICE_STRUTTURA_AREA+"";			
			idRiferimento = ((Area) g).idArea;
			suffissoImmagine = "area_";
			g = db.readAreaLocallyByID(idRiferimento,true);
		}else if(g.getClass().equals(Controllo.class)){
			tipoFoto = Constants.CODICE_STRUTTURA_CONTROLLO +"";			
			idRiferimento = Integer.parseInt(((Controllo) g).idRiferimento);
			suffissoImmagine = "controllo_";
			g = db.readControlloLocallyByID(idRiferimento+"",true);
		}else if(g.getClass().equals(Intervento.class)){
			tipoFoto = Constants.CODICE_STRUTTURA_INTERVENTO+"";			
			idRiferimento = Integer.parseInt(((Intervento) g).idRiferimento);
			suffissoImmagine = "intervento_";
			g = db.readInterventoLocallyByID(idRiferimento+"",true);
		}
		

		if(g!=null){
			for(int i = 0; i < Constants.MAX_SNAPSHOTS_AMOUNT;i++){
				SOAPFotoupdate fu = new SOAPFotoupdate();
				fu.tipoFoto = tipoFoto;
				fu.idRiferimento = idRiferimento+"";
				fu.sovrascrittura = true;
				fu.estensioneImmagine = Constants.ESTENSIONE_FOTO;
				switch(i){
				case 0:
					fu.immagine = g.foto0;
					break;
				case 1:
					fu.immagine = g.foto1;
					break;
				}
				if(fu.immagine!=null && fu.immagine.length()>1){
					fu.nomeImmagine = suffissoImmagine + idRiferimento + "_foto_"+i;
					map = new HashMap<String, Object>();
					map.put("Fotoupdate", fu);
					getRemoteResponse("setFoto", map, false,"setFoto");
				}
			}
//			db.eliminaCopiaLocaleDiStrutturaSincronizzata(g);
		}
	}

	@Override
	public void sendRequest(Object data) {

	}

	public static HashMap<String, Object> getRes(String key) {
		HashMap<String,Object> ret; 
		if(allmaps.containsKey(key)){
			ret = allmaps.get(key);
		allmaps.remove(key);
		} else {
			ret = null;
		}
		return ret;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (remoteThread != null && remoteThread.isAlive()
				&& !remoteThread.isInterrupted()) {
			remoteThread.interrupt();
		}
		db.close();
	}

	public final void updateCounter(int amount) {
		TextView counterView = (TextView) findViewById(R.id.remote_loading_dialog_counter);
		if (counterView != null) {
			counterView.setText(" Elementi in coda : " + (queueLength+1));
		}
	}
	
	public final void toastException(String message){
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void getRemoteResponse(String m, HashMap<String, Object> d,
			boolean f,String md) {

		//Abbiamo bisogno di tutti questi elementi nel runnable
		//per cui li definiamo come final
		
		final String method = m;
		final boolean finish = f;
		final Handler h = new Handler();
		final String mapid = md; 
		final HashMap<String,Object> data;
		
		if (d == null) {
			data = new HashMap<String, Object>();
		} else {
			data = d;
			
		}

		if (!finish) {
			queueLength++;
		}
		updateCounter(queueLength);

		final PropertyInfo[] properties = new PropertyInfo[data.entrySet()
				.size()];
		int i = 0;
		Iterator<Entry<String, Object>> iterator = data.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			PropertyInfo pi = new PropertyInfo();
			pi.setName(entry.getKey());
			pi.setType(entry.getValue().getClass());
			pi.setValue(entry.getValue());
			properties[i] = pi;
			i++;
		}
		if (PlatformChecks.siamoOnline(this.getApplicationContext())) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					SoapConnector sc = new SoapConnector();
					try {
						res = sc.soap(method, Constants.SOAP_ENDPOINT,
								Constants.SOAP_NAMESPACE, Constants.SOAP_URL,
								properties);

							allmaps.put(mapid, res);
							
						
						if (!finish) {
							
							if(res.get("exception") != null){
								String outText = "";
								KvmSerializable exception = (KvmSerializable) res.get("exception");
								if(exception.getClass().equals(SOAPSrvGiocoArkAutException.class)){
									String codice = ((SOAPSrvGiocoArkAutException) exception).codice;
									outText += "\nCodice: "+codice;
									outText += "\nMessaggio: "+((SOAPSrvGiocoArkAutException) exception).message;
								}else if(exception.getClass().equals(SOAPSrvGiocoArkGiochiException.class)){
									outText += "\nCodice: "+((SOAPSrvGiocoArkGiochiException) exception).codice;
									outText += "\nMessaggio: "+((SOAPSrvGiocoArkGiochiException) exception).message;
								} else {
									outText += "\nMessaggio: "+((SOAPSrvGiocoArkSrvException) exception).message;
								}
								final String t = outText;
								h.post(new Runnable() {
									
									@Override
									public void run() {
										toastException(method+"\n"+t);										
									}
								});
							} 
							queueLength--;
						}
						h.post(new Runnable() {
							public void run() {
								updateCounter(queueLength);
							}
						});
						if (finish || queueLength <= 0) {
							setResult(RESULT_OK, getIntent());
							finish();
						}
					} catch (IOException e) {
						queueLength--;
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						queueLength--;
						e.printStackTrace();
					} catch (Exception e) {
						queueLength--;
						e.printStackTrace();
					} finally {
						res = sc.getMap();
						if (finish || queueLength <= 0) {
							allmaps.put(method, res);
							setResult(RESULT_CANCELED, getIntent());
							finish();
						}
					}
				}
			};
			remoteThread = new Thread(runnable);
			remoteThread.start();

		} else {
			if (res == null) {
				res = new HashMap<String, Object>();
			}
			res.put("Errore", "Network non connesso");

			Button wirelessConfig = new Button(getApplicationContext());
			wirelessConfig.setText("Vai al centro connessioni");
			wirelessConfig.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(
							Settings.ACTION_WIRELESS_SETTINGS),
							BaseActivity.SETUP_NETWORK);
				}
			});

			res.put("Azioni possibili:", wirelessConfig);
			allmaps.put(method, res);
			Log.d(TAG, "Network non connesso");
			if (finish) {
				setResult(RESULT_CANCELED, getIntent());
				finish();
			}
		}
	}

}
