package com.hazeluff.discord.canucksbot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazeluff.discord.canucksbot.Config;

public class HttpUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

	public static String get(URI uri) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = null;
		int retries = Config.HTTP_REQUEST_RETRIES;
		do {
			try {
				response = client.execute(request);
			} catch (IOException e) {
				LOGGER.error("Failed to request page [" + uri.toString() + "]", e);
			}
		} while ((response == null || response.getStatusLine().getStatusCode() != 200) && retries > 0);
		if (retries <= 0) {	
			String message = "Failed to get page after (" + Config.HTTP_REQUEST_RETRIES + ") retries.";
			LOGGER.error(message);
			throw new RuntimeException(message);
		}

		BufferedReader rd;
		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} catch (UnsupportedOperationException | IOException e) {
			LOGGER.error("Error reading response");
			throw new RuntimeException(e);
		}

	}
}
