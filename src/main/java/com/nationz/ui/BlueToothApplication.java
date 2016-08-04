package com.nationz.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import com.nationz.bean.BluetoothDevices;
import com.nationz.crash.Crash;

public class BlueToothApplication extends Application{
	public Boolean FlagSendBLE;
	static Context _context;
	private final static String prefFileName = "P_DEV.pre";
	private final static String prefKey = "Paried_Dev";
	public Boolean getFlagSendBLE() {
		return FlagSendBLE;
	}
	public void setFlagSendBLE(Boolean flagSendBLE) {
		FlagSendBLE = flagSendBLE;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Crash crash = Crash.getInstance();  
		crash.init(getApplicationContext());
		Log.e("A", "BlueToothApplication create");
		FlagSendBLE = true;
		_context = getApplicationContext();
	}
    public static SharedPreferences getPreferences(String prefName) {
	return _context.getSharedPreferences(prefName,
		Context.MODE_MULTI_PROCESS);
    }
    public static boolean putPiredDevice(
    		BluetoothDevices des){
    	List<BluetoothDevices> lsDevices;
    	String keyString = null;
    	try {
    		 lsDevices = getPariedDevice();
    		 if(lsDevices.size() >3){
    	    		lsDevices.remove(0);
    	    	}
    		 	if(!deviceExisted(des,lsDevices)){
    		 		 Log.e("XXXXXXX","add the data to preFFFF");
    		 		des.setConnected(false);
    		 		lsDevices.add(des);
    		 		
    		 	}
    	    	keyString = list2String(lsDevices);
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
    	
    	SharedPreferences preferences = getPreferences(prefFileName);
    	int size = preferences.getAll().size();
    	Editor editor = preferences.edit();
    	editor.putString(prefKey, keyString);
    	des.setConnected(true);
    	return editor.commit();
    }
   public static List<BluetoothDevices> getPariedDevice() throws StreamCorruptedException,IOException, ClassNotFoundException{
	   String listString = getSharedString();
	   if (listString.equals("")){
		   List<BluetoothDevices> ls = new ArrayList<BluetoothDevices>(4);
		   return ls;
	   }
	   byte[] mobileBytes = Base64.decode(listString.getBytes(), Base64.DEFAULT);
	   ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
	   ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
	   List<BluetoothDevices> listPaired = (List<BluetoothDevices>)objectInputStream.readObject();
	   objectInputStream.close();
	   return listPaired;
	   
   }
    private static String getSharedString(){
    	SharedPreferences preferences = getPreferences(prefFileName);
    	return preferences.getString(prefKey, "");
    }
    
    public static String list2String(List<BluetoothDevices> ld) throws IOException{
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    	ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    	objectOutputStream.writeObject(ld);
    	String listString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
    	objectOutputStream.close();
    	return listString;
    }
    
	private  static boolean deviceExisted(BluetoothDevices device,List<BluetoothDevices> arrListDevices) {
		if (device == null)
			return false;
		Log.e("APPP", "devices ==="+device);
		Iterator<BluetoothDevices> it = arrListDevices.iterator();
		while (it.hasNext()) {
			BluetoothDevices d = it.next();
			if (d!= null && device != null && d.getDeviceAddress().equals(device.getDeviceAddress()) )
				return true;
		}
		return false;
	}
    
}
