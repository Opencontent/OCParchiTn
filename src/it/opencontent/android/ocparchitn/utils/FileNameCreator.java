package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.Constants;

import java.io.File;

import android.os.Environment;

public class FileNameCreator {


	public static File getSnapshotTempPath(){
		try{
		File publicDir = Environment.getExternalStoragePublicDirectory(
		        Environment.DIRECTORY_PICTURES
		    ); 
		return publicDir;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
