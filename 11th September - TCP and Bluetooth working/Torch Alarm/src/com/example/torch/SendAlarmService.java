package com.example.torch;

import java.util.Set;

import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class SendAlarmService extends Service{

	private static final String TAG = "SendAlarmService";
	private static BluetoothAdapter mBluetoothAdapter;

    private ConnectThread sendAlarmThread;
	private static String tempUUID;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "Congrats! SendAlarmService Created", Toast.LENGTH_LONG).show();
		if (sendAlarmThread != null) {sendAlarmThread.cancel(); sendAlarmThread = null;}
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "SendAlarmService Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
	//Note: You can start a new thread and use it for long background processing from here.

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
			tempUUID = getString(R.string.app_UUID);
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	boolean checker = device.getName().contains("Tanmay") ;
		    	if(checker|| device.getName().equalsIgnoreCase("XT1052"))
		    	{
		    		sendAlarmThread = new ConnectThread(device,tempUUID);
		    		sendAlarmThread.start();
		    	}
		    }
		}
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "SendAlarmService Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
	}
}