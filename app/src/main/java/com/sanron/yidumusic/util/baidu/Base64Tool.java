package com.sanron.yidumusic.util.baidu;

import java.util.Arrays;

public class Base64Tool {
    private static char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static byte[] bytes = new byte[128];

    static {
        Arrays.fill(bytes, (byte) -1);
        for (int i = 0; i < 64; i++) {
            bytes[chars[i]] = (byte) i;
        }
    }

    public static String encode(byte[] bytes) {
        return encode(bytes, 0, bytes.length);
    }

    public static String encode(byte[] bytes, int start, int length) {

        int num0 = (length * 4 + 2) / 3;
        char[] result = new char[(length + 2) / 3 * 4];
        int max = start + length;
        int i = start;
        int resultIndex = 0;

        while (i < max) {

            int n0 = bytes[i++] & 0xFF;

            int n1 = 0;
            if (i < max) {
                n1 = bytes[i++] & 0xFF;
            }

            int n2 = 0;
            if (i < max) {
                n2 = bytes[i++] & 0xFF;
            }

            int i1 = n0 >>> 2;
            int i2 = ((n0 & 0x3) << 4) | (n1 >>> 4);
            int i3 = ((n1 & 0xF) << 2) | (n2 >>> 6);
            int i4 = n2 & 0x3F;

            result[resultIndex++] = chars[i1];
            result[resultIndex++] = chars[i2];

            char c = resultIndex < num0 ? chars[i3] : '=';
            result[resultIndex++] = c;

            c = resultIndex < num0 ? chars[i4] : '=';
            result[resultIndex++] = c;
        }
        return new String(result);
    }
}
