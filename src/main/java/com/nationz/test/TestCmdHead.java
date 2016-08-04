package com.nationz.test;

import android.util.Log;

import com.nationz.bean.HeadReceiveBean;
import com.nationz.bean.TestBean;
import com.nationz.util.HexStringUtil;

import java.util.ArrayList;

public class TestCmdHead extends TestBase {

    /**
     * head 校验
     *
     * @param inData
     * @return
     */
    private byte getHandShake(byte[] inData) {
        Log.e(TAG,"lrc = "+HexStringUtil.byteArrayToHexstring(inData));
        byte lrc = 0;
        for (byte b : inData) {
            lrc ^= b;
        }
        return lrc;
    }

    @Override
    public void init(ArrayList arrList) {
        cmd = 0x01;
    }

    @Override
    public byte[] pack() {
        byte[] sendData = new byte[8];
        System.arraycopy(head, 0, sendData, position, 2);
        position += 2;
        byte[] lenByte = TestUtil.getInstance().getLens(8);
        System.arraycopy(lenByte, 0, sendData, position, 4);
        position += 4;
        sendData[position] = cmd;
        position += 1;
        sendData[position] = getHandShake(sendData);
        Log.i(TAG, "head cmd sendData : "+HexStringUtil.byteArrayToHexstring(sendData));
        return sendData;
    }

    @Override
    public TestBean unPack(byte[] result, int resultDataLen) {
        HeadReceiveBean bean = new HeadReceiveBean();
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
        	Log.e(TAG, "Head cmd unpack: get data lens exception");
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
        // get id
        byte[] id = new byte[2];
        System.arraycopy(result, i, id, 0, 2);
        bean.setId(id);
        i += 2;
        if (rspCode) {
            // get firmware version
            byte[] firmware_version = new byte[8];
            System.arraycopy(result, i, firmware_version, 0, 8);
            bean.setFirmware_version(firmware_version);
            i += 8;
            // get hardware version
            byte[] hardware_version = new byte[8];
            System.arraycopy(result, i, hardware_version, 0, 8);
            bean.setHardware_version(hardware_version);
            i += 8;
            // get key state
            bean.setKeyState(result[i]);
            i += 1;
        }
        // get sn
        byte[] sn = new byte[20];
        System.arraycopy(result, i, sn, 0, 20);
        bean.setSn(sn);
        i += 20;
        // get lrc
        bean.setHeadLrc(result[i]);
        return bean;
    }

}
