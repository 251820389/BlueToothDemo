package com.nationz.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import com.nationz.bean.BluetoothDevices;
import com.nationz.nk_bluetooth.R;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.SimpleExpandableListAdapter;

public class BluetoothManagerForAPP {

	private BluetoothAdapter btAdapter;
	private static Context context;
	private BluetoothInterface bt;
	private static BluetoothManagerForAPP btMFA = null;
	//sppЭ���ʶUUID
	public static final UUID MUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); 

	//	private boolean leMode = false;
	public static final int APP_ID=0x00001001;
	public static synchronized BluetoothManagerForAPP getInstance(){
		if(btMFA == null){
			btMFA = new BluetoothManagerForAPP();
		}
		return btMFA;
	}

	/**
	 * open bluetooth 
	 */
	public void openBluetooth(final Context context){

		try {
			ClassicBluetooth.openBluetooth();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}

	public BluetoothAdapter getBtAdapter(){
		if(btAdapter != null)
			return btAdapter;
		else
			return BluetoothAdapter.getDefaultAdapter();
		
	}
	

	public void scanDevices(Context context,Handler mHandler,boolean leMode){
		this.context = context;
		if(leMode){
			bt =  LEBluetooth.getInstance((BluetoothInterface.ScanCallback)context,mHandler);
		}else{
			bt = ClassicBluetooth.getInstance((BluetoothInterface.ScanCallback)context,mHandler);
		}
		try {
			btAdapter = bt.setUpBT();
		} catch (Exception e) {
			// TODO: handle exception
		}
		boolean f = bt.scanDevice();
		Log.e("btManager","scanDevice is start =="+f);
	}
	

	
	public void connectDevice(BluetoothDevices dev){
		bt.connectDevice(dev);
	}
	
	public void stopScanDevices(){
		Log.e("M", "stopScanDevices");
		if(btAdapter != null && bt != null){
			bt.stopScan();
		}
	}
	
	public void disConnectDevice(BluetoothDevices dev){
		if(bt != null){
			bt.disConnectDevice(dev);
		}
	}
	
	public void closeBt(){
		if(bt != null){
		
			bt.closeBluetooth();
			bt = null;
		}
	}

	public void closeRegister(){
		if(bt != null){
			
			bt.closeBluetooth();

		}
	}
	
	public void sendNotifaction(int bytes){
		NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher).setContentTitle("Bluetooth Recieve data").setContentText("data len ="+bytes); 
		// Creates an explicit intent for an Activity in your app 
//		Intent resultIntent = new Intent(this, ResultActivity.class);  
		// The stack builder object will contain an artificial back stack for the 
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen. 
//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this); 
//		// Adds the back stack for the Intent (but not the Intent itself) 
//		stackBuilder.addParentStack(ResultActivity.class); 
//		// Adds the Intent that starts the Activity to the top of the stack 
//		stackBuilder.addNextIntent(resultIntent); 
//		PendingIntent resultPendingIntent =         stackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT         ); 
//		mBuilder.setContentIntent(resultPendingIntent); 
		

		mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        
     
        long[] vibrate = { 0, 10, 20, 30 ,40 ,50 ,60 };
        mBuilder.setVibrate(vibrate);
        
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); 
		// mId allows you to update the notification later on. 
			try{
					mNotificationManager.notify(APP_ID, mBuilder.build());
			}catch(Exception e){
		
			}
	}
	
	
	public void sendData(byte[] data){
		Log.e("", "@@@@@@@@@@send Data is sendding");
		bt.sendData(data);
	}
	
	public void openServer(Handler mHandler){
		if(bt == null){
			bt = new ClassicBluetooth((BluetoothInterface.ScanCallback)this.context,mHandler);
		}
		bt.startServer(MUUID);
	}
	


	private String lookup(String uuid, String unknownString) {

//		uuid.substring(0, 6);
		return uuid.substring(0, 6);
	}
}
