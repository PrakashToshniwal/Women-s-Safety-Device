package com.example.torch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class HTTPConnectionTask extends AsyncTask<String, Void, String> {

	public static HttpURLConnection con;
	public static InputStream is;
	public static OutputStream outs;
	public static Socket socket;
	
	@Override
	protected String doInBackground(String... params) 
	{
		String url = params[0];
		String method = params[1];
		String data = sendHttpRequest(url, method);
		return data;
	}

	private String sendHttpRequest(String url, String method) 
	{
		if(method.equalsIgnoreCase("Connect"))
		{
			try
			{
				/*con= (HttpURLConnection) ( new URL(url)).openConnection();
				con.setRequestMethod("POST");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();
				HttpResponse response = con.getResponseCode();
				con.getOutputStream().write( ("Connected to XT1052").getBytes());
				InputStream is = con.getInputStream();
				StringBuilder buffer = new StringBuilder();
				byte[] b = new byte[1024];
				while ( is.read(b) != -1)
				  buffer.append(new String(b));
				con.disconnect();
				return buffer.toString();
				*/
				
		        //is = con.getInputStream();
		        //outs = con.getOutputStream();
		        // Convert the InputStream into a string
		        //int status = con.getResponseCode();
		        //String contentAsString = readIt(is, len);
		        
		        InetAddress serverAddr = InetAddress.getByName("192.168.43.60");

				socket = new Socket(serverAddr, 81);
				is = socket.getInputStream();
				BufferedReader mBufferIn = new BufferedReader(new InputStreamReader(is));
				String mServerMessage = mBufferIn.readLine();
				return mServerMessage;
				
			}
			catch(Exception e)
			{
				return "Error in connection";
			}
			
		}
		else
		{
			try
			{
				socket.close();
				return "Disconnected";
			}
			catch(Exception e)
			{
				return "Error in disconnection";
			}
		}
	}
	@Override
	protected void onPostExecute(String result) 
	{
		HTTPRequestActivity.responseText.setText(result);
    }
	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    reader.read(buffer);
	    return new String(buffer);
	}
}
