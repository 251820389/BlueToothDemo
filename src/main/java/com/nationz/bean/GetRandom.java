package com.nationz.bean;

import com.nationz.util.HexStringUtil;

public class GetRandom extends TestBean {
    private byte randomLens;
    private byte[] randomValue;

    public byte getRandomLens() {
        return randomLens;
    }

    public void setRandomLens(byte randomLens) {
        this.randomLens = randomLens;
    }

    public byte[] getRandomValue() {
        return randomValue;
    }

    public void setRandomValue(byte randomValue[]) {
        this.randomValue = randomValue;
    }

	@Override
	public String toString() {
		if(!sccess){
			return super.toString();
		}
		return "GetRandom \n"+ super.toString()+"\n randomLens=" + randomLens + "\n randomValue="
				+ HexStringUtil.byteArrayToHexstring(randomValue);
	}
}
