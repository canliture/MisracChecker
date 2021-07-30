package kr.ac.jbnu.ssel.misrac.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MD5Util {

    /**
     * 利用MD5进行加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encode(String str) {
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder base64en = Base64.getEncoder();
            //加密后的字符串
            return base64en.encodeToString(md5.digest(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMD5(String string) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            return toHexString(md5.digest(string.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String toHexString(byte[] md5) {
        StringBuilder sb = new StringBuilder();
        for (byte b : md5) {
            sb.append(Integer.toHexString(b & 0xff));
        }
        return sb.toString();
    }

}
