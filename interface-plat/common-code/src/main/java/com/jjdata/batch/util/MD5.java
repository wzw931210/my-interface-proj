package com.jjdata.batch.util;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author shipeien
 * @version 1.0
 * @Title: MD5
 * @ProjectName run-batch
 * @Description: MD5加密类
 * @email shipeien@jinjingdata.com
 * @date 2018/12/2512:43
 */
public class MD5 {
    public static String encryption(String data) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(data.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String md5=new BigInteger(1, md.digest()).toString(16);
            //BigInteger会把0省略掉，需补全至32位
            return fillMD5(md5);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密错误:"+e.getMessage(),e);
        }

    }
    /**利用MD5进行加密
     　　* @param str  待加密的字符串
     　　* @return  加密后的字符串
     　　* @throws NoSuchAlgorithmException  没有这种产生消息摘要的算法
     　　 * @throws UnsupportedEncodingException
     　　*/
    public String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//确定计算方法
        MessageDigest md5= MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
//加密后的字符串
        String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    public static String fillMD5(String md5){
        return md5.length()==32?md5:fillMD5("0"+md5);
    }
    public static void main(String[] args) {
//        MD5 md5 = new MD5();
        String ss = MD5.encryption("聂斌伢");
        System.err.print(ss);
    }
}
