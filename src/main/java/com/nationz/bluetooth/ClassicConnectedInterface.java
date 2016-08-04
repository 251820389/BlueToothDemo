package com.nationz.bluetooth;

import android.bluetooth.BluetoothSocket;

public interface ClassicConnectedInterface {
	public void manageConnectedSocket(BluetoothSocket socket);
	public void circleSendData(BluetoothSocket socket);
}
