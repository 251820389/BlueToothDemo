package com.nationz.bean;

import java.util.Arrays;

import com.nationz.util.HexStringUtil;

public class IcCardPower extends TestBean {

	private byte flightReq;
	private byte ATRLen;
	private byte[] ATR;
	
	public byte getFlightReq() {
		return flightReq;
	}

	public void setFlightReq(byte flightReq) {
		this.flightReq = flightReq;
	}

	public byte getATRLen() {
		return ATRLen;
	}

	public void setATRLen(byte aTRLen) {
		ATRLen = aTRLen;
	}

	public byte[] getATR() {
		return ATR;
	}

	public void setATR(byte[] aTR) {
		ATR = aTR;
	}

	@Override
	public String toString() {
		if(!sccess){
			return super.toString();
		}
		return "IcCardPower \n"+super.toString()+"\n flightReq=" + flightReq + "\n ATRLen=" + ATRLen
				+ "\n ATR=" + HexStringUtil.byteArrayToHexstring(ATR);
	}

}
