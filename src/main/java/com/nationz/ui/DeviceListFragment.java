package com.nationz.ui;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nationz.bean.BluetoothDevices;
import com.nationz.bluetooth.BluetoothManagerForAPP;
import com.nationz.nk_bluetooth.R;

import android.app.Activity;

import android.content.Context;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceListFragment extends ListFragment {
	private final String TAG = getClass().getSimpleName();
	private ListView listV;
	private ArrayList<BluetoothDevices> arrListDevices = new ArrayList<BluetoothDevices>();;
	private BluetoothManagerForAPP btM;
	private OnArticleSelectedListener mListener;

	// static{
	// Log.e("dsds","loadListParrred");
	// arrListDevices = new ArrayList<BluetoothDevices>();
	// List<BluetoothDevices> lsD = getPL();
	//
	// if(lsD != null &&!lsD.isEmpty()){
	// arrListDevices.addAll(lsD);
	// }
	// }
	private List<BluetoothDevices> getPL() {
		List<BluetoothDevices> lsD = null;
		try {
			lsD = BlueToothApplication.getPariedDevice();
			Log.e("ddddd", "the paired lsD size ===" + lsD.size());
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return lsD;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listV = getListView();
		setListAdapter(mListAdapter);
		if (!arrListDevices.isEmpty()) {
			mListAdapter.notifyDataSetChanged();
		}
	}

	public interface OnArticleSelectedListener {
		public void onArticleSelected(BluetoothDevices dev);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (btM != null) {
			btM.stopScanDevices();
		}
		BluetoothDevices dev = (BluetoothDevices) mListAdapter
				.getItem(position);
		mListener.onArticleSelected(dev);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnArticleSelectedListener) activity;
			btM = ((MainActivity) activity).getBTM();
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.devicelist_fragment, container,
				false);

		return v;
	}

	// private void openCurrentBtView(BluetoothDevices dev){
	// mA.openContentList(dev);
	// }

	private BaseAdapter mListAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return arrListDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return arrListDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BluetoothDevices device = arrListDevices.get(position);
			android.util.Log.v("DeviceView", "getView(" + position + "):"
					+ device);
			String name = device.getDeviceName();
			String addr = device.getDeviceAddress();
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (null == arrListDevices || position < 0
					|| position >= arrListDevices.size()) {
				return null;
			}

			View view = null;
			if (null != convertView) {
				view = convertView;
			}
			if (null == view) {
				view = inflater.inflate(R.layout.device_list, parent, false);
			}

			if (null != view && (view instanceof LinearLayout)) {
				TextView nameView = (TextView) view.findViewById(R.id.name);
				TextView addressView = (TextView) view
						.findViewById(R.id.address);
				int color = 0;
				if (device.isPaired()) {
					color = Color.BLUE;
				} else {
					color = Color.BLACK;
				}
				nameView.setTextColor(color);
				nameView.setText(name);
				addressView.setTextColor(color);
				addressView.setText(addr);

				// if (device.isConnected()) {
				// view.setBackgroundColor(Color.GREEN);
				// } else {
				// view.setBackgroundColor(Color.LTGRAY);
				// }
			}
			return view;

		}

	};

	public void addDevice(BluetoothDevices btDevice) {
		// if (!deviceExisted(btDevice))
		arrListDevices.clear();
		synchronized (arrListDevices) {
			if (btDevice.getDeviceName().startsWith("mpos")) {
				arrListDevices.add(btDevice);
				Log.e("xxx", "getDeviceName:" + btDevice.getDeviceName());
			}
		}
		mListAdapter.notifyDataSetChanged();
	}

	public void clearListData() {
		arrListDevices.clear();
		List<BluetoothDevices> lsD = getPL();
		if (lsD != null && lsD.size() > 0) {
			BluetoothDevices d = lsD.get(0);
			Log.e("dds", "d  ????????" + d.isPaired());
		}
		if (lsD != null && !lsD.isEmpty()) {
			arrListDevices.addAll(lsD);
		}
		mListAdapter.notifyDataSetChanged();
	}

	private synchronized boolean deviceExisted(BluetoothDevices device) {
		if (device == null)
			return false;
		Log.e(TAG, "devices ===" + device);
		Iterator<BluetoothDevices> it = arrListDevices.iterator();
		while (it.hasNext()) {
			BluetoothDevices d = it.next();
			if (d != null && device != null
					&& d.getDeviceAddress().equals(device.getDeviceAddress()))
				return true;
		}
		return false;
	}

}
