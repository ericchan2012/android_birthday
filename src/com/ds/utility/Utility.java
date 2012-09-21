package com.ds.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class Utility {

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static long getDays(String begin, String end) {

		long iDay = -1;
		Date date;
		try {
			date = (new SimpleDateFormat("yyyy-MM-dd")).parse(begin);
			long lBeginTime = date.getTime();
			Date td2 = (new SimpleDateFormat("yyyy-MM-dd")).parse(end);
			long lEndTime = td2.getTime();
			if (lBeginTime > lEndTime) {
				iDay = ((lBeginTime - lEndTime) / (24 * 60 * 60 * 1000));
			} else {
				iDay = ((lEndTime - lBeginTime) / (24 * 60 * 60 * 1000));
			}
			return iDay;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return iDay;
	}

	public static int getAge(Date birthDay) throws Exception {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			throw new IllegalArgumentException("出生时间大于当前时间!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;// 注意此处，如果不加1的话计算结果是错误的
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		Log.i("Utility", "yearNow:" + yearNow + " yearBirth:" + yearBirth);

		// if (monthNow <= monthBirth) {
		// if (monthNow == monthBirth) {
		// // monthNow==monthBirth
		// if (dayOfMonthNow < dayOfMonthBirth) {
		// age--;
		// } else {
		// // do nothing
		// }
		// } else {
		// // monthNow>monthBirth
		// age--;
		// }
		// } else {
		// // monthNow<monthBirth
		// // donothing
		// }

		return age;
	}

	public static int getLunarAge(Date birthDay, Date now) throws Exception {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthDay)) {
			throw new IllegalArgumentException("出生时间大于当前时间!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;// 注意此处，如果不加1的话计算结果是错误的
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		// if (monthNow <= monthBirth) {
		// if (monthNow == monthBirth) {
		// // monthNow==monthBirth
		// if (dayOfMonthNow < dayOfMonthBirth) {
		// age--;
		// } else {
		// // do nothing
		// }
		// } else {
		// // monthNow>monthBirth
		// age--;
		// }
		// } else {
		// // monthNow<monthBirth
		// // donothing
		// }

		return age;
	}
	
	
	public static boolean isHasSdcard()  
	{  
	    String status = Environment.getExternalStorageState();  
	    if (status.equals(Environment.MEDIA_MOUNTED)){  
	       return true;  
	    } else {  
	       return false;  
	    }  
	}
	
	public static Bitmap getBitmapFromUri(Uri uri,Context context) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
