package com.nationz.ui;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.nationz.bluetooth.LEBluetooth;
import com.nationz.bluetooth.LEBluetoothService;
import com.nationz.nk_bluetooth.R;


import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.textservice.SentenceSuggestionsInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GattActivity extends Activity{
	TextView notifyDataTV,dataMountTV,dataSpeedTV;
	int prop;
	View revView,sendView;
	EditText sendET,sendTimesET;
	public static BluetoothGattCharacteristic characteristic;
	private long startTime = 0L;
	public static int timesForTest = 0;
	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.gatt_layout);
		final Intent intent = getIntent();
		String uuidStr = intent.getStringExtra("CHARA_UUID");
		prop = intent.getIntExtra("CHARA_PROP", 0);
		notifyDataTV = (TextView)findViewById(R.id.dataTV);
		dataMountTV = (TextView)findViewById(R.id.dataMountTV);
		dataSpeedTV = (TextView)findViewById(R.id.dataSpeedTV);
		TextView uuidTV = (TextView)findViewById(R.id.uuidTV);
		revView = (LinearLayout)findViewById(R.id.revLayout);
		sendView = (LinearLayout)findViewById(R.id.sendLayout);
		sendET = (EditText)findViewById(R.id.sendET);
		sendTimesET = (EditText)findViewById(R.id.sendTimesET);
		Button readBtn = (Button)findViewById(R.id.readPropBTN);
		uuidTV.setText("Characteristic uuid:"+uuidStr);
		TextView propTV = (TextView)findViewById(R.id.propTV);
		if((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) >0){
			propTV.setText(propTV.getText()+"Notify Prop:"+prop+"r=="+(prop | BluetoothGattCharacteristic.PROPERTY_NOTIFY));
			revView.setVisibility(View.VISIBLE);
			sendView.setVisibility(View.GONE);
		}
		if((prop & BluetoothGattCharacteristic.PROPERTY_READ)> 0)
			propTV.setText(propTV.getText()+"Read Prop:"+prop+"r="+(prop | BluetoothGattCharacteristic.PROPERTY_READ));
			readBtn.setVisibility(View.VISIBLE);
		if((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) >0 ||(prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)>0){
			propTV.setText(propTV.getText()+"Write Prop:"+prop+"r=="+(prop | BluetoothGattCharacteristic.PROPERTY_WRITE));
			revView.setVisibility(View.GONE);
			sendView.setVisibility(View.VISIBLE);
		}

			propTV.setText(propTV.getText()+"Prop:"+prop);
		
		readBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					LEBluetooth.mBluetoothLeService.readCharacteristic(characteristic);
				}
			});
		
		Button btn = (Button)findViewById(R.id.opBTN);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) >0){
					lostNo.clear();
					timesForTest = 0;
					dataNumTotal = 0;
					LEBluetooth.mBluetoothLeService.setCharacteristicNotification(
                  characteristic, true);
				}
				else if(prop == BluetoothGattCharacteristic.PROPERTY_READ)
					;
				else if((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) >0 ||(prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)>0)
				{
					if(sendTimesET.getText().toString().equals("") || sendET.getText().toString().equals("")){
						Toast.makeText(getApplicationContext(), "请输入正确数据格式",Toast.LENGTH_LONG ).show();
						return;
					}
					final BlueToothApplication btA =(BlueToothApplication)getApplication();
					sendTimes = Integer.parseInt(sendTimesET.getText().toString());
					new Thread(new Runnable() {
						
						@Override
						public void run() {

							while((sendTimes)>0){
//								if(btA.getFlagSendBLE()){
//								 btA.setFlagSendBLE(false);
								 LEBluetooth.mBluetoothLeService.writeCharacteristic(characteristic, Integer.parseInt(sendET.getText().toString()));
								 sendTimes--;
								 SystemClock.sleep(50);
//								}
							}
						}
					}).start();
					
				}
				else 
					;
			}
		});
	}
	static int sendTimes = 0;
	@Override
	protected void onResume() {

		super.onResume();
		IntentFilter intentFilter = new IntentFilter(); 
		intentFilter.addAction(LEBluetoothService.ACTION_DATA_AVAILABLE);
		registerReceiver(mGattReceiver, intentFilter);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
//            case R.id.menu_connect:
////                mBluetoothLeService.connect(mDeviceAddress);
//                return true;
//            case R.id.menu_disconnect:
////                mBluetoothLeService.disconnect();
//                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	DecimalFormat decfmt = new DecimalFormat("##0.00");
	private int dataNumTotal = 0;
	private List<Integer> lostNo= new ArrayList<Integer>();
	private List<Integer> lostNos= new ArrayList<Integer>();
//	private int shouldNo = 0;
	private int testTimes = 0;
	private int lostNums = 0;
	private int lastNo = 1;
    private final BroadcastReceiver mGattReceiver = new BroadcastReceiver() {    
		@Override     
		public void onReceive(Context context, Intent intent) {  
			final String action = intent.getAction();  
			if (LEBluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {     
//				displayData(intent.getStringExtra(LEBluetoothService.EXTRA_DATA));        
				Log.e("GATT", "ACTION_DATA_AVAILABLE");
				timesForTest++;
				
	
				
				long time =	intent.getLongExtra("time", 0);
				if(timesForTest == 2){
					startTime = time;
				}else{
				int dataNum = intent.getIntExtra("num", 0);
				dataNumTotal += dataNum;
				byte[] data = intent.getByteArrayExtra("data");
				int dataNums = data[0];
				int dataNo = data[1];

				if(dataNo < lastNo){
					 testTimes++;
					 lostNo.clear();
				}
				lastNo = dataNo;
				if(dataNums > 0){
					lostNo.add(new Integer(dataNo));
					lostNos.add(new Integer(dataNo));
				}
	            if (data != null && data.length > 0) {
	                final StringBuilder stringBuilder = new StringBuilder(data.length);
	                for(byte byteChar : data)
	                    stringBuilder.append(String.format("%02X ", byteChar));
	                String speed = decfmt.format((float)(dataNumTotal)/((float)((time-startTime)/1000000000)));
	                notifyDataTV.setText(stringBuilder.toString());
	                dataMountTV.setText("dataLen:"+dataNumTotal+" byte");
//	                dataSpeedTV.setText("speed:"+speed+" byte/s"+"\n"+"总包数："+dataNums+"第 "+dataNo+" 包");
	            }
//	            if(dataNums -1 == dataNo){
	            if(dataNums > 0){
	            	 lostNums += (dataNo+1-lostNo.size());
	            	  dataSpeedTV.setText("测试"+testTimes+" 次"+"总包数"+dataNums+" 第 "+dataNo+" 包"+"漏掉"+ (dataNo+1-lostNo.size())  +" 包" +"总丢包数 "+((testTimes-1)*dataNums+dataNo-lostNos.size()));
	            }
//	            }
				
				}
	
}     
		}
		};
    
		protected void onStop() {
			super.onStop();
			unregisterReceiver(mGattReceiver);
		};
}
