package com.nationz.test;

import android.util.Log;

import com.nationz.bean.IcCardExists;
import com.nationz.bean.IcCardPower;
import com.nationz.bean.TestBean;

import java.util.ArrayList;

public class TestCmdIcCardpower extends TestBase {

	private int power_status = 0;

	public TestCmdIcCardpower(int power_statuss) {
		this.power_status = power_statuss;
		cmd = 0x0C;
	}

	/**
	 * IC卡电源
	 * 
	 * @return
	 */
	public byte[] pack() {
		// 总长度计算
		int lens = 2 + 4 + 1 + 1 + 8;
		lenByte = TestUtil.getInstance().getLens(lens);
		byte[] sendData = new byte[lens];
		System.arraycopy(head, 0, sendData, position, 2);
		position += 2;

		System.arraycopy(lenByte, 0, sendData, position, 4);
		position += 4;

		sendData[position] = cmd;
		position += 1;

		sendData[position] = (byte) power_status;
		position += 1;

		byte[] lrc = TestUtil.getInstance().getLrc(sendData);
		System.arraycopy(lrc, 0, sendData, position, 8);
		return sendData;
	}

	public TestBean unPack(byte[] result, int resultDataLen) {
		IcCardPower bean = new IcCardPower();
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
			Log.e(TAG,
					"cmd IC card power on or power off: get data lens exception");
			return null;
		}
		// get cmd
		bean.setCmd(result[i]);
		i += 1;
		// get response code
		bean.setRspCode(result[i]);
		if (result[i] != 0) {
			// TODO 响应状态 不为0，失败
			rspCode = false;
			bean.setSccess(false);
			TestUtil.getInstance().codeRsp(result[i]);
		}
		i += 1;
		if (rspCode) {
			// ic card status
			bean.setFlightReq(result[i]);
			i += 1;
			byte atrLen = result[i];
			bean.setFlightReq(atrLen);
			i+=1;
			byte[] ATR = new byte[atrLen];
			System.arraycopy(result, i, ATR, 0, atrLen);
			bean.setATR(ATR);
			i += atrLen;
		}

		// get lrc
		byte[] lrc = new byte[8];
		System.arraycopy(result, i, lrc, 0, 8);
		bean.setLrc(lrc);
		return bean;
	}

}
