package com.sanron.yidumusic.util.baidu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**************************************
 * FileName : com.baidu.android.common.util
 * Author : Administrator
 * Time : 2016/7/15 10:52
 * Description :
 **************************************/
public class MD5Util {
    public static String toHexString(byte[] input, String s, boolean toUpperCase) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            String str = Integer.toHexString(input[i] & 0xFF);
            if (toUpperCase)
                str = str.toUpperCase();
            if (str.length() == 1)
                sb.append("0");
            sb.append(str).append(s);
        }
        return sb.toString();
    }

    public static String toMd5(byte[] input, boolean toUpperCase) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(input);
            return toHexString(messageDigest.digest(), "", toUpperCase);
        } catch (NoSuchAlgorithmException e) {
        }
        throw new RuntimeException(input.toString());
    }
}
