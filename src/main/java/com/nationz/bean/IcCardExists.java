package com.nationz.bean;

public class IcCardExists extends TestBean {

	private byte icCardStatus;
	
	public byte getIcCardStatus() {
		return icCardStatus;
	}

	public void setIcCardStatus(byte icCardStatus) {
		this.icCardStatus = icCardStatus;
	}

	@Override
	public String toString() {
		if(!sccess){
			return super.toString();
		}
		return "IcCardExists \n"+super.toString()+"\n icCardStatus=" + icCardStatus;
	}
}
