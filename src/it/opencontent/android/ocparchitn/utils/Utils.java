package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {
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
}
