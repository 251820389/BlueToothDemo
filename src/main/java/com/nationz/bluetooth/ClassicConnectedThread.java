package com.nationz.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nationz.ui.MainActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

	public class ClassicConnectedThread extends Thread {     
		private final BluetoothSocket mmSocket;   
		private final InputStream mmInStream;    
		private final OutputStream mmOutStream;   
		private final Handler mHandler;
		public static  long resultTimer = 0;
		private Context context;
		public ClassicConnectedThread(Context context,BluetoothSocket socket,Handler mHandler) {       
			Log.e("", "ClassicConnectedThread!!!!!!!!!");
			this.context = context;
			mmSocket = socket;         
			InputStream tmpIn = null;         
			OutputStream tmpOut = null; 
			this.mHandler = mHandler;
			// Get the input and output streams, using temp objects because         
			// member streams are final        
			try {             
				tmpIn = socket.getInputStream();             
				tmpOut = socket.getOutputStream();        
				} catch (IOException e) { 
					
				}           
			mmInStream = tmpIn;         
			mmOutStream = tmpOut;     
			}       
		public void run() {         
			
			// buffer store for the stream         
			int bytes; 
			// bytes returned from read()          
			// Keep listening to the InputStream until an exception occurs         
			while (true) { 
				byte[] buffer = new byte[1024]; 
				Log.e("", "is running to read!!!!!!!!!");
				try {              
					// Read from the InputStream   
					
//					long start = System.nanoTime();
					bytes = mmInStream.read(buffer);          
					resultTimer  = (long)(System.nanoTime());
					int r = 0;
//					Log.e("result", "result ========"+result);
					// Send the obtained bytes to the UI activity               
					mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, r, buffer).sendToTarget();    
					Intent intent = new Intent(ClassicBluetooth.EXTRA_DATA);
//					intent.putExtra("Classic_NUM", bytes);
					intent.putExtra("data", buffer);
					context.sendBroadcast(intent);
					} catch (IOException e) {                 
						break;            
					}        
				}    
			}       
		/* Call this from the main activity to send data to the remote device */    
		public void write(byte[] bytes) {         
			try {  
				Log.d("", "is writted!!!!!!!!!!!!!");
				mmOutStream.write(bytes);         
				Log.d("", "is writted????????");
				} catch (IOException e) { 
					
				}    
			}      
		/* Call this from the main activity to shutdown the connection */    
		public void cancel() {         
			try {            
					mmSocket.close();         
				} catch (IOException e) { 
					
				}    
			} 
		


		
		
}
