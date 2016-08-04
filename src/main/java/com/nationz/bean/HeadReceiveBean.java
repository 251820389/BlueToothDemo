package com.nationz.bean;

import com.nationz.util.HexStringUtil;

public class HeadReceiveBean extends TestBean{

	private byte[] id;
	private byte[] firmware_version;
	private byte[] hardware_version;
	private byte keyState;
	private byte[] sn;
	private byte headLrc;

	public byte[] getId() {
		return id;
	}
	public void setId(byte[] id) {
		this.id = id;
	}
	public byte[] getFirmware_version() {
		return firmware_version;
	}
	public void setFirmware_version(byte[] firmware_version) {
		this.firmware_version = firmware_version;
	}
	public byte[] getHardware_version() {
		return hardware_version;
	}
	public void setHardware_version(byte[] hardware_version) {
		this.hardware_version = hardware_version;
	}
	public byte getKeyState() {
		return keyState;
	}
	public void setKeyState(byte keyState) {
		this.keyState = keyState;
	}
	public byte[] getSn() {
		return sn;
	}
	public void setSn(byte[] sn) {
		this.sn = sn;
	}

	public byte getHeadLrc() {
		return headLrc;
	}
	public void setHeadLrc(byte headLrc) {
		this.headLrc = headLrc;
	}
	@Override
	public String toString() {
		if(!sccess){
			return super.toString();
		}
		return "HeadReceiveBean \n head=" + HexStringUtil.byteArrayToHexstring(head) +
                "\n dataLen=" + dataLen +
                "\n cmd=" + Integer.toHexString(cmd) +
                "\n rspCode=" + rspCode+"\n id=" + HexStringUtil.byteArrayToHexstring(id)
				+ "\n firmware_version=" + new String(firmware_version)
				+ "\n hardware_version=" + new String(hardware_version)
				+ "\n keyState=" + keyState + "\n sn=" + new String(sn)
				+ "\n headLrc=" + Integer.toHexString(headLrc);
	}
	
}
