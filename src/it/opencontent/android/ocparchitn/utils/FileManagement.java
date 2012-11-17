package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.Constants;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;

public class FileManagement {

	public static boolean storeImage(Bitmap finalBitmap, String fname) {
		File myDir = new File(Constants.BASE_PATH
				+ Constants.IMAGE_SUBDIRECTORY);
		myDir.mkdirs();

		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
