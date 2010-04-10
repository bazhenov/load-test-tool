package com.farpost.ldt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpTask {

	private URL url;

	public void setUrl(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public void get() throws IOException {
		URLConnection connection = url.openConnection();
		InputStream stream = connection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		try {
			while (in.readLine() != null);
		} finally {
			in.close();
		}
	}
}
