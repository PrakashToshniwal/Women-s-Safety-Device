package com.example.torch;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HTTPRequestActivity extends Activity {


	public static Button ConnectButton;
	public static Button sendButton;
	public static TextView responseText;
	public static EditText dataText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_httprequest);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		sendButton = (Button) findViewById(R.id.sendButton);
		responseText = (TextView) findViewById(R.id.ResponsetextView);
		ConnectButton = (Button) findViewById(R.id.connectButton);
		dataText = (EditText) findViewById(R.id.dataText);
		sendButton.setEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.httprequest, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void SendClick(View view)
	{
		
		new HTTPSendTask().execute(dataText.getText().toString());
	}
	public void ConnectClick(View view)
	{
		EditText addressTextBox =(EditText) findViewById(R.id.addressText);
		Button connectButton = (Button) view;
		if(((String) connectButton.getText()).equalsIgnoreCase("Connect"))
		{
			new HTTPConnectionTask().execute(addressTextBox.getText().toString(),"Connect");
			ConnectButton.setText("Disconnect");
			sendButton.setEnabled(true);
			dataText.setEnabled(true);
		}
		else
		{
			new HTTPConnectionTask().execute(null,"Disconnect");
			ConnectButton.setText("Connect");
			sendButton.setEnabled(false);
			dataText.setEnabled(false);
		}
		/*if(((String) connectButton.getText()).equalsIgnoreCase("Connect"))
		{
			try
			{
				URL url = new URL(addressTextBox.getText().toString());
				con= (HttpURLConnection)url.openConnection();
				con.setRequestMethod("POST");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.connect();
				sendButton.setEnabled(true);
				connectButton.setText("Disconnect");
			}
			catch(Exception e)
			{
				responseText.setText("Error in Connection");
				sendButton.setEnabled(false);
			}
		}
		else
		{
			try
			{
				con.disconnect();
				sendButton.setEnabled(false);
			}
			catch(Exception e)
			{
				responseText.setText("Error in Disconnection");
				sendButton.setEnabled(true);
			}
		}*/
			
	}
}
