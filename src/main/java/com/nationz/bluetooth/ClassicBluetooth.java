package com.nationz.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import com.nationz.bean.BluetoothDevices;
import com.nationz.ui.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class ClassicBluetooth implements BluetoothInterface ,ClassicConnectedInterface{
	
    public final static String ACTION_CLASSIC_CONNECTED =
            "com.example.bluetooth.ACTION_ClASSIC_CONNECTED";
    public final static String ACTION_CLASSIC_CONNECT_FAIL =
            "com.example.bluetooth.ACTION_ClASSIC_CONNECT_FAIL";
    public final static String ACTION_CLASSIC_DISCONNECTED =
            "com.example.bluetooth.ACTION_ClASSIC_DISCONNECTED";

    public final static String EXTRA_DATA =
            "com.example.bluetooth.EXTRA_DATA";
	
	
	private static  BluetoothAdapter mBluetoothAdapter;
	private BluetoothInterface.ScanCallback sCB;
	private Context context;
	private  boolean isRegisted = false;
	private final String TAG = getClass().getSimpleName();
	private  BroadcastReceiver mReceiver; 
	private  ClassicConnectedThread cCT;
	static BluetoothDevice device;
	Handler mHandler;
	public static final UUID MUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); 
	private  static ClassicBluetooth classicBluetooth = null;
	public synchronized static ClassicBluetooth getInstance(BluetoothInterface.ScanCallback sCBs,Handler mHandler){
		if(classicBluetooth == null){
			classicBluetooth = new  ClassicBluetooth(sCBs,mHandler);
		}
		return classicBluetooth;
	}
	public ClassicBluetooth(BluetoothInterface.ScanCallback sCBs,Handler mHandler){

			Log.e(TAG, "ClassicBluetooth  ++ mReceiver==="+mReceiver);
			this.context = (Context)sCBs;
			this.mHandler = mHandler;
			this.sCB = sCBs;
			if(mReceiver == null){
				mReceiver = new BroadcastReceiver() {     
					public void onReceive(Context context, Intent intent) {         
						String action = intent.getAction();        
						Log.e(TAG,"action == "+action);
						// When discovery finds a device         
						if (BluetoothDevice.ACTION_FOUND.equals(action)) {           
							// Get the BluetoothDevice object from the Intent           
							device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);          
							int deviceMajor = device.getBluetoothClass().getMajorDeviceClass();
							int classValue = device.getBluetoothClass().hashCode();
							Log.e("", "deviceMajor === "+(classValue&0x1F00) +"classValue == "+classValue);
							
							
							// Add the name and address to an array adapter to show in a ListView           
//							mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
						//	if(deviceMajor != 512)
								sCB.findDeviceCallbak(device);
							}else if("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)){
								sCB.scanFinishedCallbak();
								Log.e("", "actionDISCOVERY_FINISHED  ~~~~~~~");
							}
						} 

					}; 
			}
			
			if(!isRegisted){
				Log.e(TAG, "register one ++");
				IntentFilter intentFilter = new IntentFilter(); 
				intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
				intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
				intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
				context.registerReceiver(mReceiver, intentFilter); 
				isRegisted = true;
			}
	}
	
	
	
	public static void openBluetooth(){
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {     
			// Device does not support Bluetooth 
			throw new ExceptionInInitializerError("Device does not support Bluetooth");
		}
		if (!bluetoothAdapter.isEnabled()) {

			bluetoothAdapter.enable();
		}
		mBluetoothAdapter = bluetoothAdapter;
	}
	
	
	@Override
	public BluetoothAdapter setUpBT() throws ExceptionInInitializerError{

		if(mBluetoothAdapter == null){
//			return null;
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		return mBluetoothAdapter;
	}

	
	
	private void queryPairedDevices(){
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices(); 
		// If there are paired devices 
		if (pairedDevices.size() > 0) {     
			// Loop through paired devices     
			for (BluetoothDevice device : pairedDevices) {         
				// Add the name and address to an array adapter to show in a ListView         
//				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());    
				} 
			}
			}



	public boolean scanDevice() {

		boolean scanStart = false;
		if(mBluetoothAdapter != null){
			scanStart = mBluetoothAdapter.startDiscovery();
		}
		return scanStart;
	}



	@Override
	public void closeBluetooth() {

		if(mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
		Log.e(TAG, "unregisterReceiver  ++ mReceiver==="+mReceiver);
		
		if(isRegisted && mReceiver != null){
			Log.e(TAG, "unregister one ---");
			this.context.unregisterReceiver(mReceiver);
			mReceiver = null;
			isRegisted = false;
		}
	}


	@Override
	public void stopScan() {

		if(mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
	}


	private ClassicConnectThread cThread;
	
	@Override
	public void connectDevice(BluetoothDevices device) {

		Log.e("Classic", "connect btn is pressed");
		cThread = new ClassicConnectThread(this.context,device,MUUID,mBluetoothAdapter,this,mHandler);
		cThread.start();
	}

	

	@Override
	public void manageConnectedSocket(BluetoothSocket socket) {

		Log.e("", "manageConnectedSocket is occur");
		cCT = new ClassicConnectedThread(this.context,socket,mHandler);
		Log.e("", "manageConnectedSocket is occur@!!!!!!!!");
		cCT.start();
	}
	@Override
	public void circleSendData(BluetoothSocket socket){
		cCT = new ClassicConnectedThread(this.context,socket,mHandler);
		Log.e("", "manageConnectedSocket is occur@!!!!!!!!");
		byte[] data = new byte[512];
		for(int i=0;i<data.length;i++){
			data[i]=(byte)i;
		}
		int  x = 10;
		while(x > 0){
		 cCT.write(data);
		 x--;
		}
	}
	
	
	

	
	
	public static boolean doRequestDiscoverable(){
		try{
			//JAVA������Ƶ���API @hide����
			Class<? extends BluetoothAdapter> cls = BluetoothAdapter.class;
			Method m = cls.getMethod("setScanMode",new Class[] {Integer.TYPE,Integer.TYPE});
			int mode = BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
			int timeOut = 300;
			boolean flag = (Boolean)true;
			m.invoke(ClassicBluetooth.mBluetoothAdapter, new Object[] {Integer.valueOf(mode),Integer.valueOf(timeOut) });
			return flag;
//			createSocketWithChannel(1);
		}catch(Exception e){
			e.printStackTrace();	
		}
		return false;
	}
	
	
	static BluetoothSocket createSocketWithChannel(int channel)
	  {
	    BluetoothSocket socket = null;
	    Class<? extends BluetoothDevice> cls = BluetoothDevice.class;
	    Method m = null;
	    try
	    {
	      m = cls.getMethod("createRfcommSocket", new Class[] { Integer.TYPE });
	    }
	    catch (NoSuchMethodException e)
	    {
	      e.printStackTrace();
	    }
	    if (m != null) {
	      try
	      {
	        socket = (BluetoothSocket)m.invoke(device, new Object[] { Integer.valueOf(channel) });
	      }
	      catch (IllegalArgumentException e)
	      {
	        e.printStackTrace();
	      }
	      catch (IllegalAccessException e)
	      {
	        e.printStackTrace();
	      }
	      catch (InvocationTargetException e)
	      {
	        e.printStackTrace();
	      }
	    }
	    return socket;
	  }



	@Override
	public void startServer(UUID uuid) {

		ClassicServerThread ccThread = new ClassicServerThread(mBluetoothAdapter,uuid,this);
		ccThread.start();
	}



	@Override
	public void sendData(byte[] data) {

		Log.e("Classic BT", "!!send Data is sendding");
		cCT.write(data);
	}



	@Override
	public void disConnectDevice(BluetoothDevices device) {

		if(cThread != null){
			cThread.resetSocket();
			cThread = null;
		}
	}
	
	
	
}

			
			
