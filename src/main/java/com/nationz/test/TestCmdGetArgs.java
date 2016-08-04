package com.nationz.test;

import android.util.Log;

import com.nationz.bean.GetArgs;
import com.nationz.bean.TestBean;

import java.util.ArrayList;

public class TestCmdGetArgs extends TestBase {
	private int type = 0;

	/**
	 * set param 0:营运商名 1:商户号 2:终端号
	 */
	@Override
	public void init(ArrayList arrList) {
		this.type = (int)arrList.get(0);
		cmd = 0x03;
	}

	/**
	 * 参数设置 first:Need to call setData()
	 * 
	 * @return
	 */
	@Override
	public byte[] pack() {
		// 总长度计算
		int lens = 2 + 4 + 1 + 1 + 8;
		byte[] lenData = TestUtil.getInstance().getLens(lens);
		byte[] sendData = new byte[lens];
		System.arraycopy(head, 0, sendData, position, 2);
		position += 2;

		System.arraycopy(lenData, 0, sendData, position, 4);
		position += 4;

		sendData[position] = cmd;
		position += 1;

		switch (type) {
		case 0:
			sendData[position] = 0x01;
			break;
		case 1:
			sendData[position] = 0x02;
			break;
		case 2:
			sendData[position] = 0x03;
			break;
		default:
			break;
		}
		position += 1;

		byte[] lrc = TestUtil.getInstance().getLrc(sendData);
		System.arraycopy(lrc, 0, sendData, position, 8);

		return sendData;
	}

	/**
	 * pack head respond
	 */
	@Override
	public TestBean unPack(byte[] result, int resultDataLen) {
		GetArgs bean = new GetArgs();
		// get head
		byte[] head = new byte[2];
		System.arraycopy(result, i, head, 0, 2);
		bean.setHead(head);
		i += 2;
		// get len
		byte[] dataLen = new byte[4];
		System.arraycopy(result, i, dataLen, 0, 4);
		int lens = TestUtil.getInstance().getLens(dataLen);
		bean.setDataLen(lens);
		i += 4;
		if (lens != resultDataLen) {
			Log.e(TAG, "get args cmd unpack: get data lens exception");
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
			int argLen = result[i];
			bean.setArgLen((byte) argLen);
			i += 1;
			bean.setArgId(result[i]);
			i += 1;
			byte[] argData = new byte[argLen];
			System.arraycopy(result, i, argData, 0, argLen);
			bean.setArgData(argData);
			i += argLen;
		}
		byte[] lrc = new byte[8];
		System.arraycopy(result, i, lrc, 0, 8);
		bean.setLrc(lrc);
		i += 8;
		return bean;
	}

}
