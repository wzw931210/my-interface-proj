package cn.jinjing.inter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

public class AESUtil {

	//算法名称 
    public static final String KEY_ALGORITHM = "AES";
    //算法名称/加密模式/填充方式 
    //AES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     *   
     * 生成密钥key对象
     * AES加密和解密过程中，密钥长度为16位
     * @param  keyStr 密钥字符串
     * @return 密钥对象 
     * @throws InvalidKeyException   
     * @throws NoSuchAlgorithmException   
     * @throws
     * @throws Exception 
     */
    private static SecretKey keyGenerator(String keyStr) throws Exception {
        byte input[] = HexString2Bytes(keyStr);
        
        KeyGenerator keyGenerator=KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );  
        secureRandom.setSeed(input);
        
        keyGenerator.init(128, secureRandom);
        SecretKey securekey = keyGenerator.generateKey(); 
        return securekey;
    }

    private static int parse(char c) {
        if (c >= 'a') return (c - 'a' + 10) & 0x0f;
        if (c >= 'A') return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    // 从十六进制字符串到字节数组转换 
    public static byte[] HexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    /** 
     * 加密数据
     * @param data 待加密数据
     * @param key 密钥
     * @return 加密后的数据 
     */
    public static String encrypt(String data, String key) {
        try {
			Key deskey = keyGenerator(key);
			// 实例化Cipher对象，它用于完成实际的加密操作
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			// 初始化Cipher对象，设置为加密模式
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			byte[] results = cipher.doFinal(data.getBytes());
			// 执行加密操作。加密后的结果通常都会用Base64编码进行传输 
			return Base64.encodeBase64String(results);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} 
    }

    /** 
     * 解密数据 
     * @param data 待解密数据 
     * @param key 密钥 
     * @return 解密后的数据 
     */
    public static String decrypt(String data, String key) throws Exception {
        Key deskey = keyGenerator(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        //初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        // 执行解密操作
        return new String(cipher.doFinal(Base64.decodeBase64(data)));
    }

    
    /**
     * 测试
     * @param args mz+iWut6l9k=
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\wzw\\Desktop\\mdn\\密文.txt"));
        StringBuilder reqData = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            reqData.append(line);
        }
        String decryptData = decrypt(reqData.toString(), "00DmcDjftOJzMl8S");
        System.out.println("解密后: " + decryptData);
    }
	
}
