package com.nationz.util;

public class HexStringUtil {

    public static final char ERR_INVALID_ARGUMENT = '.';

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.length() == 0) {
            return null;
        }

        hexString = hexString.replaceAll(" ", ""); //remove all blankspace
        hexString = hexString.toUpperCase();
        if (hexString.length() % 2 != 0) {
            hexString = "0"+hexString;
        }
        char[] hexChars = hexString.toCharArray();
        int bytesLen = (hexString.length() + 1) / 2;
        byte[] desBytes = new byte[bytesLen];
        int pos = 0;

        for (int i = 0; i < desBytes.length; i++) {
            pos = i * 2;
            desBytes[i] = (byte) ((charToByte(hexChars[pos]) << 4) | charToByte(hexChars[pos + 1]));
        }

        return desBytes;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String byteArrayToHexstring(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        if (bytes.length <= 0 || bytes == null) {
            return null;
        }

        String hv;
        int v = 0;

        for (int i = 0; i < bytes.length; i++) {
            v = bytes[i] & 0xFF;
            hv = Integer.toHexString(v);

            if (hv.length() < 2) {
                hexString.append(0);
            }

            hexString.append(hv);
        }

        return hexString.toString().toUpperCase();
    }

    public static char[] byteToHexChar(byte b) {
        char[] ch = new char[2];
        if (b > 0xFF) {
            return null;
        }

        //		System.out.println("byteToHexChar b=" + ((b & 0xF0)>>4)  + ", " + (b & 0x0F));
        ch[0] = "0123456789ABCDEF".charAt((b & 0xF0) >> 4);
        ch[1] = "0123456789ABCDEF".charAt((b & 0x0F));
        return ch;
    }

    public static String byteArrayToHexstring(byte[] bytes, int start, int end) {
        StringBuilder hexString = new StringBuilder();

        if (bytes.length <= 0 || bytes == null) {
            return null;
        }

        String hv;
        int v = 0;

        for (int i = start; i < end; i++) {
            v = bytes[i] & 0xFF;
            hv = Integer.toHexString(v);

            if (hv.length() < 2) {
                hexString.append(0);
            }

            hexString.append(hv);
        }

        return hexString.toString().toUpperCase();
    }

    public static byte[] getBytes(double data)

    {
        return longToBytes(Double.doubleToLongBits(data));
    }

    public static byte[] longToBytes(long l) {
        byte[] b = new byte[8];
        b[0] = (byte) (l >>> 56);
        b[1] = (byte) (l >>> 48);
        b[2] = (byte) (l >>> 40);
        b[3] = (byte) (l >>> 32);
        b[4] = (byte) (l >>> 24);
        b[5] = (byte) (l >>> 16);
        b[6] = (byte) (l >>> 8);
        b[7] = (byte) (l);
        return b;
    }

    //byte 与 int 的相互转换
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }
}
