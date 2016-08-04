package com.nationz.util;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.util.Log;

public class DESUtils {
	private static final char[] hex = "0123456789abcdef".toCharArray();

	public static byte[] DESEncrypt(byte[] data, byte[] bykey) {
		byte[] encryptedData = new byte[data.length];
		byte[] result = null;
		SecureRandom sr = new SecureRandom();
		try {
			DESKeySpec dks = new DESKeySpec(bykey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
			result = cipher.doFinal(data);
			System.arraycopy(result, 0, encryptedData, 0, data.length);
		} catch (Exception e) {
			System.out.println("Failed to des" + e.getMessage());
		}
		return encryptedData;
		// return result;
	}

	public static byte[] DESDecrypt(byte[] data, byte[] bykey) {
		byte[] newData = new byte[16];
		System.arraycopy(data, 0, newData, 0, 8);
		byte[] result = null;
		// String desJdk=SystemConstants.getConfPara(Constants.DES_JDK);
		// SecureRandom sr = new SecureRandom();
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			DESKeySpec dks = new DESKeySpec(bykey);
			IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
			// SecretKey securekey = keyFactory.generateSecret(dks);
			Key key = keyFactory.generateSecret(dks);

			Cipher encryptCipher = Cipher.getInstance("DES");
			// encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			encryptCipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			byte[] pseudoEncryptedPKCS5 = encryptCipher.doFinal(new byte[0]);

			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] part1 = cipher.update(newData);

			// and now the pseudo PKCS5
			byte[] part2 = cipher.doFinal(pseudoEncryptedPKCS5);

			// Now create the final decrypted byte array by copying.
			result = new byte[part1.length + part2.length];
			int j = 0;
			for (int i = 0; i < part1.length; i++)
				result[j++] = part1[i];
			for (int i = 0; j < result.length; i++)
				result[j++] = part2[i];
			// result = cipher.doFinal(data);
			System.arraycopy(result, 0, data, 0, 8);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to des:  " + e.getMessage());
		}
		return data;
	}

	/**
	 * 3des解密
	 * 
	 * @param data
	 * @param bykey
	 * @return
	 */
	public static byte[] TripDESDecrypt(String data, String bykey) {
		return TripDESDecrypt(strToBytes(data), strToBytes(bykey));
	}

	/**
	 * 3des解密
	 * 
	 * @param data
	 * @param bykey
	 * @return
	 */
	public static byte[] TripDESDecrypt(byte[] data, byte[] bykey) {
		byte[] result = null;
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		System.arraycopy(bykey, 0, key1, 0, 8);
		System.arraycopy(bykey, 8, key2, 0, 8);
		result = DESDecrypt(data, key1);
		result = DESEncrypt(result, key2);
		result = DESDecrypt(result, key1);
		return result;
	}

	/**
	 * 3des加密
	 * 
	 * @param data
	 * @param bykey
	 * @return
	 */
	public static byte[] TripDesEncrypt(String data, String bykey) {
		return TripDesEncrypt(strToBytes(data), strToBytes(bykey));
	}

	/**
	 * 3des加密
	 * 
	 * @param data
	 * @param bykey
	 * @return
	 */
	public static byte[] TripDesEncrypt(byte[] data, byte[] bykey) {
		byte[] result = null;
		byte[] key1 = new byte[8];
		byte[] key2 = new byte[8];
		System.arraycopy(bykey, 0, key1, 0, 8);
		System.arraycopy(bykey, 8, key2, 0, 8);
		result = DESEncrypt(data, key1);
		Log.i("xxx", "加密1："+Arrays.toString(result));
		result = DESDecrypt(result, key2);
		Log.i("xxx", "解密："+Arrays.toString(result));
		result = DESEncrypt(result, key1);
		Log.i("xxx", "加密2："+Arrays.toString(result));
		return result;
	}

	/**
	 * des加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] desEncrypt(String data, String key) {
		byte[] result = null;
		byte[] bKey = strToBytes(key);
		byte[] bData = strToBytes(data);
		result = DESEncrypt(bData, bKey);

		return result;
	}

	/**
	 * des解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] desDecrype(String data, String key) {
		byte[] result = null;
		byte[] bKey = strToBytes(key);
		byte[] bData = strToBytes(data);
		result = DESDecrypt(bData, bKey);
		return result;
	}

	/**
	 * string to byte
	 * 
	 * @param src
	 *            "81214f5888"
	 * @return [-127, 33, 79, 88, -120]
	 */
	public static byte[] strToBytes(String src) {
		byte[] res = new byte[1];
		res[0] = (byte) 0xff;
		if (src == null || src.length() <= 0) {
			return res;
		}
		char[] c = src.toCharArray();
		byte[] b = new byte[c.length / 2];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) ((Character.digit((int) c[2 * i], 16) << 4) | Character
					.digit((int) c[2 * i + 1], 16));
		}
		return b;
	}

	/**
	 * byte to str
	 * 
	 * @param b
	 *            [1, 33, 79, 88, -120]
	 * @return "01214f5888"
	 */
	public static String bytesToStr(byte[] b) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			s.append(hex[(b[i] >>> 4) & 0xf]);
			s.append(hex[b[i] & 0xf]);
		}
		return s.toString();
	}

	/**
	 * 
	 * @param key
	 * @param checkValue
	 * @return
	 */
	public static boolean checkValue(String key, String checkValue, int type) {
		if (checkValue == null || key == null) {
			return false;
		}
		if (checkValue.equals("") || key.equals("")) {
			return false;
		}
		String solid = "00000000000000000000000000000000";

		byte[] byteSolid = strToBytes(solid);
		byte[] byteKey = strToBytes(key);
		byte[] res = null;
		String strRes = "";
		if (type == 0) {
			res = TripDesEncrypt(byteSolid, byteKey);
			strRes = bytesToStr(res).substring(0, 8);
		} else {
			res = DESEncrypt(byteSolid, byteKey);
			// strRes = bytesToStr(res).substring(0, 16);
			strRes = bytesToStr(res).substring(0, 8);
		}
		if (strRes.equalsIgnoreCase(checkValue)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String lastAddO(String str) {
		if (str.length() < 32) {
			int len = 32 - str.length();
			for (int i = 0; i < len; i++) {
				str += "0";
			}
		}
		return str;

	}
}
