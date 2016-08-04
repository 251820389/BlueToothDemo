package com.nationz.bean;

import java.util.Arrays;

public class KeyInject extends TestBean{

	private byte keyLrcCipherLens;
	private byte[] keyLrcCipherData;
	public byte getKeyLrcCipherLens() {
		return keyLrcCipherLens;
	}
	public void setKeyLrcCipherLens(byte keyLrcCipherLens) {
		this.keyLrcCipherLens = keyLrcCipherLens;
	}
	public byte[] getKeyLrcCipherData() {
		return keyLrcCipherData;
	}
	public void setKeyLrcCipherData(byte[] keyLrcCipherData) {
		this.keyLrcCipherData = keyLrcCipherData;
	}
	@Override
	public String toString() {
		return "KeyInject \n"+super.toString()+"\n keyLrcCipherLens=" + keyLrcCipherLens
				+ "\n keyLrcCipherData=" + Arrays.toString(keyLrcCipherData);
	}
}
