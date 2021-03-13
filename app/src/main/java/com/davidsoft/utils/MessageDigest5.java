package com.davidsoft.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MessageDigest5 {

    public static String encode(byte[] data, boolean upperCase) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = algorithm.digest(data);
        StringBuilder builder = new StringBuilder(32);
        for (byte b : digest) {
            builder.append(String.format(upperCase ? "%02X" : "%02x", b));
        }
        return builder.toString();
    }
}
