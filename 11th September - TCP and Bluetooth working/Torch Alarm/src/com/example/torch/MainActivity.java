package com.example.torch;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

// Package functionality manifest

public class MainActivity extends Activity { // function1 and function2
		
	
	public static Camera cam = null;  //comment whenever hw based initialisation
	public Switch sw;
    private static Bundle bundle = new Bundle();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent newAlarmIntent = getIntent();
		if(newAlarmIntent!=null)
		{
			String hour = newAlarmIntent.getStringExtra("HOUR");
			String min = newAlarmIntent.getStringExtra("MIN");
			if(hour!=null && min!=null)
			{
				Button stopButton = (Button) findViewById(R.id.stopButton);
				stopButton.setVisibility(View.VISIBLE);

   	    	 
			}
		}
		sw=(Switch) findViewById(R.id.onOffSwitch);
		sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				onOffSwitchClick(sw);
			}
		});
		
	}
	@Override
	public void onPause()
	{
		super.onPause();
		bundle.putBoolean("ToggleButtonState", sw.isChecked());
	}
	@Override
	public void onResume()
	{
		super.onResume();
		sw.setChecked(bundle.getBoolean("ToggleButtonState",false));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
		      Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
		      // prompts email clients only
		      email.setType("message/rfc822");

		      email.putExtra(Intent.EXTRA_EMAIL, new String[] {"vivekt.iiti@gmail.com"});
		      email.putExtra(Intent.EXTRA_SUBJECT, "FlashLight Report");
		      email.putExtra(Intent.EXTRA_TEXT, "");

		      try {
			    // the user can choose the email client
		         startActivity(Intent.createChooser(email, "Choose an email client from..."));
		     
		      } catch (android.content.ActivityNotFoundException ex) {
		         Toast.makeText(MainActivity.this, "No email client installed.",
		        		 Toast.LENGTH_LONG).show();
		      }
		}
		return super.onOptionsItemSelected(item);
	}
	public void onOffSwitchClick(View view)
	{
		Switch onOff = (Switch) view;
		if(onOff.isChecked())
		{
			try {
		        if (getPackageManager().hasSystemFeature(
		                PackageManager.FEATURE_CAMERA_FLASH)) {
		        	if(cam==null)
		        	{	
			            cam = Camera.open();
			            Parameters p = cam.getParameters();
			            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			            cam.setParameters(p);
			            cam.startPreview();
		        	}
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		        Toast.makeText(getBaseContext(), "Exception flashLightOn",
		                Toast.LENGTH_SHORT).show();
		    }
		}
		else
		{
			try
			{
			    if (getPackageManager().hasSystemFeature(
		                PackageManager.FEATURE_CAMERA_FLASH)) 
			    {
		            cam.stopPreview();
		            cam.release();
		            cam = null;
			    }
			} 
			catch (Exception e) 
			{
		        e.printStackTrace();
		        Toast.makeText(getBaseContext(), "Exception flashLightOff",
		                Toast.LENGTH_SHORT).show();
		    }
		}
	}
	public void countdownClick(View view)
	{
		Intent settingsIntent = new Intent(this, CountdownActivity.class);
		startActivity(settingsIntent);
	}
	public void stopClick(View view)
	{
		view.setVisibility(View.GONE);
		Intent cancelIntent = new Intent(this, CountdownActivity.class);
		NotificationManager alarmNotifyMgr = 
		        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		alarmNotifyMgr.cancel(001);
		CountdownActivity.alarmCountDown.cancel();
		cancelIntent.putExtra("AlarmState","Stop");
		startActivity(cancelIntent);
	}

	public void httpClick(View view)
	{
		Intent HTTPPageIntent = new Intent(this, HTTPRequestActivity.class);
		startActivity(HTTPPageIntent);
	}
}
