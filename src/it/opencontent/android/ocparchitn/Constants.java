package it.opencontent.android.ocparchitn;

public class Constants {
	public static final String IMAGE_SUBDIRECTORY = "images";

	public final static String TAKE_SNAPSHOT = "it.opencontent.android.ocparchitn.CameraAction";
	
	public final static int PERMESSO_NONE = 0;
	public final static int PERMESSO_VISUALIZZA = 15;
	public final static int PERMESSO_MODIFICA = 32;
	public final static int PERMESSO_ADMIN = 1000;
	
	public final static String PREFISSO_NDEF = "parchi://struttura";
	
	public final static String EXTRAKEY_DATAMAP = "dataMap";
	public final static String EXTRAKEY_DATAMAP_RESULT = "dataMapResult";
	public final static String EXTRAKEY_METHOD_NAME = "methodName";
	public final static String EXTRAKEY_STRUCTURE_TYPE = "tipoStruttura";
	public final static String GET_GIOCO_METHOD_NAME = "getGioco";
	public final static String SET_GIOCO_METHOD_NAME = "setGioco";
	public final static String SET_AREA_METHOD_NAME = "setArea";
	public final static String SET_FOTO_METHOD_NAME = "setFoto";
	public final static String GET_GIOCO_ID_METHOD_NAME = "getGiocoId";
	public final static String GET_AREA_METHOD_NAME = "getArea";
	public final static String GET_AREA_ID_METHOD_NAME = "getAreaId";
	public final static String GET_FOTO_METHOD_NAME = "getFoto";
	public final static String GET_INFO_METHOD_NAME = "getInfo";
	public final static String GET_LOGINUSER_METHOD_NAME = "loginUser";
	public final static String GET_TOKEN_STATUS_METHOD_NAME = "statusToken";
	public final static String GET_INVALIDATE_TOKEN_METHOD_NAME = "invalidateToken";
	public final static String GET_TABELLA_METHOD_NAME = "getTabella";
	
	public final static String EXTRAKEY_FOTO_NUMBER = "fotoNumber";
	public final static String EXTRAKEY_RFID = "rfid";
	public final static String EXTRAKEY_SYNC_ALL = "sync_all";
	public final static String EXTRAKEY_ID_TABELLA_REMOTA = "id_tabella";
	
	public final static int TABELLA_MARCHE = 1;
	public final static int TABELLA_TIPO_MAUNTENZIONI = 2;
	public final static int TABELLA_TIPO_PAVIMENTAZIONI = 5;
	public final static int TABELLA_TIPO_GIOCO = 6;
	public final static int TABELLA_PRODUTTORI = 7;
	public final static int TABELLA_TIPO_PARCO = 8;	
	public final static int TABELLA_CIRCOSCRIZIONI = 9;
	public final static int TABELLA_TIOLOGIA_INTERVENTO = 10;
	public final static int TABELLA_RIFERIMENTO_INTERVENTO = 11;
	public final static int TABELLA_STATO_GIOCO = 12;
	public final static int TABELLA_SEGNALAZIONI = 13;
	public final static int TABELLA_ESITI_INTERVENTO = 14;
	
	public final static int[] ID_TABELLE_SUPPORTO = new int[]{1,
			TABELLA_TIPO_MAUNTENZIONI,
			TABELLA_TIPO_PAVIMENTAZIONI,
			TABELLA_TIPO_GIOCO,
			TABELLA_PRODUTTORI,
			TABELLA_TIPO_PARCO,
			TABELLA_CIRCOSCRIZIONI,
			TABELLA_TIOLOGIA_INTERVENTO,
			TABELLA_RIFERIMENTO_INTERVENTO,
			TABELLA_STATO_GIOCO,
			TABELLA_SEGNALAZIONI,
			TABELLA_ESITI_INTERVENTO };
	
	public final static int RIFERIMENTO_INTERVENTO_VERIFICA_PROGRAMMATA = 1;
	public final static int RIFERIMENTO_INTERVENTO_MANUTENZIONE_PROGRAMMATA = 2;
	public final static int RIFERIMENTO_INTERVENTO_CONTROLLO_OCCASIONALE = 3;
	public final static int RIFERIMENTO_INTERVENTO_INTERVENTO = 3;
	
	public final static int ESITO_INTERVENTO_POSITIVO = 1;
	public final static int ESITO_INTERVENTO_NEGATIVO = 2;
	
	public final static int MAX_SNAPSHOTS_AMOUNT = 5;
	
	public final static int MAX_UNSYNCHRONIZED_LOCAL_ENTITIES = 10;
	
	public final static int GPS_BEACON_INTERVAL = 5000;
	public final static int GPS_METER_THRESHOLD = 0;
	
	public final static String STATUS_MESSAGE_SERVER_STATUS = "server_status";
	public final static String STATUS_MESSAGE_REMOTE_UPDATE_STATUS = "remote_update_status";
	public final static String STATUS_MESSAGE_GPS_STATUS = "gps_status";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_OK = "GPS OK:";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_INACTIVE = "GPS Spento: attivalo nelle opzioni dell'apparecchio";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXING = STATUS_MESSAGE_GPS_STATUS_MESSAGE_OK+" cerco la posizione";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXED = STATUS_MESSAGE_GPS_STATUS_MESSAGE_OK+" posizione trovata";
	
	public final static int TAG_ID_VIEW_ID = 0;
	public final static int TAG_ID_NOME_CAMPO = 1;
	
	
	
    public final static String SOAP_ENDPOINT ="https://webapps.comune.trento.it/parcogiochiSrv/";
    public final static String SOAP_NAMESPACE = "http://gioco.parcogiochi";
    public final static String SOAP_URL = "https://webapps.comune.trento.it/parcogiochiSrv/services/SrvGioco?wsdl";

    /**
     * Mapping dei codici struttura per come se li aspetta in firma il server
     * Validi anche per sttare il tipo di foto
     */
    public static final int CODICE_STRUTTURA_GIOCO = 1;
    public static final int CODICE_STRUTTURA_AREA = 2;
    public static final int CODICE_STRUTTURA_VERIFICA = 3;
    public static final int CODICE_STRUTTURA_MANUTENZIONE = 4;
    public static final int CODICE_STRUTTURA_CONTROLLO_VISIVO = 5;
    public static final int CODICE_STRUTTURA_INTERVENTO = 6; 
    
	public static final int FOTO_REQUEST_CODE = 1;
	public static final int SOAP_GET_GIOCO_REQUEST_CODE = 2;
	public static final int SOAP_GET_GIOCO_FOTO_REQUEST_CODE = 3;
	public static final int CREDENTIALS_UPDATED_REQUEST_CODE = 4;
	

	public static final int SOAP_SINCRONIZZA_TUTTO_REQUEST_CODE = 90;
	public static final int SOAP_GET_TABELLA_REQUEST_CODE = 91;

	public static final int SOAP_SERVICE_INFO_REQUEST_CODE = 100;	
	public static final int SOAP_GET_GIOCO_REQUEST_CODE_BY_ID = 101;
	public static final int SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID = 102;
	public static final int SOAP_GET_AREA_REQUEST_CODE_BY_ID = 103;
	public static final int SOAP_GET_AREA_REQUEST_CODE_BY_RFID = 104;

	public static final int SOAP_GET_TOKEN_REQUEST_CODE = 200;
	public static final String SOAP_EXCEPTION_ARKAUT_TOKEN_SCADUTO = "AUT104";
	public static final int SOAP_GET_TOKEN_STATUS_REQUEST_CODE = 201;
	public static final int SOAP_GET_INVALIDATE_TOKEN_REQUEST_CODE = 202;

	public static final int LEGGI_RFID_DA_LETTORE_ESTERNO = 999;
	public static final int LEGGI_RFID_AREA_DA_LETTORE_ESTERNO_E_LEGALO_A_GIOCO = 998;
	public static final int LEGGI_RFID_DA_LETTORE_ESTERNO_E_LEGALO_A_STRUTTURA = 997;
	
	
}
