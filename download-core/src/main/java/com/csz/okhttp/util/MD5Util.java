package com.csz.okhttp.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author caishuzhan
 */
public class MD5Util {

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return toHexString(digest.digest());
    }

    public static String generateCode(String code){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(code.getBytes());
            byte[] digest = messageDigest.digest();
            String hex = toHexString(digest);
            return hex;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String toHexString(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            stringBuilder.append(hex.length() == 1 ? "0" + hex : hex);
        }
        return stringBuilder.toString();
    }
}
