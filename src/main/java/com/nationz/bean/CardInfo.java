package com.nationz.bean;

import java.util.Arrays;

import com.nationz.util.HexStringUtil;

public class CardInfo extends TestBean {

	private byte cardType;
	private byte trackLen1;
	private byte[] trackData1;
	private byte trackLen2;
	private byte[] trackData2;
	private byte trackLen3;
	private byte[] trackData3;
	public byte getCardType() {
		return cardType;
	}
	public void setCardType(byte cardType) {
		this.cardType = cardType;
	}
	public byte getTrackLen1() {
		return trackLen1;
	}
	public void setTrackLen1(byte trackLen1) {
		this.trackLen1 = trackLen1;
	}
	public byte[] getTrackData1() {
		return trackData1;
	}
	public void setTrackData1(byte[] trackData1) {
		this.trackData1 = trackData1;
	}
	public byte getTrackLen2() {
		return trackLen2;
	}
	public void setTrackLen2(byte trackLen2) {
		this.trackLen2 = trackLen2;
	}
	public byte[] getTrackData2() {
		return trackData2;
	}
	public void setTrackData2(byte[] trackData2) {
		this.trackData2 = trackData2;
	}
	public byte getTrackLen3() {
		return trackLen3;
	}
	public void setTrackLen3(byte trackLen3) {
		this.trackLen3 = trackLen3;
	}
	public byte[] getTrackData3() {
		return trackData3;
	}
	public void setTrackData3(byte[] trackData3) {
		this.trackData3 = trackData3;
	}
	@Override
	public String toString() {
		if(!sccess){
			return super.toString();
		}
		return "CardInfo [cardType=" + cardType + ", trackLen1=" + trackLen1
				+ ", trackData1=" + HexStringUtil.byteArrayToHexstring(trackData1)
				+ ", trackLen2=" + trackLen2 + ", trackData2="
				+ HexStringUtil.byteArrayToHexstring(trackData2) + ", trackLen3=" + trackLen3
				+ ", trackData3=" + HexStringUtil.byteArrayToHexstring(trackData3) + ", head="
				+ HexStringUtil.byteArrayToHexstring(head) + ", dataLen="
				+ dataLen + ", cmd=" + cmd + ", rspCode="
				+ rspCode + ", lrc=" + HexStringUtil.byteArrayToHexstring(lrc) + "]";
	}
	
}
