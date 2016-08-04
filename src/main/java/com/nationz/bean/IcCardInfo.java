package com.nationz.bean;

import com.nationz.util.HexStringUtil;


public class IcCardInfo extends TestBean {

	private int cmdInfoLens;
	private byte[] rspInfo;
	
	
	
	public int getCmdInfoLens() {
		return cmdInfoLens;
	}

	public void setCmdInfoLens(int cmdInfoLens) {
		this.cmdInfoLens = cmdInfoLens;
	}

	public byte[] getRspInfo() {
		return rspInfo;
	}

	public void setRspInfo(byte[] rspInfo) {
		this.rspInfo = rspInfo;
	}

	@Override
	public String toString() {
		if(!sccess){
			return super.toString();
		}
		return "IcCardInfo \n"+super.toString()+"\n cmdInfoLens=" + cmdInfoLens
				+ "\n  rspInfo=" + HexStringUtil.byteArrayToHexstring(rspInfo);
	}
}
