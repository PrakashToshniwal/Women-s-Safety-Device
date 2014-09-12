package com.example.torch;


import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;

public class ReceiveAlarmService extends Service{

	private static final String TAG = "ReceiveAlarmService";
	private static BluetoothAdapter mBluetoothAdapter;
	private AcceptThread receiveAlarmThread;
	private static String tempUUID;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "Congrats! ReceiveAlarmService Created", Toast.LENGTH_LONG).show();
		if(receiveAlarmThread!=null){receiveAlarmThread.cancel(); receiveAlarmThread = null;}
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "ReceiveAlarmService Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
	//Note: You can start a new thread and use it for long background processing from here.

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		tempUUID = getString(R.string.app_UUID);
		receiveAlarmThread = new AcceptThread(mBluetoothAdapter, tempUUID);
		receiveAlarmThread.start();
		
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "ReceiveAlarmService Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
	}
}