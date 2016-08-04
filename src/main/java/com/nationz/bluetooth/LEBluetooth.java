package com.nationz.bluetooth;

import java.util.List;
import java.util.UUID;

import com.nationz.bean.BluetoothDevices;
import com.nationz.ui.MainActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class LEBluetooth implements BluetoothInterface{
	private static Context context;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private static final long SCAN_PERIOD = 10000; 
	BluetoothInterface.ScanCallback scanCB;
//	private BluetoothGatt mBluetoothGatt; 
	private Handler mhandler;
	
	public  static LEBluetoothService mBluetoothLeService;
	private static BluetoothGattCharacteristic characteristic;
	public BluetoothGattCharacteristic getCharacteristic() {
		return characteristic;
	}

	public static void setCharacteristic(BluetoothGattCharacteristic mcharacteristic) {
		characteristic = mcharacteristic;
	}
	private BluetoothAdapter.LeScanCallback mLeScanCallback =   new BluetoothAdapter.LeScanCallback() {     
		@Override    
		public void onLeScan(final BluetoothDevice device, int rssi,byte[] scanRecord) {   
			((Activity) context).runOnUiThread(new Runnable() {       
				@Override            
				public void run() {           
//					mLeDeviceListAdapter.addDevice(device);          
//					mLeDeviceListAdapter.notifyDataSetChanged();  
					scanCB.findDeviceCallbak(device);
					}       
				});    
			
			}
		};
		
	private  static LEBluetooth lEBluetooth = null;
	public synchronized static LEBluetooth getInstance(BluetoothInterface.ScanCallback sCBs,Handler mHandler){
			if(lEBluetooth == null){
				lEBluetooth = new  LEBluetooth(sCBs,mHandler);
			}
			return lEBluetooth;
		}	
		
	public LEBluetooth( BluetoothInterface.ScanCallback context,Handler mhandler){
		if(this.context == null)
			this.context = (Context)context;
		scanCB = (BluetoothInterface.ScanCallback)context;
		this.mhandler = mhandler;
	}
	
	@Override
	public BluetoothAdapter setUpBT() throws ExceptionInInitializerError {

		BluetoothAdapter mBluetoothAdapter;
		final BluetoothManager bluetoothManager = (BluetoothManager)this.context.getSystemService(Context.BLUETOOTH_SERVICE); 
		mBluetoothAdapter = bluetoothManager.getAdapter();
		Log.d("LE", "setup");
//		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {    
//			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);     
//			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); 
//			}
		if(mBluetoothAdapter == null){
			throw new ExceptionInInitializerError("Device does not support Bluetooth Low Energy");
		}
		
		if(!mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.enable();
		}
		
		this.mBluetoothAdapter = mBluetoothAdapter;
		return mBluetoothAdapter;
	}
	
	@Override
	public boolean scanDevice() { 

		Handler mHandler = new Handler(); 
	       if (!mScanning) {  
	    	   // Stops scanning after a pre-defined scan period.            
	    	   mHandler.postDelayed(new Runnable() {                 
	    		   @Override                
	    		  public void run() {                   
	    			   mScanning = false;                  
	    			   mBluetoothAdapter.stopLeScan(mLeScanCallback); 
	    			   scanCB.scanFinishedCallbak();
	    			   }             }, SCAN_PERIOD);         
	    	   mScanning = true;            
	    	   mBluetoothAdapter.startLeScan(mLeScanCallback);      
	    	   } else {            
	    		   mScanning = false;         
	    		   mBluetoothAdapter.stopLeScan(mLeScanCallback); 
	    		   scanCB.scanFinishedCallbak();
	    	}
		return true;        
	    }

	@Override
	public void closeBluetooth() {

		if(mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
//		if(mBluetoothLeService != null && conn != null){
//			this.context.unbindService(conn);
//			
//		}
		
		
//	    if (mBluetoothGatt == null) {         
//	    	return;     
//	    	}     
//	    mBluetoothGatt.close();     
//	    mBluetoothGatt = null; 
	}

	@Override
	public void stopScan() {

		if(mBluetoothAdapter != null && mLeScanCallback != null)
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
	}

	public static ServiceConnection getServiceConn(){
		return conn;
	}
	private static ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {

			Log.e("service", "LE service disconnected ");
			 mBluetoothLeService = null;
			 conn = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			Log.e("service", "LE service connected ");
            mBluetoothLeService = ((LEBluetoothService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("", "Unable to initialize Bluetooth");
//                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(device.getDeviceAddress());
		}
	};
	BluetoothDevices device;	
	@Override
	public void connectDevice(BluetoothDevices device) {

		this.device = device;
		Log.e("LEBT", "is connected pressed");
//		LEBluetoothService leBTService = new LEBluetoothService();
		
//		Intent intent = new Intent();
//		Bundle bundle = new Bundle();
//		bundle.put
//		intent.putExtras(bundle);
//		LEBluetoothService.setDevice(device);
//		this.context.startService(new Intent(this.context,LEBluetoothService.class));
//		if(mBluetoothLeService == null)
//			this.context.bindService(new Intent(this.context,LEBluetoothService.class), conn, Context.BIND_AUTO_CREATE);
//		else
			boolean connect = mBluetoothLeService.connect(device.getDeviceAddress());
			Log.e("xyz", "connect:"+connect);
//		mBluetoothGatt = device.getDevice().connectGatt(this.context, false, mGattCallback);
	}

	@Override
	public void sendData(byte[] data) {

		Log.e("LE","send DDDDDDDD");
		mBluetoothLeService.writeCharacteristic(characteristic, data);
	}

	@Override
	public void startServer(UUID uuid) {

		
	}

	@Override
	public void disConnectDevice(BluetoothDevices device) {

//		this.context.stopService(new Intent(this.context,LEBluetoothService.class));
//		mBluetoothGatt.disconnect();
		mBluetoothLeService.disconnect();

	} 
	     
	public static List<BluetoothGattService> getServices(){
		return mBluetoothLeService.getSupportedGattServices();
		
		
	}
	

}
