package com.ds.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownload {
	/**
	 * 功能：下载文件到指定目录下，成功返回1<BR>
	 * 日期：2012-4-5<BR>
	 * 作者：裴江平
	 */
	public static int downLoadFile(String httpUrl, String fileName, String path) {
		FileOutputStream fos = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		// 当存放文件的文件目录不存在的时候创建文件目录
		File tmpFile = new File(path);
		if (!tmpFile.exists()) {
			tmpFile.mkdir();
		}
		// 获取文件对象
		File file = new File(path + "/" + fileName);
		try {
			URL url = new URL(httpUrl);
			try {
				conn = (HttpURLConnection) url.openConnection();
				is = conn.getInputStream();// 获得http请求返回的InputStream对象。
				fos = new FileOutputStream(file);// 获得文件输出流对象来写文件用的
				byte[] buf = new byte[2048];
				conn.connect();// http请求服务器
				double count = 0;
				// http请求取得响应的时候
				if (conn.getResponseCode() >= 400) {
					System.out.println("nono");
					return 0;
				} else {
					while (count <= 100) {
						if (is != null) {
							int numRead = is.read(buf);
							System.out.println("numRead:" + numRead);
							if (numRead <= 0) {
								break;
							} else {
								fos.write(buf, 0, numRead);
							}

						} else {
							break;
						}
					}
				}
				conn.disconnect();
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			} finally {
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					fos = null;
				}
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					is = null;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
}
