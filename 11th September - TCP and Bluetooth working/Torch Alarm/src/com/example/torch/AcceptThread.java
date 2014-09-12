package com.example.torch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private InputStream mmInStream;
    public static String receivedData;
    public AcceptThread(BluetoothAdapter mBluetoothAdapter, String tempUUID) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        InputStream tmpInput = null;
        receivedData = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
        	UUID alarmUUID = UUID.fromString(tempUUID);
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("AlarmSocket", alarmUUID);
        } catch (IOException e) { e.printStackTrace();}
        mmServerSocket = tmp;
        mmInStream = tmpInput;
    }
 
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
            	try {
					mmInStream = socket.getInputStream();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                manageConnectedSocket(socket);
                try {
					cancel();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            }
        }
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
    
    public void manageConnectedSocket(BluetoothSocket socket)
    {
    	String result;
    	try
    	{
	        InputStream aStream = socket.getInputStream();
	        InputStreamReader aReader = new InputStreamReader( aStream );
	        BufferedReader mBufferedReader = new BufferedReader( aReader );
	        result = mBufferedReader.readLine();
    	}
    	catch(Exception e)
    	{
    		result = "Error in receiving"; 
    	}
    }
}
