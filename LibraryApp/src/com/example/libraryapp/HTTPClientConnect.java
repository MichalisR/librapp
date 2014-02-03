/* Author: 	Michalis Rodios
 * File:   	HTTPClientConnect.java
 * Purpose:	Connects with remote server and serves http requests
 */
package com.example.libraryapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
 
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class HTTPClientConnect 
{

	//Sets the time until the client times out (30 sec)
	public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds 
	//Instance of the HTTP Client
	private static HttpClient mHttpClient;
	
	/* Create an HTTP client instance with default parameters
	 * Return the instance with parameters set 
	 */
	private static HttpClient getHttpClient() 
	{
		if (mHttpClient == null) 
		{
			mHttpClient = new DefaultHttpClient();
		   
			final HttpParams params = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
		}
		return mHttpClient;
	}
	
	/* Creates an hhtp post request to the given url
	 * Parameters: url + parameters
	 * Results: the result from the php file
	 * Throws exception
	 */
	public static String executeHttpPost(String url,ArrayList<NameValuePair> postParameters) throws Exception 
	{
		BufferedReader in = null;

		try 
		{
			HttpClient client = getHttpClient();
	
			HttpPost request = new HttpPost(url);
	
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
	
			request.setEntity(formEntity);
	
			HttpResponse response = client.execute(request);
	
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	
			StringBuffer sb = new StringBuffer("");
	
			String line = "";
	
			String NL = System.getProperty("line.separator");
	
			while ((line = in.readLine()) != null) 
			{
				sb.append(line + NL);
			}
	
			in.close();
	
			String result = sb.toString();
	
			return result;
	
		} finally 
		{
			if (in != null) 
			{

				try 
				{
				
					in.close();

				} 
				catch (IOException e) 
				{

					Log.e("log_tag", "Error converting result "+e.toString()); 

					e.printStackTrace();

				}

			}

		}

	}
	
	/* Creates an hhtp GET request to the given url
	 * Parameters: url
	 * Results: the result from the php file
	 * Throws exception
	 */
	public static String executeHttpGet(String url) throws Exception 
	{

		  BufferedReader in = null;

		  try 
		  {

			  HttpClient client = getHttpClient();

			  HttpGet request = new HttpGet();

			  request.setURI(new URI(url));

			  HttpResponse response = client.execute(request);

			  in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		 
			  StringBuffer sb = new StringBuffer("");

			  String line = "";

			  String NL = System.getProperty("line.separator");

			  while ((line = in.readLine()) != null) 
			  {

				  sb.append(line + NL);

			  }

			  in.close();

		 
			  String result = sb.toString();

			  return result;

		  } 
		  finally 
		  {

			  if (in != null) 
			  {

				  try
				  {

					  in.close();

				  } 
				  catch (IOException e) 
				  {

					  Log.e("log_tag", "Error converting result "+e.toString()); 

					  e.printStackTrace();

				  }

			  }

		  }

	}
}
