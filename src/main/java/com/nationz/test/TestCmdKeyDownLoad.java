package com.nationz.test;

import android.util.Log;

import com.nationz.bean.TestBean;
import com.nationz.util.HexStringUtil;

import java.util.ArrayList;


public class TestCmdKeyDownLoad extends TestBase{

	private String oldKey;
	private String newKey;

	@Override
	public void init(ArrayList arrList) {
		this.oldKey = arrList.get(0).toString();
		this.newKey = arrList.get(1).toString();
		cmd = 0x08;
	}
	/**
	 * 参数设置
	 * 
	 * @return
	 */
	public byte[] pack() {
		// 总长度计算
		int lens = 2 + 4 + 1 + 16 + 16 + 8;
		sendData = new byte[lens];
		byte[] lenData = TestUtil.getInstance().getLens(lens);
		System.arraycopy(head, 0, sendData, position, 2);
		position += 2;

		System.arraycopy(lenData, 0, sendData, position, 4);
		position += 4;

		sendData[position] = cmd;
		position += 1;

		byte[] oldKeyByte = HexStringUtil.hexStringToBytes(oldKey);
		System.arraycopy(oldKeyByte,0,sendData,position,16);
		position += 16;

		byte[] newKeyByte = HexStringUtil.hexStringToBytes(newKey);
		System.arraycopy(newKeyByte,0,sendData,position,16);
		position += 16;

		byte[] lrc = TestUtil.getInstance().getLrc(sendData);
		System.arraycopy(lrc, 0, sendData, position, 8);

		return sendData;
	}

	/**
	 * pack head respond
	 */
	@Override
	public TestBean unPack(byte[] result,int resultDataLen) {
		TestBean bean = new TestBean();
		// get head
		byte[] head = new byte[2];
		System.arraycopy(result, i, head, 0, 2);
		bean.setHead(head);
		i += 2;
		// get len
		byte[] dataLen = new byte[4];
		System.arraycopy(result, i, dataLen, 0, 4);
		// TODO 判断后续值长度是否与dataLen（高低位记得交换）一致
		int lens = TestUtil.getInstance().getLens(dataLen);
		bean.setDataLen(lens);
		i += 4;
		if (lens != resultDataLen) {
			Log.e(TAG, "set args cmd: get data lens exception");
			return null;
		}
		// get cmd
		bean.setCmd(result[i]);
		i += 1;
		// get response code
		bean.setRspCode(result[i]);
		if (result[i] != 0) {
			// TODO 响应状态 不为0，失败
			bean.setSccess(false);
			TestUtil.getInstance().codeRsp(result[i]);
		}
		i += 1;
		// get lrc
		byte[] lrc = new byte[8];
		System.arraycopy(result, i, lrc, 0, 8);
		bean.setLrc(lrc);
		return bean;
	}

}
