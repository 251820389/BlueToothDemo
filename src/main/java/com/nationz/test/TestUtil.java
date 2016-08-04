package com.nationz.test;

import java.util.Arrays;

import android.util.Log;

import com.nationz.util.DESUtils;
import com.nationz.util.HexStringUtil;

public class TestUtil {
	
	public static final int NAME = 0;
	public static final int MID = 1;
	public static final int TID = 2;
	public static final String KEY = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
	private static TestBase testCmd = null;
	private static TestUtil testUtil;
	public static TestUtil getInstance(){
		if(testUtil == null){
			testUtil = new TestUtil();
		}
		return testUtil;
	}
	/**
	 * pack len
	 * @param len
	 * @return
	 */
	public byte[] getLens(int len) {
		byte[] newLen = new byte[4];
		String hexStrLen = Integer.toHexString(len);
		byte[] temp = new byte[4];
		byte[] byteLen = HexStringUtil.hexStringToBytes(hexStrLen);
		System.arraycopy(byteLen, 0, temp, 4-byteLen.length, byteLen.length);
		for (int i = 0; i < 4; i++) {
			newLen[4 - i - 1] = temp[i];
		}
		return newLen;
	}
	/**
	 * unpack len
	 * @param len
	 * @return
	 */
	public int getLens(byte[] len) {
		byte[] newLen = new byte[4];
		for (int i = 0; i < 4; i++) {
			newLen[4 - i - 1] = len[i];
		}
		return Integer.parseInt(HexStringUtil.byteArrayToHexstring(newLen),16);
	}
	/**
	 * 校验
	 * 
	 * @param inData
	 * @return
	 */
	public byte[] getLrc(byte[] inData) {
		byte[] result = new byte[8];
		byte[] temp = null;
		int len = inData.length;
		int a = len / 8;
		if (len % 8 != 0) {
			a += 1;
			temp = new byte[len + (8 - len % 8)];
		}else{
			temp = new byte[len];
		}
		System.arraycopy(inData, 0, temp, 0, len);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < a; j++) {
				result[i] ^= temp[8 * j + i];
			}
		}
		Log.e("xxx", "3des key = "+Arrays.toString(DESUtils.strToBytes(KEY)));
		return DESUtils.TripDesEncrypt(result, DESUtils.strToBytes(KEY));
	}
	
	public String codeRsp(byte code){
		String resultStr = "";
		switch (code) {
		case 0:
			resultStr = "操作成功";
			break;
		case 1:
			resultStr = "无效参数";
			break;
		case 2:
			resultStr = "校验错";
			break;
		case 3:
			resultStr = "旧密钥错误";
			break;
		case 4:
			resultStr = "新密钥错误";
			break;
		case 5:
			resultStr = "系统升级失败";
			break;
		case 6:
			resultStr = "重复设置SN号";
			break;
		default:
			resultStr = "其他错误";
			break;
		}
		Log.e(TestBase.TAG, "send error msg = "+resultStr);
		return resultStr;
	}

	public byte[] addZero(byte[] data){
		byte[] result = new byte[20];
		if(data.length < 20){
			System.arraycopy(data,0,result,0,data.length);
		}else{
			return data;
		}
		return result;
	}
}
