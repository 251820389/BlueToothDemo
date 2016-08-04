package com.nationz.util;

import java.util.HashMap;

public class BluetoothLEAtrribute {
	private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
//    static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    static final String GATT_SERVICE = "0000ff00-0000-1000-8000-00805f9b34fb";
    public static final String GATT_READ_CHARACTERISTIC = "0000ff01-0000-1000-8000-00805f9b34fb";
    public static final String GATT_WRITE_CHARACTERISTIC = "0000ff02-0000-1000-8000-00805f9b34fb";
    static final String GATT_MTU_CHARACTERISTIC = "0000ff03-0000-1000-8000-00805f9b34fb";
    static {
        // Sample Services.
    	attributes.put(GATT_SERVICE, "GATT_SERVICE");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put("0000ff01-0000-1000-8000-00805f9b34fb", "read");
        attributes.put("0000ff02-0000-1000-8000-00805f9b34fb", "write");
        attributes.put("0000ff03-0000-1000-8000-00805f9b34fb", "mtu");

    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
