package net.cleverleo.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class C {

	private Connection con;
	private static final int MAXTRY = 3;
	private static String UserAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/1A542a Safari/419.3";
	private static final int TIMEOUT = 3000;
	private String url = "";

	public Map<String, String> cookies = new HashMap<String, String>();

	public C(String url) {
		this.url = url;
		this.con = Jsoup.connect(url);
		this.con.timeout(C.TIMEOUT);
		this.con.userAgent(C.UserAgent);
	}

	public C setData(Map<String, String> data) {
		this.con.data(data);
		return this;
	}

	public C setData(String key, String value) {
		this.con.data(key, value);
		return this;
	}

	public C setUrl(String url) {
		this.url = url;
		this.con.url(url);
		return this;
	}

	public Document post() throws IOException {
		Document doc = null;
		if (this.cookies != null)
			this.con.cookies(this.cookies);

		for (int i = 0; i < C.MAXTRY; i++) {
			try {
				doc = this.con.post();
				break;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				if (i == C.MAXTRY - 1) {
					throw e;
				}
				continue;
			}
		}

		this.cookies = this.con.response().cookies();

		return doc;
	}

	public Document get() throws IOException {
		Document doc = null;
		if (this.cookies != null)
			this.con.cookies(this.cookies);

		for (int i = 0; i < C.MAXTRY; i++) {
			try {
				doc = this.con.get();
				break;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				if (i == C.MAXTRY - 1) {
					throw e;
				}
				continue;
			}
		}

		this.cookies = this.con.response().cookies();

		return doc;
	}

	public JSONObject getJsonObject() throws IOException {
		JSONObject result = null;
		for (int i = 0; i < C.MAXTRY; i++) {
			try {
				URL ourl = new URL(this.url);
				HttpURLConnection conn = (HttpURLConnection) ourl
						.openConnection();
				conn.setConnectTimeout(C.TIMEOUT);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "*/*");
				conn.setRequestProperty("Accept-Language", "zh-CN");
				conn.setRequestProperty("Charset", "UTF-8");
				conn.setRequestProperty("Connection", "keep-alive");
				conn.setRequestProperty("Cookie", this.getCookieText());
				conn.setRequestProperty("User-Agent", C.UserAgent);
				result = new JSONObject(new JSONTokener(conn.getInputStream()));
				break;
			} catch (JSONException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				break;
			} catch (MalformedURLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				break;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				if (i == C.MAXTRY - 1)
					throw e;
				continue;
			}
		}
		return result;

	}

	public JSONArray getJsonArray() throws IOException {
		JSONArray result = null;
		for (int i = 0; i < C.MAXTRY; i++) {
			try {
				URL ourl = new URL(this.url);
				HttpURLConnection conn = (HttpURLConnection) ourl
						.openConnection();
				conn.setConnectTimeout(C.TIMEOUT);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "*/*");
				conn.setRequestProperty("Accept-Language", "zh-CN");
				conn.setRequestProperty("Charset", "UTF-8");
				conn.setRequestProperty("Connection", "keep-alive");
				conn.setRequestProperty("Cookie", this.getCookieText());
				conn.setRequestProperty("User-Agent", C.UserAgent);
				result = new JSONArray(new JSONTokener(conn.getInputStream()));
				break;
			} catch (JSONException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				break;
			} catch (MalformedURLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				break;
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				if (i == C.MAXTRY - 1)
					throw e;
				continue;
			}
		}
		return result;
	}

	public String getCookieText() {
		Iterator<String> it = this.cookies.keySet().iterator();
		String result = "";
		while (it.hasNext()) {
			String key = it.next();
			String value = this.cookies.get(key);
			result += String.format("%s=%s; ", key, value);
		}

		return result;
	}

	@SuppressWarnings("resource")
	public String downloadFile(String imgUrl, String path) throws IOException {
		RandomAccessFile file = null;
		HttpURLConnection conn = null;

		URL ourl = new URL(imgUrl);
		conn = (HttpURLConnection) ourl.openConnection();
		conn.setConnectTimeout(3000);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Cookie", this.getCookieText());

		int filesize = conn.getContentLength();
		if ((new File(path)).isDirectory()) {
			String[] oFilename_arr = conn.getURL().getPath().split("/", 0);

			path += "/" + oFilename_arr[oFilename_arr.length - 1];
		}
		file = new RandomAccessFile(path, "rw");
		file.setLength(filesize);// 获取文件大小

		InputStream inStream = conn.getInputStream();
		byte[] buffer = new byte[5000];
		int hasdown = 0;
		int hasRead = 0;
		// 下载开始
		while (hasdown < filesize && (hasRead = inStream.read(buffer)) != -1) {
			file.write(buffer, 0, hasRead);
			hasdown += hasRead;
		}
		if (filesize == hasdown) {
			return path;
		}

		return null;
	}

	public String postFile(Part[] part) {
		String URLString = this.url;
		try {
			StringBuffer response = new StringBuffer();

			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(URLString);

			// 设置Http Post数据，这里是上传文件
			method.setRequestHeader("Cookie", this.getCookieText());
			MultipartRequestEntity data = new MultipartRequestEntity(part,
					method.getParams());
			method.setRequestEntity(data);
			try {
				client.executeMethod(method); // 这一步就把文件上传了
				// 下面是读取网站的返回网页，例如上传成功之类的
				if (method.getStatusCode() == HttpStatus.SC_OK) {
					// 读取为 InputStream，在网页内容数据量大时候推荐使用
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(
									method.getResponseBodyAsStream()));
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					reader.close();
				}
			} catch (IOException e) {
				System.out.println("执行HTTP Post请求" + URLString + "时，发生异常！");
				e.printStackTrace();
			} finally {
				method.releaseConnection();
			}
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public C setUserAgent(String ua) {
		C.UserAgent = ua;
		return this;
	}
}
