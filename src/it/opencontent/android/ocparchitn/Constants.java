package it.opencontent.android.ocparchitn;

public class Constants {
	public static final String IMAGE_SUBDIRECTORY = "images";

	public final static String TAKE_SNAPSHOT = "it.opencontent.android.ocparchitn.CameraAction";
	
	public final static String EXTRAKEY_DATAMAP = "dataMap";
	public final static String EXTRAKEY_METHOD_NAME = "methodName";
	
	public final static String EXTRAKEY_FOTO_NUMBER = "fotoNumber";
	public final static String EXTRAKEY_RFID = "rfid";
	public final static String EXTRAKEY_SYNC_ALL = "sync_all";
	
	public final static int MAX_SNAPSHOTS_AMOUNT = 5;
	
	public final static int MAX_UNSYNCHRONIZED_LOCAL_ENTITIES = 10;
	
	public final static int GPS_BEACON_INTERVAL = 5000;
	public final static int GPS_METER_THRESHOLD = 0;
	
	public final static String STATUS_MESSAGE_SERVER_STATUS = "server_status";
	public final static String STATUS_MESSAGE_GPS_STATUS = "server_status";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_OK = "GPS OK:";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_INACTIVE = "GPS Spento: attivalo nelle opzioni dell'apparecchio";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXING = STATUS_MESSAGE_GPS_STATUS_MESSAGE_OK+" cerco la posizione";
	public final static String STATUS_MESSAGE_GPS_STATUS_MESSAGE_FIXED = STATUS_MESSAGE_GPS_STATUS_MESSAGE_OK+" posizione trovata";
	
}
