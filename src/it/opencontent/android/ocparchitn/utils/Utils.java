package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.R;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkAutException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkGiochiException;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPSrvGiocoArkSrvException;

import org.ksoap2.serialization.KvmSerializable;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Utils {
	
	private static final String TAG = Utils.class.getSimpleName();
	private static KvmSerializable exc;
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(byte[] decoded,
			Resources res, int index, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(decoded, 0, decoded.length, options);

		// Calculate inSampleSize
		options.inSampleSize = Utils.calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.length,
				options);
		String text = "Foto " + index;
		if (bmp == null) {
			bmp = new DrawableOverlayWriter().writeOnDrawable(res,
					R.drawable.snapshot_teaser, text).getBitmap();
		} else {
			bmp = new DrawableOverlayWriter().writeOnDrawable(res,
					bmp.copy(Bitmap.Config.ARGB_8888, true), text, 70, 50)
					.getBitmap();
		}

		return bmp;
	}
	
	public static KvmSerializable parseExceptionNode(Node node) {
		exc = null;
        int count  = node.getChildCount();
        for(int i = 0; i < count; i++){
        	Element n = node.getElement(i);
        	digIntoNode(n);
        }
        return exc;
	}
	
	private static void setExc(KvmSerializable e){
		exc = e;
		Log.d(TAG,exc.getClass().getSimpleName());
	}
	
	public static void digIntoNode(Element element){
		if(element.getName().equals("ArkAutException")
				||element.getName().equals("ArkGiochiException")
				||element.getName().equals("ArkSrvException")){		
			
			
			if(element.getName().equals("ArkAutException")){
				SOAPSrvGiocoArkAutException exceptionClass = new SOAPSrvGiocoArkAutException();
				Log.d(TAG,element.getName());
				for (int i = 0; i < element.getChildCount(); i++){
					Element e = element.getElement(i);
					if(e.getName().equals("message")){
						exceptionClass.message = e.getText(0);
					}
					if(e.getName().equals("codice")){
						exceptionClass.codice = e.getText(0);
					}
					if(e.getName().equals("level")){
						exceptionClass.level = Integer.parseInt(e.getText(0));
					}
					Log.d(TAG," "+e.getText(0));
				}
				setExc(exceptionClass);
			} else if(element.getName().equals("ArkGiochiException")){
				SOAPSrvGiocoArkGiochiException exceptionClass = new SOAPSrvGiocoArkGiochiException();
				for (int i = 0; i < element.getChildCount(); i++){
					Element e = element.getElement(i);
					if(e.getName().equals("message")){
						exceptionClass.message = e.getText(0);
					}
					if(e.getName().equals("codice")){
						exceptionClass.codice = e.getText(0);
					}
					if(e.getName().equals("level")){
						exceptionClass.level = Integer.parseInt(e.getText(0));
					}
					Log.d(TAG," "+e.getText(0));
				}
				setExc(exceptionClass);
			} else {
				SOAPSrvGiocoArkSrvException exceptionClass = new SOAPSrvGiocoArkSrvException();
				for (int i = 0; i < element.getChildCount(); i++){
					Element e = element.getElement(i);
					if(e.getName().equals("message")){
						exceptionClass.message = e.getText(0);
					}
					Log.d(TAG," "+e.getText(0));
				}	
				setExc(exceptionClass);
			}
			
		} else if(element.getChildCount()>0) {
			digIntoNode(element.getElement(0));
		} else{
			
		}
	}
	
}
