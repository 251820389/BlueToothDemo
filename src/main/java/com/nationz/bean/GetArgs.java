package com.nationz.bean;

import java.util.Arrays;

import com.nationz.util.HexStringUtil;

public class GetArgs extends TestBean {

	private byte argLen;
	private byte argId;
	private byte[] argData;

	public byte getArgLen() {
		return argLen;
	}

	public void setArgLen(byte argLen) {
		this.argLen = argLen;
	}

	public byte getArgId() {
		return argId;
	}

	public void setArgId(byte argId) {
		this.argId = argId;
	}

	public byte[] getArgData() {
		return argData;
	}

	public void setArgData(byte[] argData) {
		this.argData = argData;
	}

	@Override
	public String toString() {
		if(!sccess){
			return super.toString();
		}
		return "GetArgs \n" + super.toString() + "\n argLen=" + argLen
				+ "\n argId=" + argId + "\n argData=" + new String(argData);
	}

}
