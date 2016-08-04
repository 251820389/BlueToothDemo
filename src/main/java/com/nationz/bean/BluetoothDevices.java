package com.nationz.bean;

import java.io.Serializable;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;


public class BluetoothDevices implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 935028777397963270L;
	private String name = "";
	private String address = "";

	private boolean connected = false;
	private boolean paired = false;
	public boolean isPaired() {
		return paired;
	}

	public void setPaired(boolean paired) {
		this.paired = paired;
	}

	public static class DeviceInfo{
		private static BluetoothDevice device;
		private static BluetoothSocket mSocket = null;
		
	}
	public BluetoothSocket getmSocket() {
		return DeviceInfo.mSocket;
	}

	public void setmSocket(BluetoothSocket mSocket) {
		DeviceInfo.mSocket = mSocket;
	}
	public BluetoothDevice getDevice() {
		return DeviceInfo.device;
	}

	public void setDevice(BluetoothDevice device) {
		DeviceInfo.device = device;
	}
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
		setPaired(true);
	}



	public BluetoothDevices(){
		
	}

	@Override
	  public String toString()
	  {
	    String name = this.name == null ? "Device" : this.name;
	    String addr = this.address == null ? "00:00:00:00:00:00" : this.address;
	    
	    return " [" + name + " - " + addr + "]";
	  }
//	@Override
//	public int describeContents() {
//		return 0;
//	}

//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//	    dest.writeString(this.name);
//	    dest.writeString(this.address);
//	}
	public String getDeviceName() {
		return name;
	}

	public void setDeviceName(String name) {
		this.name = name;
	}
	
	public String getDeviceAddress(){
		return address;
	}
	
	public void setDeviceAddress(String address){
		this.address = address;
	}
}
