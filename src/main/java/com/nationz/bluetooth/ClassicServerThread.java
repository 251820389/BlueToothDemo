package com.nationz.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ClassicServerThread extends Thread{
	private ClassicConnectedInterface cI;
	private final BluetoothServerSocket mmServerSocket;
	public ClassicServerThread(BluetoothAdapter mBluetoothAdapter,UUID MY_UUID,ClassicConnectedInterface cI) {
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		BluetoothServerSocket tmp = null;
		this.cI = cI;
		mBluetoothAdapter.cancelDiscovery();
		try {
			// MY_UUID is the app's UUID string, also used by the client code
			tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("XC", MY_UUID);
		} catch (IOException e) { }
		mmServerSocket = tmp;
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

				Log.e("ClassicBtconnneted", "已连接！！！！");
//					cI.manageConnectedSocket(socket);
				cI.circleSendData(socket);
				try {
					mmServerSocket.close();
				} catch (IOException e) {
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
		} catch (IOException e) {

		}
	}



}
