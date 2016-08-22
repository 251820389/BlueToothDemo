package com.nationz.test;

import android.util.Log;

import com.nationz.bean.KeyUse;
import com.nationz.bean.TestBean;
import com.nationz.util.HexStringUtil;

import java.util.ArrayList;


public class TestCmdKeyUse extends TestBase {

    private int keyID;
    private int algorithm;
    private String keyData;

    public TestCmdKeyUse(int keyID,int algorithm,String keyData) {
        this.keyID = keyID;
        this.algorithm = algorithm;
        this.keyData = keyData;
        cmd = 0x0F;
    }

    /**
     * 参数设置
     *
     * @return
     */
    public byte[] pack() {
        byte[] keyDataByte = HexStringUtil.hexStringToBytes(keyData);
        int keyDataLen = keyDataByte.length;
        // 总长度计算
        int lens = 2 + 4 + 1 + 1 + 1 + 1 + keyDataLen + 8;
        sendData = new byte[lens];
        byte[] lenData = TestUtil.getInstance().getLens(lens);
        System.arraycopy(head, 0, sendData, position, 2);
        position += 2;

        System.arraycopy(lenData, 0, sendData, position, 4);
        position += 4;

        sendData[position] = cmd;
        position += 1;

        sendData[position] = (byte) keyID;
        position += 1;

        sendData[position] = (byte) algorithm;
        position += 1;

        sendData[position] = (byte) keyDataLen;
        position += 1;

        System.arraycopy(keyDataByte, 0, sendData, position, keyDataLen);
        position += keyDataLen;

        byte[] lrc = TestUtil.getInstance().getLrc(sendData);
        System.arraycopy(lrc, 0, sendData, position, 8);

        return sendData;
    }

    /**
     * pack head respond
     */
    @Override
    public TestBean unPack(byte[] result, int resultDataLen) {
        KeyUse bean = new KeyUse();
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
            rspCode = false;
            bean.setSccess(false);
            TestUtil.getInstance().codeRsp(result[i]);
        }
        i += 1;

        if (rspCode) {
            int keyDataLen = result[i];
            bean.setKeyDataLens((byte) keyDataLen);
            i += 1;

            byte[] keyData = new byte[keyDataLen];
            System.arraycopy(result,i,keyData,0,keyDataLen);
            bean.setKeyData(keyData);
            i += keyDataLen;
        }
        // get lrc
        byte[] lrc = new byte[8];
        System.arraycopy(result, i, lrc, 0, 8);
        bean.setLrc(lrc);
        return bean;
    }

}
