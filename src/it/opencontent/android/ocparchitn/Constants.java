package it.opencontent.android.ocparchitn;

public class Constants {
	public static final String IMAGE_SUBDIRECTORY = "images";

	public final static String TAKE_SNAPSHOT = "it.opencontent.android.ocparchitn.CameraAction";
	
	public final static int PERMESSO_NONE = 0;
	public final static int PERMESSO_VISUALIZZA = 15;
	public final static int PERMESSO_MODIFICA = 32;
	public final static int PERMESSO_ADMIN = 1000;
	
	public final static String EXTRAKEY_DATAMAP = "dataMap";
	public final static String EXTRAKEY_DATAMAP_RESULT = "dataMapResult";
	public final static String EXTRAKEY_METHOD_NAME = "methodName";
	public final static String GET_GIOCO_METHOD_NAME = "getGioco";
	public final static String GET_GIOCO_ID_METHOD_NAME = "getGiocoId";
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
	
	public final static int[] ID_TABELLE_SUPPORTO = new int[]{1,2,5,6,7,8,9,10};
	
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

	public static final int FOTO_REQUEST_CODE = 1;
	public static final int SOAP_GET_GIOCO_REQUEST_CODE = 2;
	public static final int SOAP_GET_GIOCO_FOTO_REQUEST_CODE = 3;
	public static final int CREDENTIALS_UPDATED_REQUEST_CODE = 4;
	

	public static final int SOAP_SINCRONIZZA_TUTTO_REQUEST_CODE = 90;
	public static final int SOAP_GET_TABELLA_REQUEST_CODE = 91;

	public static final int SOAP_SERVICE_INFO_REQUEST_CODE = 100;	
	public static final int SOAP_GET_GIOCO_REQUEST_CODE_BY_ID = 101;
	public static final int SOAP_GET_GIOCO_REQUEST_CODE_BY_RFID = 102;

	public static final int SOAP_GET_TOKEN_REQUEST_CODE = 200;
	public static final int SOAP_GET_TOKEN_STATUS_REQUEST_CODE = 201;
	public static final int SOAP_GET_INVALIDATE_TOKEN_REQUEST_CODE = 202;


}
