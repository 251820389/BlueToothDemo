package com.nationz.test;

import android.util.Log;

import com.nationz.bean.TestBean;
import com.nationz.util.HexStringUtil;

import java.util.ArrayList;

public abstract class TestBase {
	protected byte[] sendData;
	protected byte[] head = { (byte) 0xaa, (byte) 0x55 };
	protected byte[] lenByte = new byte[4];
	protected byte cmd;
	protected int position = 0;
	protected int i = 0;
	protected byte[] lrc = new byte[8];
	protected boolean rspCode = true;
	public final static String TAG = "TESTCMD";

	public byte[] sendData(ArrayList arrList) {
		init(arrList);
		sendData = pack();
		Log.i(TAG,"send data = "+ HexStringUtil.byteArrayToHexstring(sendData));
		return sendData;
	}

	public abstract void init(ArrayList arrList);

	/**
	 * if you Need to set param, should call setData()
	 * 
	 * @return byte[] sendData
	 */
	public abstract byte[] pack();
	
	public abstract TestBean unPack(byte[] result, int resultDataLen);
	public TestBean receive(byte[] result, int resultDataLen){
		TestBean bean = unPack(result,resultDataLen);
		release();
		return bean;
	}
	/**
	 * release resouse
	 */
	protected void release() {
		sendData = null;
		lenByte = null;
		lrc = null;
		rspCode = true;
		position = 0;
		i = 0;
	}
	/**
	 * set param (type or lens or data)
	 * 
	 * @param type
	 */
	/*
	 * public void setData(int data) {
	 * 
	 * }
	 *//**
	 * set param
	 * 
	 * @param data
	 */
	/*
	 * public void setData(String data) {
	 * 
	 * }
	 *//**
	 * set param
	 * 
	 * @param data
	 * @param type
	 *            (type or lens)
	 */
	/*
	 * public void setData(String data,int type) {
	 * 
	 * }
	 */
}
