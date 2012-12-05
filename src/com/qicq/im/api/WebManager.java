package com.qicq.im.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class WebManager {

	private String cookie = null;
	protected String addr = null;

	public WebManager(String addr) {
		this.addr = addr;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getCookie() {
		return cookie;
	}

	private void parseCookie(HttpURLConnection conn) {
		for (int i = 1;; i++) {
			String v = conn.getHeaderField(i);
			String k = conn.getHeaderFieldKey(i);
			if (v == null && k == null)
				break;
			// Log.v(k,v);
			if (k.equalsIgnoreCase("set-cookie")) {
				cookie = v.substring(0, v.indexOf(';'));
				Log.v("WebManager", v);
				Log.v("WebManager", cookie);
				break;
			}
		}
	}

	private HttpURLConnection initConn(String addr) throws IOException {
		URL url;
		url = new URL(addr);
		HttpURLConnection conn;
		conn = (HttpURLConnection) url.openConnection();

		if (cookie != null)
			conn.setRequestProperty("Cookie", cookie);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Accept-Charset:", "UTF-8");
		return conn;
	}

	public String GetData(String hostAddr) {
		try {
			HttpURLConnection conn = initConn(hostAddr);

			conn.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String tmp = "";
			String line;
			while ((line = reader.readLine()) != null) {
				tmp += line;
			}

			parseCookie(conn);

			reader.close();
			conn.disconnect();

			return tmp;
		} catch (IOException e) {
			Log.e("WebManager",e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}

	public String PostData(String hostAddr, String data) {
		try {
			Log.v("PostData", data);

			HttpURLConnection conn = initConn(hostAddr);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			conn.connect();

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());

			out.writeBytes(data);
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));

			parseCookie(conn);

			String tmp = "";
			String line;
			while ((line = reader.readLine()) != null) {
				tmp += line;
			}

			reader.close();
			conn.disconnect();
			
			return tmp;
		} catch (IOException e) {
			Log.e("WebManager",e.getMessage());
			//e.printStackTrace();
		}
		return null;

	}

	private static final String BOUNDARY = "----WebKitFormBoundarySjgczspGPr8v27W4";

	public String UploadFile(String hostAddr, String filename,
			String contentType) {
		try {
			HttpURLConnection conn = initConn(hostAddr);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			conn.connect();

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			File file = new File(filename);

			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"file\";filename=\"");
			sb.append(filename);
			sb.append("\"\r\n");
			sb.append("Content-Type:");
			sb.append(contentType);
			sb.append("\r\n\r\n");

			byte[] data = sb.toString().getBytes();

			out.write(data);

			DataInputStream in = new DataInputStream(new FileInputStream(file));

			int bytes = 0;

			byte[] bufferOut = new byte[1024];

			while ((bytes = in.read(bufferOut)) != -1) {

				out.write(bufferOut, 0, bytes);

			}
			in.close();

			sb = new StringBuilder();
			sb.append("\r\n");
			sb.append("\r\n--");
			sb.append(BOUNDARY);
			sb.append("--\r\n");
			out.write(sb.toString().getBytes());

			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));

			parseCookie(conn);

			String tmp = "";
			String line;
			while ((line = reader.readLine()) != null) {
				tmp += line;
			}

			reader.close();
			conn.disconnect();

			return tmp;
		} catch (IOException e) {
			Log.e("WebManager",e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}

	public boolean DownloadFile(String hostAddr, String filename) {
		try {
			HttpURLConnection conn = initConn(hostAddr);

			conn.connect();

			InputStream in = conn.getInputStream();

			File file = new File(filename);

			@SuppressWarnings("resource")
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					file));

			byte[] buffer = new byte[1024];
			int bytes = 0;

			while ((bytes = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes);
			}

			out.write(buffer);
			Log.v("File saved", filename);

			parseCookie(conn);

			conn.disconnect();

			return true;
		} catch (IOException e) {
			Log.e("WebManager",e.getMessage());
			//e.printStackTrace();
		}
		return false;
	}
}
