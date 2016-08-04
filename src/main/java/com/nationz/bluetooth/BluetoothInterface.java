package com.nationz.bluetooth;

import java.util.UUID;

import com.nationz.bean.BluetoothDevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public interface BluetoothInterface {
	//set up bluetooth
	public BluetoothAdapter setUpBT();
	
	//scan remote device
	public boolean scanDevice();
	
	//close bluetooth
	public void closeBluetooth();
	
	//stop ScanBt
	public void stopScan();
	
	//connect device
	public void connectDevice(BluetoothDevices device);
	
	//connect disconnect
	public void disConnectDevice(BluetoothDevices device);
	//startServer
	
	public void startServer(UUID uuid);
	//send data
	public void sendData(byte[] data);
	
	
	public interface ScanCallback{
		public void findDeviceCallbak(BluetoothDevice device);
		public void scanFinishedCallbak();
	}
	
}
