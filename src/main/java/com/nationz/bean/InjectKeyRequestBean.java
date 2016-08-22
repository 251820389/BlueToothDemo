package com.nationz.bean;

/**
 * Created by LEO on 2016/8/22.
 */
public class InjectKeyRequestBean {

    private int keyId;
    private int decryption_algorithm;
    private int decodeKeyId;
    private String keyData;
    private int keyLrcAlgorithm;
    private String keyLrcData;

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public int getDecryption_algorithm() {
        return decryption_algorithm;
    }

    public void setDecryption_algorithm(int decryption_algorithm) {
        this.decryption_algorithm = decryption_algorithm;
    }

    public int getDecodeKeyId() {
        return decodeKeyId;
    }

    public void setDecodeKeyId(int decodeKeyId) {
        this.decodeKeyId = decodeKeyId;
    }

    public String getKeyData() {
        return keyData;
    }

    public void setKeyData(String keyData) {
        this.keyData = keyData;
    }

    public int getKeyLrcAlgorithm() {
        return keyLrcAlgorithm;
    }

    public void setKeyLrcAlgorithm(int keyLrcAlgorithm) {
        this.keyLrcAlgorithm = keyLrcAlgorithm;
    }

    public String getKeyLrcData() {
        return keyLrcData;
    }

    public void setKeyLrcData(String keyLrcData) {
        this.keyLrcData = keyLrcData;
    }

    @Override
    public String toString() {
        return "InjectKeyRequestBean{" +
                "\n  keyId=" + keyId +
                "\n  decryption_algorithm=" + decryption_algorithm +
                "\n  decodeKeyId=" + decodeKeyId +
                "\n  keyData='" + keyData + '\'' +
                "\n  keyLrcAlgorithm=" + keyLrcAlgorithm +
                "\n  keyLrcData='" + keyLrcData + '\'' +
                '}';
    }
}
