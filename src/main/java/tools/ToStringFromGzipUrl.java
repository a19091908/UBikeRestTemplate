package tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class ToStringFromGzipUrl {

	private URL url;
	private char[] buffer = new char[1024];
	
	public ToStringFromGzipUrl(URL url){
		this.url=url;		
	}
	
	/*
	 * 將會下載.gz的URL轉成JSON String
	 */
	public String gzipUrlToString(){
		
		StringBuilder jsonStringBuilder = new StringBuilder();
		try {
			GZIPInputStream gzis = new GZIPInputStream(url.openStream());
			Reader rd = new InputStreamReader(gzis, "UTF-8");
			for (;;) {
				int rsz = rd.read(buffer, 0, buffer.length);
				if (rsz < 0) {
					break;
				}
				jsonStringBuilder.append(buffer, 0, rsz);
			}
			gzis.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return jsonStringBuilder.toString();
	}
}
