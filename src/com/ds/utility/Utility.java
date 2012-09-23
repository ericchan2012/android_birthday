package com.ds.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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

	public static final String FEEDBACK_URL = "http://360birthday.sinaapp.com/feedback.php?type=1";
	public static final String FEEDBACK_QUERY_URL = "http://360birthday.sinaapp.com/feedback.php?type=0";
	public static final String DOWNLOAD_URI="http://360birthday.sinaapp.com/download.php?userid=";
	
	public static final String REGISTER_URI="http://360birthday.sinaapp.com/register.php?";
	
	public static final String BACKUP_URI="http://360birthday.sinaapp.com/receive_file.php";
	
	public static final String LOGIN_URI="http://360birthday.sinaapp.com/login.php?";

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

	public static boolean isHasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static Bitmap getBitmapFromUri(Uri uri, Context context) {
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

	public static String getJSONData(String url)
			throws ClientProtocolException, IOException {
		String result = "";
		HttpGet httpGet = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = null;

		try {
			httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				result = convertStreamToString(inputStream);

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			throw e;
		} finally {
			httpClient.getConnectionManager().shutdown();
			httpResponse = null;
		}
		return result;

	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "GB2312"),// 防止模拟器上的乱码
					512 * 1024);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "/n");
			}
		} catch (IOException e) {
			Log.e("DataProvier convertStreamToString", e.getLocalizedMessage(),
					e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
