package com.example.torch;

import java.util.Date;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class CountdownActivity extends Activity {
	
	public NotificationCompat.Builder alarmNotif;
	public static CountDownTimer alarmCountDown;
	private static boolean alarmSet = false;
	protected static final String HOUR = null;
	protected static final String MIN = null;
	private static BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 5;
	public static Camera cam = null;
	Thread t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countdown);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		TimePicker newAlarmTime = (TimePicker)findViewById(R.id.alarmTimePicker);
		newAlarmTime.setIs24HourView(true);
		Intent doCancel = getIntent();
		String alarmStatus = null;
		alarmStatus = doCancel.getStringExtra("AlarmState");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(getBaseContext(), "Bluetooth isn't supported on this device!",
	                Toast.LENGTH_SHORT).show();
		}
		if (!mBluetoothAdapter.isEnabled()) {
		    mBluetoothAdapter.enable();
		}
		if(alarmStatus!=null)
		{
			CountdownActivity.alarmSet=false;
			Intent backToMainIntent = new Intent(this, MainActivity.class);
			startActivity(backToMainIntent);
		}
	}
	
	public void onResume()
	{
		super.onResume();
		Intent doCancel = getIntent();
		String alarmStatus = null;
		alarmStatus = doCancel.getStringExtra("AlarmState");
		if(alarmStatus!=null)
		{
			CountdownActivity.alarmSet=false;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
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
	public void saveClick(View view)
	{
		CountdownActivity.alarmSet=true;
		TimePicker newAlarmTime = (TimePicker)findViewById(R.id.alarmTimePicker);
		newAlarmTime.setIs24HourView(true);
		long systime= System.currentTimeMillis();
		Date currentDate = new Date(systime);
		String formattedTime  = (String) DateFormat.format("hh:mm a", currentDate.getTime());
		int currentHour = Integer.parseInt(formattedTime.substring(0,2));
		int currentMin = Integer.parseInt(formattedTime.substring(3,5));
		String am_pm = formattedTime.substring(6);
		if(am_pm.equalsIgnoreCase("pm"))
		{
			currentHour+=12;
		}
		int alarmHour = newAlarmTime.getCurrentHour();
		int alarmMin = newAlarmTime.getCurrentMinute();
		int hourDiff;
		final int minDiff;
		if(alarmHour>=currentHour)
			hourDiff = alarmHour-currentHour;
		else
			hourDiff = alarmHour-currentHour+24;
		
		if(alarmMin>=currentMin)
			minDiff = alarmMin - currentMin;
		else
		{
			minDiff = alarmMin + 60 - currentMin;
			hourDiff= hourDiff-1;
		}
		final int HourDiff = hourDiff;
		final int alarmNotificationId = 001;
		final NotificationManager alarmNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		alarmNotif = new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.torch_icon)
			    .setContentTitle("Flash Alarm");
		
		Intent notifToMainPage = new Intent(this, MainActivity.class);

		notifToMainPage.putExtra("HOUR", newAlarmTime.getCurrentHour().toString());
		notifToMainPage.putExtra("MIN", newAlarmTime.getCurrentMinute().toString());
		PendingIntent resultPendingIntent =
			    PendingIntent.getActivity(
			    this,
			    0,
			    notifToMainPage,
			    PendingIntent.FLAG_UPDATE_CURRENT
			);

		alarmNotif.setContentIntent(resultPendingIntent);
		
		// Gets an instance of the NotificationManager service
		
		// Builds the notification and issues it.
		alarmNotifyMgr.notify(alarmNotificationId, alarmNotif.build());
		//Toast.makeText(getBaseContext(), "Time Difference is "+hourDiff+" Hours and "+minDiff+" Minutes",
        //        Toast.LENGTH_SHORT).show();
		final long timeDiffMillis = (hourDiff*60*60*1000) + (minDiff*60*1000);
   	    alarmCountDown= new CountDownTimer(timeDiffMillis, 1000) {
   	        				int timer = 0;

      	        	    	 int varMinDiff = 0, varHourDiff = 0;
   	        	     public void onTick(long millisUntilFinished) 
   	        	     {
   	        	    	 if(timer==0)
   	        	    	 {
   	        	    		 varMinDiff = minDiff;
   	        	    		 varHourDiff = HourDiff;
   	        	    	 }
   	        	    	 else if(timer==60)
   	        	    	 {
   	        	    		 varMinDiff--;
   	        	    		 if(varMinDiff==-1)
   	        	    		 {
   	        	    			 varMinDiff=59;
   	        	    			 varHourDiff--;
   	        	    		 }
   	        	    		 timer = 0 ;
   	        	    		
   	        	    	 }
   	        	    	timer++;
   	        	    	alarmNotif.setContentText("in "+varHourDiff+" hours and "+varMinDiff+" minutes.");
	    	        	    // Because the ID remains unchanged, the existing notification is
	    	        	    // updated.
	    	        	    alarmNotifyMgr.notify(
	    	        	            alarmNotificationId,
	    	        	            alarmNotif.build());
   	        	    	
   	        	     }

   	        	     public void onFinish() 
   	        	     {
   	        			new Thread(new Runnable() {
						public void run() {
   	        	    	 int i=0;
   	        	        	for(i=0;CountdownActivity.alarmSet;i++)
   				            {
   				            	if(i<=5)
   				            	{
   				            		if(cam==null)
   				            		{
   				            			try
   				            			{
	   				            			cam = Camera.open();
	   							            Parameters p = cam.getParameters();
	   							            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
	   							            cam.setParameters(p);
	   							            cam.startPreview();
   				            			}
   				            			catch(Exception e)
   				            			{}
   					            	}
   				            	}
   				            	else
   				            	{
   				            		try
	   				            	{
	   				            		if(cam!=null)
	   				            		{
	   				            			cam.stopPreview();
	   				            			cam.release();
	   				            			cam = null;
	   				            		}
	   				            		if(i==10)
	   				            			{
	   				            				i=0;
	   				            			}
	   				            	}
   				            		catch(Exception e)
   				            		{}
   				            	}
   				            }
   	        	        	if(MainActivity.cam==null)
   	        	        	{
   	        	        		try
   	        	        		{
		   	        	        	cam.stopPreview();
				            		cam.release();
				            		cam = null;
   	        	        		}
   	        	        		catch(Exception e)
   	        	        		{}
   	        	        	}
   	        	       	 }}).start();

   	     			//Intent backToMainIntent = new Intent(new CountdownActivity(), MainActivity.class);
   	     			//startActivity(backToMainIntent);
   	        			 
   	        	     }
   	        	  }.start();
   	 
		Intent mainPageIntent = new Intent(this, MainActivity.class);
		mainPageIntent.putExtra("HOUR", newAlarmTime.getCurrentHour().toString());
		mainPageIntent.putExtra("MIN", newAlarmTime.getCurrentMinute().toString());
		startActivity(mainPageIntent);
	}
	
	public void sendClick(View view)
	{
		
		/*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
			tempUUID = getString(R.string.app_UUID);
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	Toast.makeText(getBaseContext(), device.getName() + "\t" + device.getAddress(),
		                Toast.LENGTH_SHORT).show();
		    	if(device.getName().equalsIgnoreCase("TanmayMoto") || device.getName()=="XT1052")
		    	{
		    		Thread sendAlarmThread = new ConnectThread(device,tempUUID);
		    		sendAlarmThread.start();
		    	}
		    }
		}*/
		/*
		 * Creates a new Intent to start the RSSPullService
		 * IntentService. Passes a URI in the
		 * Intent's "data" field.
		 */
		Intent mServiceIntent = new Intent(CountdownActivity.this, SendAlarmService.class);
		mServiceIntent.setData(Uri.parse("Hi"));
		startService(mServiceIntent);
	}
	public void receiveClick(View view)
	{
		Button receiver = (Button) view;
		Intent mServiceIntent = new Intent(CountdownActivity.this, ReceiveAlarmService.class);
		if(((String) receiver.getText()).equalsIgnoreCase("receive alarm"))
		{
			receiver.setText("Stop Receiving");
			/*
			tempUUID = getString(R.string.app_UUID);
			Thread receiveAlarmThread = new AcceptThread(mBluetoothAdapter, tempUUID);
			receiveAlarmThread.start();
			*/
			mServiceIntent.setData(Uri.parse("Hi"));
			startService(mServiceIntent);
		}
		else
		{
			receiver.setText("Receive Alarm");
			stopService(mServiceIntent);
		}
	}
}
