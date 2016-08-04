package com.nationz.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import com.nationz.bean.BluetoothDevices;
import com.nationz.ui.MainActivity;
import com.nationz.util.EventMSG;
import com.nationz.util.ToastUtil;

import de.greenrobot.event.EventBus;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class ClassicConnectThread extends Thread{
	private  BluetoothSocket mmSocket;     
	private final BluetoothDevices mmDevice;   
	private BluetoothAdapter mBluetoothAdapter;
	private final ClassicConnectedInterface cCI;
	private final Handler mHandler;
	private Context context;
	public ClassicConnectThread(Context context,BluetoothDevices device ,UUID MY_UUID,BluetoothAdapter mBluetoothAdapter,ClassicConnectedInterface cCI,Handler mHandler) {    
		// Use a temporary object that is later assigned to mmSocket,        
		// because mmSocket is final         
		this.context = context;
		BluetoothSocket tmp = null;         
		mmDevice = device;           
		this.mHandler = mHandler;
		this.cCI = cCI;
		// Get a BluetoothSocket to connect with the given BluetoothDevice       
		try {            
			// MY_UUID is the app's UUID string, also used by the server code
			BluetoothDevice device2 = device.getDevice();
			if(device2 == null){
				Toast.makeText(context, "设备连接异常，请重新", Toast.LENGTH_LONG).show();
				return;
			}
			tmp = device2.createRfcommSocketToServiceRecord(MY_UUID);        
			} catch (IOException e) {
				e.printStackTrace();
			}        
		mmSocket = tmp;     
		this.mBluetoothAdapter = mBluetoothAdapter;
	}      
	long start;
	public void run() {       
		// Cancel discovery because it will slow down the connection 
		if(mBluetoothAdapter == null){
			ToastUtil.showToast(context, "蓝牙适配器未获取到");
			return;
		}
		mBluetoothAdapter.cancelDiscovery();        
		try {         
			Log.e("ConnectThread", "SPP 配对中。。。");
			//先配对再连接
			if(mmDevice.getDevice().getBondState() == BluetoothDevice.BOND_NONE){
				
					Method creMethod = BluetoothDevice.class.getMethod("createBond");
					creMethod.invoke(mmDevice.getDevice());
				
			}
			// Connect the device through the socket. This will block            
			// until it succeeds or throws an exception  
			start = System.nanoTime();
			mmSocket.connect();         
			} catch (IOException connectException) {           
				// Unable to connect; close the socket and get out    
//				connectException.printStackTrace();
				Log.e("ConnectThread", "connectException dont why");
				try {               
//					mmSocket.close();   
//					SystemClock.sleep(500);
					mmSocket = (BluetoothSocket)mmDevice.getDevice().getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice.getDevice(), 6);
					mmSocket.connect();
					} catch (Exception closeException) {
						closeException.printStackTrace();
						Log.e("ConnectThread", "connectException dont why2222");
						try {
							mmSocket.close();
							mHandler.obtainMessage(MainActivity.MESSAGE_CONNECTED_FAILED).sendToTarget();
							broadcastUpdate(ClassicBluetooth.ACTION_CLASSIC_CONNECT_FAIL);
							return;  
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}          
			       
				} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   
		long result  = (long)(System.nanoTime()-start);
		mHandler.obtainMessage(MainActivity.MESSAGE_CONNECTED,result).sendToTarget();
//		EventBus.getDefault().post(new EventMSG(MainActivity.MESSAGE_CONNECTED,null));
		broadcastUpdate(ClassicBluetooth.ACTION_CLASSIC_CONNECTED);
		mmDevice.setmSocket(mmSocket);
		Log.e("ConnectThread", "Connect is ok");
		// Do work to manage the connection (in a separate thread)        
		cCI.manageConnectedSocket(mmSocket);     
		}  
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
       
        this.context.sendBroadcast(intent);
    }
	/** Will cancel an in-progress connection, and close the socket */   
	public void cancel() {        
		try {          
			mmSocket.close();     
			} catch (IOException e) {
				
			}    
		} 
	
	void resetSocket()
	  {
	    if (null != mmSocket)
	    {
	      try
	      {
	        InputStream is = mmSocket.getInputStream();
	        if (null != is) {
	          is.close();
	        }
	      }
	      catch (IOException ie)
	      {
	        ie.printStackTrace();
	      }
	      try
	      {
	        OutputStream os = mmSocket.getOutputStream();
	        if (null != os) {
	          os.close();
	        }
	      }
	      catch (IOException oe)
	      {
	        oe.printStackTrace();
	      }
	      try
	      {
	    	  mmSocket.close();
	      }
	      catch (IOException se)
	      {
	        se.printStackTrace();
	      }
	      mmSocket = null;
	      mmDevice.setmSocket(null);
	      mHandler.obtainMessage(MainActivity.MESSAGE_DISCONNECTED).sendToTarget();
	      broadcastUpdate(ClassicBluetooth.ACTION_CLASSIC_DISCONNECTED);
	    }
	  
	    
	  }

	
}
