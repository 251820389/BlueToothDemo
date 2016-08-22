package com.nationz.test;

import android.util.Log;

import com.nationz.bean.IcCardInfo;
import com.nationz.bean.TestBean;

import java.util.ArrayList;

public class TestCmdIcCard extends TestBase {

	/*
	 * ApduSend.CLA = 0; ApduSend.INS = 0xA4; ApduSend.P1 = 4; ApduSend.P2 = 0;
	 * ApduSend.Lc = 0x0E; ApduSend.Le = 0;
	 * 
	 * ApduSend.CLA = 0; ApduSend.INS = 0xA4; ApduSend.P1 = 4; ApduSend.P2 = 0;
	 * ApduSend.Lc = 0x80; ApduSend.Le = 0;
	 */
	public TestCmdIcCard() {
		cmd = 0x06;
	}

	/**
	 * IC卡命令包
	 * 
	 * @return
	 */
	@Override
	public byte[] pack() {
		// 总长度计算
		int lens = 2 + 4 + 1 + 4 + 6 + 8;
		// int lens = 2 + 4 + 1 + 20 + 8;
		lenByte = TestUtil.getInstance().getLens(lens);
		byte[] sendData = new byte[lens];
		System.arraycopy(head, 0, sendData, position, 2);
		position += 2;

		System.arraycopy(lenByte, 0, sendData, position, 4);
		position += 4;

		sendData[position] = cmd;
		position += 1;
		byte[] cmdData  = { (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00,
					(byte) 0x0E, (byte) 0x00 };
//			byte[] cmdData = { (byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00,
//					(byte) 0x80, (byte) 0x00 };
		byte[] cmdDataLens = TestUtil.getInstance().getLens(cmdData.length);
		System.arraycopy(cmdDataLens, 0, sendData, position, cmdDataLens.length);
		position += cmdDataLens.length;

		System.arraycopy(cmdData, 0, sendData, position, cmdData.length);
		position += cmdData.length;

		byte[] lrc = TestUtil.getInstance().getLrc(sendData);
		System.arraycopy(lrc, 0, sendData, position, 8);
		return sendData;
	}

	@Override
	public TestBean unPack(byte[] result, int resultDataLen) {
		IcCardInfo bean = new IcCardInfo();
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
			Log.e(TAG, "set sn cmd unpack: get data lens exception");
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
			byte[] cmdLens = new byte[4];
			System.arraycopy(result, i, cmdLens, 0, 4);
			int len = TestUtil.getInstance().getLens(cmdLens);
			bean.setCmdInfoLens(len);
			i += 4;
			byte[] rspData = new byte[len];
			System.arraycopy(result, i, rspData, 0, len);
			bean.setRspInfo(rspData);
			i += len;
		}
		// get lrc
		byte[] lrc = new byte[8];
		System.arraycopy(result, i, lrc, 0, 8);
		bean.setLrc(lrc);
		return bean;
	}

}
