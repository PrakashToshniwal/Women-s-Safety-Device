package com.example.torch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.AsyncTask;

public class HTTPSendTask extends AsyncTask <String, Void, String>{

	Socket connectedSocket;
	OutputStream os;
	InputStream is;
	@Override
	protected String doInBackground(String... params) 
	{
		String data = params[0];
		String result = HTTPSendData(data);
		return result;
	}
	
	private String HTTPSendData(String data)
	{
		String mServerMessage = null;
		try
		{
			connectedSocket = HTTPConnectionTask.socket;
			os = connectedSocket.getOutputStream();
			is = connectedSocket.getInputStream();
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)), true);
			out.println(data);
			BufferedReader mBufferIn = new BufferedReader(new InputStreamReader(is));
			while(true)
			{
				mServerMessage = mBufferIn.readLine();
				if(mServerMessage!=null)
					break;
			}
			return mServerMessage;
		}
		catch( Exception e)
		{
			return "Data not sent";
		}
		
	}
	
	@Override
	protected void onPostExecute(String result) 
	{
		HTTPRequestActivity.responseText.setText(result);
    }

}
