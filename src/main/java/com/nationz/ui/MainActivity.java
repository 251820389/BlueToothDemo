package com.nationz.ui;

import java.text.DecimalFormat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.nationz.bean.BluetoothDevices;
import com.nationz.bluetooth.BluetoothInterface;
import com.nationz.bluetooth.BluetoothManagerForAPP;
import com.nationz.bluetooth.ClassicBluetooth;
import com.nationz.bluetooth.ClassicConnectedThread;
import com.nationz.bluetooth.LEBluetooth;
import com.nationz.bluetooth.LEBluetoothService;
import com.nationz.nk_bluetooth.R;
import com.nationz.test.TestCmdHead;
import com.nationz.ui.DeviceListFragment.OnArticleSelectedListener;

public class MainActivity extends FragmentActivity  implements BluetoothInterface.ScanCallback,OnArticleSelectedListener{
	private RadioButton rbCls,rbLe;
	private BluetoothManagerForAPP btMangerForAPP;
	private boolean leMode = false;
	DeviceListFragment fgList;
	Button scanBtn,visibleBtn;
	RadioGroup rg;
	Handler mHandler;
	ContentFragment cF;
	public final static int MESSAGE_READ = 0;
	public final static int MESSAGE_CONNECTED = 1;
	public final static int MESSAGE_DISCONNECTED = 2;
	public final static int MESSAGE_LE_CONNECTED = 3;
	public final static int MESSAGE_LE_DISCONNECTED = 4;
	public final static int GATT_SERVICES_DISCOVERED = 5;
	public final static int MESSAGE_CONNECTED_FAILED = 6;
	public static long TimerForStart=0l;
	public static int timesFT = 0;
	ServiceConnection conn;
	TextView tv;
	Context cContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initHandler();
		IntentFilter intentFilter = new IntentFilter(); 
//		intentFilter.addAction(LEBluetoothService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(LEBluetoothService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(LEBluetoothService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(LEBluetoothService.EXTRA_DATA);
		intentFilter.addAction(LEBluetoothService.ACTION_GATT_SERVICES_DISCOVERED);
		registerReceiver(mGattUpdateReceiver, intentFilter); 
		if(leMode){
		conn = LEBluetooth.getServiceConn();
		bindService(new Intent(this,LEBluetoothService.class), conn, Context.BIND_AUTO_CREATE);
		}
		cContext = this;
//		startService(new Intent(this,LEBluetoothService.class));
		getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {

				Log.e("", "scanBtn.isShown() ==="+scanBtn.isShown());
				if(!scanBtn.isShown()){
					scanBtn.setVisibility(View.VISIBLE);
				}else{
					scanBtn.setVisibility(View.GONE);
				}
			}
		});
	}

	DecimalFormat decfmt = new DecimalFormat("##0.00");
	
	public void initHandler(){
		if(mHandler != null){
			return;
		}
		mHandler = new Handler(){
			public void handleMessage(Message msg){
				switch (msg.what) {
				case MESSAGE_READ:
					timesFT++;
					byte[] buffer = (byte[])msg.obj;
//					printHexbyte(buffer);
					int bytes = msg.arg1;
//					btMangerForAPP.sendNotifaction(bytes);
					if(timesFT == 1){
						TimerForStart = ClassicConnectedThread.resultTimer;
						cF.setTV("",buffer,bytes,0);
					}else{
						float time = (float)( ClassicConnectedThread.resultTimer- TimerForStart)/1000000000;
						String speed = decfmt.format((float)(bytes*(timesFT -1))/time);
						//Log.e("","(float)msg.arg2/1000000 ==="+(float)msg.arg2/1000000000);
						cF.setTV(speed,buffer,bytes,time);
					}
					break;
				case MESSAGE_CONNECTED:
					cF.setCbtn("Disconnect",true);
					long connetTime = ((Long)msg.obj).longValue()/1000000;
					tv.setText("SPP conneted time:"+connetTime +"ms");
					break;
				case MESSAGE_DISCONNECTED:
					cF.setCbtn("Connect",true);
					tv.setText("");
					break;
				case MESSAGE_CONNECTED_FAILED:
					cF.setBtnEnable();
					break;
				default:
					break;
				}
			}
		};
	}
	public BluetoothManagerForAPP getBTM(){
		if(btMangerForAPP != null){
			return btMangerForAPP;
		}
		return null;
		
	}
	
	public void openContentList(){
		
	}
	
	private void initView(){
		tv = (TextView)findViewById(R.id.tv2);
		rbCls = (RadioButton)findViewById(R.id.classicRB);
		rbLe = (RadioButton)findViewById(R.id.leRB);
		scanBtn = (Button)findViewById(R.id.scanBtn);
		visibleBtn = (Button)findViewById(R.id.visibleBtn);
		rg = (RadioGroup)findViewById(R.id.rg);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				btMangerForAPP.closeBt();
			}
			
		} );
		visibleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(!ClassicBluetooth.doRequestDiscoverable()){
					Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); 
					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); 
					startActivity(discoverableIntent);
				}
				btMangerForAPP.openServer(mHandler);
//				btMangerForAPP.sendNotifaction(); 
			}
		});
		
		scanBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				scanBtn.setEnabled(false);
				
				boolean leMode = false;
				if(fgList == null)
					fgList = new DeviceListFragment();
				if(!fgList.isAdded())
					//getSupportFragmentManager().beginTransaction().add(fgList, null).commit();
					getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout,fgList).commit();
				if(rg.getCheckedRadioButtonId() == R.id.leRB){
					leMode = true;
				}else{
					leMode = false;
				}
				fgList.clearListData();
				btMangerForAPP.scanDevices(cContext,mHandler,leMode);
				
			}
		});
		
		if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			rbLe.setVisibility(View.GONE);
			leMode = false;
		}else{
			leMode = true;
		}
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		if(btMangerForAPP == null){
			btMangerForAPP = BluetoothManagerForAPP.getInstance();
			btMangerForAPP.openBluetooth(this);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public void findDeviceCallbak(BluetoothDevice device) {

		BluetoothDevices blD = new BluetoothDevices();
		blD.setDeviceName(device.getName());
		blD.setDeviceAddress(device.getAddress());
		blD.setDevice(device);
		fgList.addDevice(blD);
	}

	@Override
	public void scanFinishedCallbak() {

		scanBtn.setEnabled(true);
	}

	@Override
	public void onArticleSelected(BluetoothDevices dev) {

		//scanBtn.setVisibility(View.GONE);
		cF = ContentFragment.newInstance(dev);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); 
		transaction.replace(R.id.fragmentLayout, cF);
		transaction.addToBackStack(null); 
		transaction.commit();
		
	}

	
	
	@Override
	protected void onStop() {

		super.onStop();
		
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver);
		btMangerForAPP.closeBt();
		if(leMode)
			unbindService(conn);
	}
	
	public synchronized static void printHexbyte(byte[] bytes) {
		String s = "";
		Log.e("", "bytes.length===="+bytes.length);
		try {
			for (int i = 0; i < bytes.length; i++) {
				s += String.format("%02X ", bytes[i]);
			}
			Log.e("debug", s);
		} catch (Exception e) {
			// result Data is Null pointer
		}

	}
	
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {    
		@Override     
		public void onReceive(Context context, Intent intent) {         
			final String action = intent.getAction();         
			if (LEBluetoothService.ACTION_GATT_CONNECTED.equals(action)) {           
//				mConnected = true;            
//				updateConnectionState(R.string.connected);     
				cF.setCbtn("Disconnect",false);
				long result = intent.getLongExtra("RTIME", 0)/1000000;
				tv.setText("LE conneted time:"+result +"ms");
//				invalidateOptionsMenu();       
				} 
			else if (LEBluetoothService.ACTION_GATT_DISCONNECTED.equals(action)) {
//					mConnected = false;            
//					updateConnectionState(R.string.disconnected);  
					cF.setCbtn("Connect",false);
					Log.e("", "sadadsada");
					tv.setText("");
//					invalidateOptionsMenu();            
//					clearUI();        
					} 
			else if (LEBluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {             
						// Show all the supported services and characteristics on the            
						// user interface.             
//						displayGattServices(mBluetoothLeService.getSupportedGattServices()); 

				Log.e("","gattServices ===");
				if(cF != null)
					cF.displayGattServices(LEBluetooth.getServices());
						} 
//			else if (LEBluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {     
////							displayData(intent.getStringExtra(LEBluetoothService.EXTRA_DATA));        
//				Log.e("", "ACTION_DATA_AVAILABLE");
//				
//				
//					String s = intent.getStringExtra(LEBluetoothService.EXTRA_DATA);
//				
//					Log.e("", "ACTION_DATA_AVAILABLE =="+s);
//				
//				
//			}     
			} 
		};
			
	
	
	
	
}
