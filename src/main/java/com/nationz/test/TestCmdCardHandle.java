package com.nationz.test;

import android.util.Log;

import com.nationz.bean.CardInfo;
import com.nationz.bean.TestBean;

import java.util.ArrayList;

public class TestCmdCardHandle extends TestBase {

	private int type = 0;

	public TestCmdCardHandle(ArrayList arrList) {
		this.type = (int)arrList.get(0);
		cmd = 0x05;
	}

	@Override
	public byte[] pack() {
		// 总长度计算
		int lens = 2 + 4 + 1 + 1 + 8;
		byte[] lenData = TestUtil.getInstance().getLens(lens);
		sendData = new byte[lens];
		System.arraycopy(head, 0, sendData, position, 2);
		position += 2;

		System.arraycopy(lenData, 0, sendData, position, 4);
		position += 4;

		sendData[position] = cmd;
		position += 1;

		switch (type) {
		case 0:
			sendData[position] = 0x00;
			break;
		case 1:
			sendData[position] = 0x01;
			break;
		default:
			break;
		}
		position += 1;

		byte[] lrc = TestUtil.getInstance().getLrc(sendData);
		System.arraycopy(lrc, 0, sendData, position, 8);
		position += 8;

		return sendData;
	}

	@Override
	public TestBean unPack(byte[] result, int resultDataLen) {
		CardInfo bean = new CardInfo();
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
			Log.e(TAG, "card handle cmd unpack: get data lens exception");
            return null;
		}
		// get cmd
		bean.setCmd(result[i]);
		i += 1;
		// get response code
		bean.setRspCode(result[i]);
		if (result[i] != 0) {
			rspCode = false;
			bean.setSccess(false);
			TestUtil.getInstance().codeRsp(result[i]);
		}
		i += 1;
		if (rspCode) {
			bean.setCardType(result[i]);
			i += 1;
			byte trackLen1 = result[i];
			bean.setTrackLen1(trackLen1);
			i += 1;
			byte[] track1 = new byte[trackLen1];
			System.arraycopy(result, i, track1, 0, trackLen1);
			bean.setTrackData1(track1);
			i += trackLen1;

			byte trackLen2 = result[i];
			bean.setTrackLen2(trackLen2);
			i += 1;
			byte[] track2 = new byte[trackLen2];
			System.arraycopy(result, i, track2, 0, trackLen2);
			bean.setTrackData2(track2);
			i += trackLen2;

			byte trackLen3 = result[i];
			bean.setTrackLen3(trackLen3);
			i += 1;
			byte[] track3 = new byte[trackLen1];
			System.arraycopy(result, i, track3, 0, trackLen3);
			bean.setTrackData3(track3);
			i += trackLen3;
		}
		// get lrc
		byte[] lrc = new byte[8];
		System.arraycopy(result, i, lrc, 0, 8);
		bean.setLrc(lrc);
		i += 8;
		return bean;
	}

}
