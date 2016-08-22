package com.nationz.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nationz.bean.BluetoothDevices;
import com.nationz.bean.InjectKeyRequestBean;
import com.nationz.bluetooth.BluetoothManagerForAPP;
import com.nationz.nk_bluetooth.R;
import com.nationz.test.TestBase;
import com.nationz.test.TestCmdGetArgs;
import com.nationz.test.TestCmdGetRandom;
import com.nationz.test.TestCmdHead;
import com.nationz.test.TestCmdIcCard;
import com.nationz.test.TestCmdIcCardExist;
import com.nationz.test.TestCmdIcCardpower;
import com.nationz.test.TestCmdKeyDownLoad;
import com.nationz.test.TestCmdKeyInject;
import com.nationz.test.TestCmdKeyUse;
import com.nationz.test.TestCmdSetArgs;
import com.nationz.test.TestCmdSetSn;
import com.nationz.test.TestCmdSwitch;
import com.nationz.test.TestUtil;
import com.nationz.util.BluetoothLEAtrribute;
import com.nationz.util.HexStringUtil;

public class ContentFragment extends Fragment {
    private BluetoothDevices devF;
    private ContentFragment f;
    private View v;
    private BluetoothManagerForAPP btM;
    private EditText edDataLen, edDataTimes;
    private Button connectBtn;
    private TextView speedTV, dataTV, dataNumTv, sendDataText;
    private View classiview;
    private ExpandableListView eLV;
    private Spinner spinner;
    public final static String LIST_NAME = "name";
    public final static String LIST_UUID = "id";
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private ArrayList arrayList = null;
    private byte[] data;
    private TestBase cmdBean;

    public void setBtnEnable() {
        connectBtn.setEnabled(true);
    }

    public void setCbtn(String btnStr, boolean flag) {
        if (btnStr.equals("Disconnect")) {
            connectBtn.setEnabled(true);
            connectBtn.setText(btnStr);
            if (flag)
                classiview.setVisibility(View.VISIBLE);
            devF.setConnected(true);
            BlueToothApplication.putPiredDevice(devF);
        } else {
            connectBtn.setEnabled(true);
            connectBtn.setText(btnStr);
            devF.setConnected(false);
            if (flag)
                classiview.setVisibility(View.GONE);
            else
                clearUI();
        }

    }

    DecimalFormat decfmt = new DecimalFormat("##0.00");
    //	private int dataTotalLen = 0;
    //	private float totalTime = 0;

    // TODO 接收数据
    public void setTV(String speedStr, byte[] dataStr, int dataLen, float time) {
        //		dataTotalLen += dataLen;
        String recieveData = HexStringUtil.byteArrayToHexstring(dataStr, 0, dataLen);
        //		dataNumTv.setText(recieveData);
        Log.e("xxx", "recieveData = " + recieveData);
        dataNumTv.setText(cmdBean.receive(dataStr, dataLen).toString());
        //		totalTime += time;
        // String speedTol = decfmt.format((float)dataTotalLen/totalTime);
        speedTV.setText("receive speed:" + speedStr + " byte/s");
        String dataS = "";
        for (int i = 0; i < dataLen; i++) {
            // dataS += String.format("%02X ", dataStr[i]);
        }
        System.out.println("dataS" + dataS);
        dataTV.setText(dataS);
    }

    public static ContentFragment newInstance(BluetoothDevices dev) {
        ContentFragment f = new ContentFragment();

        f.setDev(f, dev);
        return f;
    }

    private void setDev(ContentFragment f, BluetoothDevices dev) {
        this.f = f;
        this.devF = dev;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.content_fragment, container, false);
        // if(devF.isConnected()){
        // connectBtn.setText("Disconnect");
        // }
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        btM = ((MainActivity) activity).getBTM();
    }

    // private LEBluetoothService mBluetoothLeService;
    // If a given GATT characteristic is selected, check for supported features.
    // This sample
    // demonstrates 'Read' and 'Notify' features. See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for
    // the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (mGattCharacteristics != null) {
                final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                GattActivity.characteristic = characteristic;
                final int charaProp = characteristic.getProperties();
                Log.e("", "characteristic.getUuid();==" + characteristic.getUuid() + "charaProp ===" + charaProp + "charaPermission @=" + characteristic.getPermissions());
                Intent intent = new Intent(getActivity(), GattActivity.class);
                intent.putExtra("CHARA_PROP", charaProp);
                intent.putExtra("CHARA_UUID", characteristic.getUuid().toString());
                startActivity(intent);
                // if ((charaProp == BluetoothGattCharacteristic.PROPERTY_READ)
                // ) {
                // // If there is an active notification on a characteristic,
                // clear
                // // it first so it doesn't update the data field on the user
                // interface.
                // if (mNotifyCharacteristic != null) {
                // LEBluetooth.mBluetoothLeService.setCharacteristicNotification(
                // mNotifyCharacteristic, false);
                // mNotifyCharacteristic = null;
                // }
                // Log.e("", "readCharacteristic");
                // LEBluetooth.
                // mBluetoothLeService.readCharacteristic(characteristic);
                //
                // }
                // if ((charaProp ==
                // BluetoothGattCharacteristic.PROPERTY_NOTIFY) ) {
                // mNotifyCharacteristic = characteristic;
                // LEBluetooth.mBluetoothLeService.setCharacteristicNotification(
                // characteristic, true);
                // }
                return true;
            }
            return false;
        }
    };

    // int times;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        sendDataText = (TextView) v.findViewById(R.id.sendData);
        dataNumTv = (TextView) v.findViewById(R.id.dataNumTV);
        speedTV = (TextView) v.findViewById(R.id.speedTV);
        dataTV = (TextView) v.findViewById(R.id.dataTV);
        TextView tvName = (TextView) v.findViewById(R.id.devName);
        Button backBtn = (Button) v.findViewById(R.id.backBtn);
        classiview = (View) v.findViewById(R.id.classicZone);
        eLV = (ExpandableListView) v.findViewById(R.id.gatt_services_list);
        spinner = (Spinner) v.findViewById(R.id.spinner);
        eLV.setOnChildClickListener(servicesListClickListner);
        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        connectBtn = (Button) v.findViewById(R.id.conBtn);
        connectBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.timesFT = 0;
                if (devF.isConnected()) {
                    btM.disConnectDevice(devF);
                } else {
                    //					dataTotalLen = 0;
                    //					totalTime = 0;
                    btM.connectDevice(devF);
                    connectBtn.setEnabled(false);
                }

            }
        });
        tvName.setText(devF.getDeviceName() + " : " + devF.getDeviceAddress());
        Button sendBtn = (Button) v.findViewById(R.id.sendBtn);
        final String name = "六合通讯";
        final String TID = "987654321098765";
        final String MID = "12345678";
        final String SN = "1234567890123456";
        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                arrayList = new ArrayList();
                // times = Integer.parseInt(edDataTimes.getText().toString());
                // TODO 发送数据
                final int position = spinner.getSelectedItemPosition();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        switch (position) {
                            case 0:// 握手
                                cmdBean = new TestCmdHead();
                                data = cmdBean.sendData();
                                break;
                            case 1://set args
                                cmdBean = new TestCmdSetArgs(name,TestUtil.NAME);
                                data = cmdBean.sendData();
                                break;
                            case 2:
                                cmdBean = new TestCmdSetArgs(MID,TestUtil.MID);
                                data = cmdBean.sendData();
                                break;
                            case 3:
                                cmdBean = new TestCmdSetArgs(TID,TestUtil.TID);
                                data = cmdBean.sendData();
                                break;
                            case 4:
                                cmdBean = new TestCmdGetArgs(TestUtil.NAME);
                                data = cmdBean.sendData();
                                break;
                            case 5:
                                cmdBean = new TestCmdGetArgs(TestUtil.MID);
                                data = cmdBean.sendData();
                                break;
                            case 6:
                                cmdBean = new TestCmdGetArgs(TestUtil.TID);
                                data = cmdBean.sendData();
                                break;
                            case 7:
                                cmdBean = new TestCmdSetSn(SN);
                                data = cmdBean.sendData();
                                break;
                            case 8:
                                cmdBean = new TestCmdGetRandom(8);
                                data = cmdBean.sendData();
                                break;
                            case 9:// IC card exists
                                cmdBean = new TestCmdIcCardExist();
                                data = cmdBean.sendData();
                                break;
                            case 10://0 power on; 1 power off
                                cmdBean = new TestCmdIcCardpower(0);
                                data = cmdBean.sendData();
                                break;
                            case 11:// IC card cmd
                                cmdBean = new TestCmdIcCard();
                                data = cmdBean.sendData();
                                break;
                            case 12://注入密钥
                                InjectKeyRequestBean injectKeyRequestBean = new InjectKeyRequestBean();
                                injectKeyRequestBean.setKeyId(1);
                                injectKeyRequestBean.setDecryption_algorithm(12);
                                injectKeyRequestBean.setDecodeKeyId(1);
                                injectKeyRequestBean.setKeyData("11111111111111111111111111111111");
                                injectKeyRequestBean.setKeyLrcAlgorithm(2);
                                injectKeyRequestBean.setKeyData("11111111111111111111111111111111");
                                cmdBean = new TestCmdKeyInject(injectKeyRequestBean);
                                data = cmdBean.sendData();
                                break;
                            case 13://使用密钥
                                cmdBean = new TestCmdKeyUse(1,3,"11111111111111111111111111111111");
                                data = cmdBean.sendData();
                                break;
                            case 14://Set up the debug switch
                                //1 open  0 close
                                cmdBean = new TestCmdSwitch(1);
                                data = cmdBean.sendData();
                                break;
                            case 15://updata system
                                break;
                            case 16://download key
                                cmdBean = new TestCmdKeyDownLoad("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF","11111111111111111111111111111111");
                                data = cmdBean.sendData();
                                break;
                            default:
                                Log.e(TestBase.TAG, "暂不支持此命令");
                                data = null;
                                break;
                        }
                        if (data == null) {
                            return;
                        }
                        Log.i(TestBase.TAG, "send data = " + HexStringUtil.byteArrayToHexstring(data));
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                sendDataText.setText(HexStringUtil.byteArrayToHexstring(data));
                            }
                        });
                        btM.sendData(data);
                        Thread.currentThread();
                        try {
                            Thread.sleep(320);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView on the UI.
    public void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null || v == null)
            return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            Log.e("", "@@@@@@@@@@Service uuid ====" + uuid);
            currentServiceData.put(LIST_NAME, BluetoothLEAtrribute.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                Log.e("", "!!!!!!!!!!!!!!!!!Characteristic uuid ====" + uuid);
                currentCharaData.put(LIST_NAME, BluetoothLEAtrribute.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(this.getActivity(), gattServiceData, android.R.layout.simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1, android.R.id.text2}, gattCharacteristicData, android.R.layout.simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1, android.R.id.text2});
        eLV.setAdapter(gattServiceAdapter);
    }

    private void clearUI() {
        eLV.setAdapter((SimpleExpandableListAdapter) null);
        // mDataField.setText(R.string.no_data);
    }

    public synchronized static void printHexbyte(byte[] bytes) {
        String s = "";
        Log.e("", "bytes.length====" + bytes.length);
        try {
            for (int i = 0; i < bytes.length; i++) {
                s += String.format("%02X ", bytes[i]);
            }
            Log.e("debug", s);
        } catch (Exception e) {
            // result Data is Null pointer
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v = null;
    }

    public void setSendData(byte[] data) {
        this.data = data;
    }

}
