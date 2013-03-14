package it.opencontent.android.ocparchitn.utils;

import java.io.File;

import android.os.Environment;

public class FileNameCreator {


	public static File getSnapshotTempPath(){
		try{
		File publicDir = new File(Environment.getExternalStoragePublicDirectory(
		        Environment.DIRECTORY_PICTURES
		    ), "ocParchi");
		publicDir.mkdirs();
		return publicDir;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
