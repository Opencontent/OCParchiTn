package it.opencontent.android.ocparchitn;

public class Constants {
	public static final String IMAGE_SUBDIRECTORY = "images";

	public final static String TAKE_SNAPSHOT = "it.opencontent.android.ocparchitn.CameraAction";
	
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
	
	public final static String EXTRAKEY_FOTO_NUMBER = "fotoNumber";
	public final static String EXTRAKEY_RFID = "rfid";
	public final static String EXTRAKEY_SYNC_ALL = "sync_all";
	
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
}
