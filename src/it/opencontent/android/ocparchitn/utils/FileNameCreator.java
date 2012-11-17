package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.Constants;

public class FileNameCreator {
	public static String getSnapshotFileName(int rfid, int whichOne) {
		return rfid + "_" + whichOne + ".jpg";
	}

	public static String getSnapshotFullPath(int rfid, int whichOne) {
		return Constants.IMAGE_SUBDIRECTORY + "_" + rfid + "_" + whichOne
				+ ".jpg";
	}

}
