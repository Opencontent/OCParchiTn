package it.opencontent.android.ocparchitn.utils;

import android.os.Environment;

import java.io.File;

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

    public static String getManualsTempPath(String path){
		try{
		File publicDir = new File(Environment.getExternalStoragePublicDirectory(
		        Environment.DIRECTORY_DOWNLOADS
		    ), "ocParchi");
		publicDir.mkdirs();

        File newFile = new File(publicDir.getPath()+path);
        File newFileParent = new File(newFile.getParent());
        newFileParent.mkdirs();

		return newFile.getPath();
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
