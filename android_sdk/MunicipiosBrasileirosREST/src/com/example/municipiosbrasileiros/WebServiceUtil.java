package com.example.municipiosbrasileiros;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

import android.os.Build;

public class WebServiceUtil {

	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int DATARETRIEVAL_TIMEOUT = 10000;
	
	public static String requestWebService(String serviceUrl) {
	    disableConnectionReuseIfNecessary();
	 
	    HttpURLConnection urlConnection = null;
	    try {
	        // create connection
	        URL urlToRequest = new URL(serviceUrl);
	        urlConnection = (HttpURLConnection) 
	            urlToRequest.openConnection();
	        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
	        urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);
	         
	        // handle issues
	        int statusCode = urlConnection.getResponseCode();
	        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
	            // handle unauthorized (if service requires user login)
	    		System.err.println("HTTP_UNAUTHORIZED");
	        } else if (statusCode != HttpURLConnection.HTTP_OK) {
	            // handle any other errors, like 404, 500,..
	    		System.err.println("HTTP ERROR");
	        }
	         
	        // create JSON object from content
	        InputStream in = new BufferedInputStream(
	        		urlConnection.getInputStream());
	        return getResponseText(in);
	         
	    } catch (MalformedURLException e) {
	        // URL is invalid
    		System.err.println("URL is invalid");
	    } catch (SocketTimeoutException e) {
	        // data retrieval or connection timed out
    		System.err.println("SocketTimeoutException");
	    } catch (IOException e) {
	        // could not read response body 
	        // (could not create input stream)
    		System.err.println("IOException");
	    } finally {
	        if (urlConnection != null) {
	            urlConnection.disconnect();
	        }
	    }       
	     
	    return null;
	}
	 
	/**
	 * required in order to prevent issues in earlier Android version.
	 */
	private static void disableConnectionReuseIfNecessary() {
	    // see HttpURLConnection API doc
	    if (Integer.parseInt(Build.VERSION.SDK) 
	            < Build.VERSION_CODES.FROYO) {
	        System.setProperty("http.keepAlive", "false");
	    }
	}
	 
	private static String getResponseText(InputStream inStream) {
	    // very nice trick from 
	    // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	    return new Scanner(inStream).useDelimiter("\\A").next();
	}
	
}
