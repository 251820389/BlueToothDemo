package com.nationz.bluetooth;

import java.util.List;
import java.util.UUID;

import com.nationz.bean.BluetoothDevices;
import com.nationz.ui.BlueToothApplication;
import com.nationz.ui.ContentFragment;
import com.nationz.ui.MainActivity;
import com.nationz.util.BluetoothLEAtrribute;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LEBluetoothService extends Service {
    long start;
    long result;
//	private int dataNum = 0;
	public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	private final static String TAG = LEBluetoothService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(HEART_RATE_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
            	resultTime  = (long)(System.nanoTime()-startTime);
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
               // changeMTU(100);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	Log.d(TAG, "onCharacteristicRead   111111111111111");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.d(TAG, "onCharacteristicRead   2222222222222");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
			   
			result  = System.nanoTime();
//			dataNum++;
        	broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.e(TAG, "onCharacteristicChanged   sssssssss");
        }
        
        
        public void onCharacteristicWrite(BluetoothGatt gatt, 
        								  BluetoothGattCharacteristic characteristic, int status) {
//        	 broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//        	BlueToothApplication btA= (BlueToothApplication)getApplication();
        	if(status == BluetoothGatt.GATT_FAILURE){
        		Log.d(TAG, "onCharacteristicWrite failure ==" +status);
        		
        	}
        	if(status == BluetoothGatt.GATT_SUCCESS){
        		Log.d(TAG, "onCharacteristicWritefff   s");
        		
        	}
//       	btA.setFlagSendBLE(true);
        };
        
        public void onDescriptorWrite(BluetoothGatt gatt, 
        		BluetoothGattDescriptor descriptor, int status) {
//        	 broadcastUpdate(ACTION_DATA_AVAILABLE);
        	 Log.d(TAG, "onDescriptorWrite");
        };
        
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        	broadcastUpdate(ACTION_DATA_AVAILABLE);
        	Log.d(TAG, "onDescriptorRead");
        };
        
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
//        	broadcastUpdate(ACTION_DATA_AVAILABLE);
        	Log.d(TAG, "onReadRemoteRssi");
        };
        
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        	Log.e(TAG, "onMtuChanged");
        	if(status == BluetoothGatt.GATT_SUCCESS){
        		Log.d(TAG, "onMtuChanged oKKKKKKKKK@ mtu==="+mtu);
        	}
        };
        
        
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        if(mConnectionState == STATE_CONNECTED)
        	intent.putExtra("RTIME", resultTime);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for(byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                intent.putExtra("time", result);
                intent.putExtra("num", data.length);
                intent.putExtra("data", data);
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
    	LEBluetoothService getService() {
            return LEBluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    long startTime;
    long resultTime;
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            startTime = System.nanoTime();
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        startTime = System.nanoTime();
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        //Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        
        return true;
    }

    private void changeMTU(int mtu){
    	//mBluetoothGatt.requestMtu(mtu);
    }
    
    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
        
    }
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic,int len) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        byte[] data = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09};
        byte [] data2 = new byte[len];
		for(int i=0;i<len;i++){
			data2[i]=(byte)i;
		}
		ContentFragment.printHexbyte(data2);
        characteristic.setValue(data2);
        mBluetoothGatt.writeCharacteristic(characteristic);
        
    }
    
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic,byte[] data) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        
		ContentFragment.printHexbyte(data);
        characteristic.setValue(data);
        mBluetoothGatt.writeCharacteristic(characteristic);
        
    }
    
    
    
    
    public void readDescriptor(BluetoothGattDescriptor descriptor) {
//        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            Log.w(TAG, "BluetoothAdapter not initialized");
//            return;
//        }
        mBluetoothGatt.readDescriptor(descriptor);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
//        mBluetoothGatt.readRemoteRssi();
        boolean flag = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        Log.e("","setCharacteristicNotification  flag=="+flag +"characteristic.getUuid()="+characteristic.getUuid());
        // This is specific to Heart Rate Measurement.
        if (true) {
        	List<BluetoothGattDescriptor> descriptors=characteristic.getDescriptors();
        	for(BluetoothGattDescriptor ddescriptor : descriptors){
        		Log.e("333333","dddddddescriptor=="+ddescriptor.getUuid());
//        		ddescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        	}
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(BluetoothLEAtrribute.CLIENT_CHARACTERISTIC_CONFIG));
            //Log.e("ZZZZZZZZZEEEEEEEEE","~~~~~setCharacteristicNotification  descriptor=="+descriptor.getUuid());
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
//            dataNum = 0;
            start = System.nanoTime();
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    
    @Override
    public void onCreate() {

    	super.onCreate();
    	Log.e("", "Service is create");
    }
    
    @Override
    public void onDestroy() {

    	super.onDestroy();
    	Log.e("", "Service is Destroy");
    }
    
    
}
