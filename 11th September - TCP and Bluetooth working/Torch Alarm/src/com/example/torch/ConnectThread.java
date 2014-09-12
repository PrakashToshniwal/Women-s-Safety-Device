package com.example.torch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final OutputStream mmOutStream;
 
    public ConnectThread(BluetoothDevice device, String tempUUID) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        OutputStream tmpOut = null;
        mmDevice = device;
        UUID appUUID = UUID.fromString(tempUUID);
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = mmDevice.createRfcommSocketToServiceRecord(appUUID);
            tmpOut = tmp.getOutputStream();
        } catch (IOException e) { }
        mmSocket = tmp;
        mmOutStream = tmpOut;
    }
 
    public void run() {
        // Cancel discovery because it will slow down the connection
        
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }
 
    private void manageConnectedSocket(BluetoothSocket mmSocket2) {
		// TODO Auto-generated method stub
    	String sendData = "Hi\n";
		try {
			mmOutStream.write(sendData.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block

			cancel();
			
		}
	}

	/** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
