package com.ds.birth;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.ds.kaixin.FriendInfo;
import com.ds.utility.Renren;

public class AsyncImageLoader {

	private HashMap<String, SoftReference<Drawable>> imageCache; // 缓存图片
	private static final int POST=100;
	private static final int POST2=101;
	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final Renren renren,
			final ImageCallback imageCallback) {
		final String imageId = renren.getId();
		final String imageURL = renren.getHeadurl();

		// 判断缓存中是否已经存在图片，如果存在则直接返回
		if (imageCache.containsKey(imageId)) {
			SoftReference<Drawable> softReference = imageCache.get(imageId);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageURL);
			}
		};

		// 开辟一个新线程去下载图片，并用Handler去更新UI
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageURL);
				imageCache.put(imageId, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public Drawable loadDrawable(final int id, final String imageUrl,
			final ImageCallback imageCallback) {

		// 判断缓存中是否已经存在图片，如果存在则直接返回
		if (imageCache.containsKey(String.valueOf(id))) {
			SoftReference<Drawable> softReference = imageCache.get(String.valueOf(id));
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};

		// 开辟一个新线程去下载图片，并用Handler去更新UI
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				imageCache.put(String.valueOf(id), new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}
	
	public Drawable loadDrawable(final String id,final String imageUrl,
			final ImageCallback imageCallback) {

		// 判断缓存中是否已经存在图片，如果存在则直接返回
		if (imageCache.containsKey(id)) {
			SoftReference<Drawable> softReference = imageCache.get(id);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};

		// 开辟一个新线程去下载图片，并用Handler去更新UI
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				imageCache.put(id, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public Drawable loadDrawable(final FriendInfo renren,
			final ImageCallback imageCallback) {
		final String imageId = renren.getUid();
		final String imageURL = renren.getLogoUrl();

		// 判断缓存中是否已经存在图片，如果存在则直接返回
		if (imageCache.containsKey(imageId)) {
			SoftReference<Drawable> softReference = imageCache.get(imageId);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageURL);
			}
		};

		// 开辟一个新线程去下载图片，并用Handler去更新UI
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageURL);
				imageCache.put(imageId, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}
	
	public Drawable loadDraw(String imageId,final String url){
		if(imageCache.containsKey(imageId)){
			SoftReference<Drawable> softReference = imageCache.get(imageId);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}else{
			new Thread(){
				public void run(){
					Drawable drawable = loadImageFromUrl(url);
					imageCache.put(url, new SoftReference<Drawable>(drawable));
				}
			}.start();
		}
		return null;
	}

	// 从URL下载图片
	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(i, "src");
		return d;
	}

	// 回调接口
	public interface ImageCallback {
		// 回调函数
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}
}
