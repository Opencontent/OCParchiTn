package it.opencontent.android.ocparchitn.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;

public class DrawableOverlayWriter {

	public BitmapDrawable writeOnDrawable(Resources res, Bitmap bm,
			String text, float x, float y) {
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setTextSize(20);

		Canvas canvas = new Canvas(bm);
		canvas.drawText(text, x, y, paint);

		return new BitmapDrawable(res, bm);
	}

	public BitmapDrawable writeOnDrawable(Resources res, int drawableId,
			String text) {
		Bitmap bm = BitmapFactory.decodeResource(res, drawableId).copy(
				Bitmap.Config.ARGB_8888, true);
		return writeOnDrawable(res, bm, text, 0, 15);
	}
}
