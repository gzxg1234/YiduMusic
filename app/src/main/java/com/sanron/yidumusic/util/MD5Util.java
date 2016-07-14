package com.sanron.yidumusic.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sanron on 16-7-14.
 */
public class MD5Util {

    public static String toMd5(byte[] input, boolean toUpperCase) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(input);
            return toHexString(messageDigest.digest(), "", toUpperCase);
        } catch (NoSuchAlgorithmException paramArrayOfByte) {
            throw new RuntimeException(paramArrayOfByte);
        }
    }

    public static String toHexString(byte[] input, String salt, boolean toUpperCase) {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            String str = Integer.toHexString(input[i] & 0xFF);
            if (toUpperCase) {
                str = str.toUpperCase();
            }
            if (str.length() == 1) {
                localStringBuilder.append("0");
            }
            localStringBuilder.append(str).append(salt);
        }
        return localStringBuilder.toString();
    }
}
