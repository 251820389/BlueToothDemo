package com.nationz.test;

import android.util.Log;

import com.nationz.bean.GetRandom;
import com.nationz.bean.TestBean;

import java.util.ArrayList;

public class TestCmdGetRandom extends TestBase{
	private int randomLen = 0;
	
    @Override
    public void init(ArrayList arrList) {
    	this.randomLen = (int)arrList.get(0);
    	cmd = 0x0A;
    }

    /**
     * 获取随机数
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

        sendData[position] = (byte) randomLen;
        position += 1;

        byte[] lrc = TestUtil.getInstance().getLrc(sendData);
        System.arraycopy(lrc, 0, sendData, position, 8);

        return sendData;
    }

    public TestBean unPack(byte[] result, int resultDataLen) {
        GetRandom bean = new GetRandom();
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
        	Log.e(TAG, "get random cmd unpack: get data lens exception");
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
            bean.setRandomLens(result[i]);
            byte randomLens = result[i];
            i += 1;

            byte[] random = new byte[randomLens];
            System.arraycopy(result, i, random, 0, randomLens);
            bean.setRandomValue(random);
            i += randomLens & 0xFF;
        }
        // get lrc
        byte[] lrc = new byte[8];
        System.arraycopy(result, i, lrc, 0, 8);
        bean.setLrc(lrc);
        return bean;
    }

}
