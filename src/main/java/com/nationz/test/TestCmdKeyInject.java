package com.nationz.test;

import android.util.Log;

import com.nationz.bean.KeyInject;
import com.nationz.bean.TestBean;
import com.nationz.util.HexStringUtil;

import java.util.ArrayList;


public class TestCmdKeyInject extends TestBase {

    private int keyId;
    private int decryption_algorithm;
    private int decodeKeyId;
    private String keyData;
    private int keyLrcAlgorithm;
    private String keyLrcData;

    @Override
    public void init(ArrayList arrList) {
        this.keyId = (int) arrList.get(0);
        this.decryption_algorithm = (int) arrList.get(1);
        if (decryption_algorithm != 12) {
            this.decodeKeyId = (int) arrList.get(2);
            this.keyData = arrList.get(3).toString();
            this.keyLrcAlgorithm = (int) arrList.get(4);
            this.keyLrcData = arrList.get(5).toString();
        }
        cmd = 0x0E;
    }

    /**
     * 参数设置
     *
     * @return
     */
    public byte[] pack() {
        // 总长度计算
        byte[] keyDataByte = HexStringUtil.hexStringToBytes(keyData);
        byte[] keyLrcDataByte = HexStringUtil.hexStringToBytes(keyLrcData);

        int keyDataLen = keyDataByte.length;
        int keyLrcDataLen = keyLrcDataByte.length;
        int lens = 2 + 4 + 1 + 1 + 1 + 1 + 1 + keyDataLen + 1 + 1 + keyLrcDataLen + 8;
        sendData = new byte[lens];
        byte[] lenData = TestUtil.getInstance().getLens(lens);
        System.arraycopy(head, 0, sendData, position, 2);
        position += 2;

        System.arraycopy(lenData, 0, sendData, position, 4);
        position += 4;

        sendData[position] = cmd;
        position += 1;

        sendData[position] = (byte) keyId;
        position += 1;

        sendData[position] = (byte) decryption_algorithm;
        position += 1;
        if (decryption_algorithm != 12) {
            sendData[position] = (byte) decodeKeyId;
            position += 1;

            sendData[position] = (byte) keyDataLen;
            position += 1;

            System.arraycopy(keyDataByte, 0, sendData, position, keyDataLen);
            position += keyDataLen;

            sendData[position] = (byte) keyLrcDataLen;
            position += 1;

            sendData[position] = (byte) keyLrcAlgorithm;
            position += 1;

            System.arraycopy(keyLrcDataByte, 0, sendData, position, keyLrcDataLen);
            position += keyLrcDataLen;
        }

        byte[] lrc = TestUtil.getInstance().getLrc(sendData);
        System.arraycopy(lrc, 0, sendData, position, 8);

        return sendData;
    }

    /**
     * pack head respond
     */
    @Override
    public TestBean unPack(byte[] result, int resultDataLen) {
        KeyInject bean = new KeyInject();
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
            int keyLrcDataLens = result[i];
            bean.setKeyLrcCipherLens((byte) keyLrcDataLens);
            byte[] keyLrcData = new byte[i];
            i += 1;

            System.arraycopy(result, i, keyLrcData, 0, keyLrcDataLens);
            bean.setKeyLrcCipherData(keyLrcData);
            i += keyLrcDataLens;
        }

        // get lrc
        byte[] lrc = new byte[8];
        System.arraycopy(result, i, lrc, 0, 8);
        bean.setLrc(lrc);
        return bean;
    }

}
