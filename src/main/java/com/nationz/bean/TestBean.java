package com.nationz.bean;

import com.nationz.util.HexStringUtil;

public class TestBean {
    protected byte[] head;
    protected int dataLen;
    protected byte cmd;
    protected byte rspCode;
    protected byte[] lrc;
    protected boolean sccess = true;

    public byte[] getHead() {
        return head;
    }

    public void setHead(byte[] head) {
        this.head = head;
    }

    public int getDataLen() {
        return dataLen;
    }

    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getRspCode() {
        return rspCode;
    }

    public void setRspCode(byte rspCode) {
        this.rspCode = rspCode;
    }

    public byte[] getLrc() {
        return lrc;
    }

    public void setLrc(byte[] lrc) {
        this.lrc = lrc;
    }

    public boolean isSccess() {
		return sccess;
	}

	public void setSccess(boolean sccess) {
		this.sccess = sccess;
	}

	@Override
    public String toString() {
        return "\n head=" + HexStringUtil.byteArrayToHexstring(head) +
                "\n dataLen=" + dataLen +
                "\n cmd=" + Integer.toHexString(cmd) +
                "\n rspCode=" + rspCode +
                "\n lrc=" + HexStringUtil.byteArrayToHexstring(lrc);
    }
    
}
