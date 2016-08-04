package com.nationz.util;

public class EventMSG {

		public final static int MESSAGE_READ = 0;
		public final static int MESSAGE_CONNECTED = 1;
		public final static int MESSAGE_DISCONNECTED = 2;
		public final static int MESSAGE_LE_CONNECTED = 3;
		public final static int MESSAGE_LE_DISCONNECTED = 4;
		public final static int GATT_SERVICES_DISCOVERED = 5;
		
		public byte[] buf;
		public int what;
		
		public EventMSG(int what,byte[] buf){
			this.what = what;
			this.buf = buf;
		}
		public byte[] getBuf() {
			return buf;
		}

		public void setBuf(byte[] buf) {
			this.buf = buf;
		}
	
}
