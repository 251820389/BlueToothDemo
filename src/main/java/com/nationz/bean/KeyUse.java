package com.nationz.bean;

import com.nationz.util.HexStringUtil;

import java.util.Arrays;

public class KeyUse extends TestBean{

	private byte keyDataLens;
	private byte[] keyData;

	public byte getKeyDataLens() {
		return keyDataLens;
	}

	public void setKeyDataLens(byte keyDataLens) {
		this.keyDataLens = keyDataLens;
	}

	public byte[] getKeyData() {
		return keyData;
	}

	public void setKeyData(byte[] keyData) {
		this.keyData = keyData;
	}

	@Override
	public String toString() {
		return "KeyUse{" +
				"\n  keyDataLens=" + keyDataLens +
				"\n  keyData=" + Arrays.toString(keyData) +
				"} " + super.toString();
	}
}
