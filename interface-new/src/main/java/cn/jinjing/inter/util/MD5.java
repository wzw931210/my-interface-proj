package cn.jinjing.inter.util;
 
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 
 /**
  * Salt MD5 加强模块<br>
  * 提供 16 位与 32 位 MD5 加密与其 Salt 加密功能<p>
  * 
  * Salt 加密方法调用：<br>
  *         MD5 md5 = new MD5();<br>
  *         String [] _tmp = md5.salt32("明文"); <br>
         System.out.println(_tmp[0] + "\t" + _tmp[1]);
  * 
  * @author Programer.Monkey<br>
  * Email: Programer.Monkey@gmail.com
  * 
  * @version 11.12.30.1
  * 
  */
 public class MD5 {
 
     /**
      * 随机密钥长度
      */
     private static final int _KEYTLENGTH = 6;
 
     public MD5() {
     }
 
     /**
      * 生成随机密钥
      * 
      * @param length
      *            密钥长度
      */
     private String getRandomKeyt(int length) throws Exception {
         if (length < 1)
             throw new Exception("密钥长度不能小于 1");
         String _keyt = "";
         for (int i = 0; i < length; i++) {
             _keyt += (char) (33 + (int) (Math.random() * (126 - 33 + 1)));
         }
         return _keyt;
     }
 
     /**
      * 32位大写标准 MD5 加密
      * 
      * @param plainText
      *            明文
      * @return 密文<br>
      *         返回 Null 值则出现异常
      */
   static public String cell32(String plainText) {
         try {
             MessageDigest md = MessageDigest.getInstance("MD5");
             md.update(plainText.getBytes());
             byte b[] = md.digest();
             int i;
             StringBuffer buf = new StringBuffer("");
             for (int offset = 0; offset < b.length; offset++) {
                 i = b[offset];
                 if (i < 0)
                     i += 256;
                 if (i < 16)
                     buf.append("0");
                 buf.append(Integer.toHexString(i));
             }
             return buf.toString().toUpperCase();// 32位的加密
 
         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
         }
         return null;
     }
 
     /**
      * 32 位 salt 加密
      * 
      * @param plainText
      *            明文
      * @return 索引 0 是密文，索引 1 是二次密钥
      */
     public String[] salt32(String plainText) throws Exception {
         return salt("cell32", plainText);
     }
 
     /**
      * 16 位标准 MD5 加密
      * 
      * @param plainText
      *            明文
      * @return 密文<br>
      *         返回 Null 值则出现异常
      */
     public String cell16(String plainText) {
         String result = cell32(plainText);
         if (result == null)
             return null;
         return result.toString().substring(8, 24);// 16位的加密
     }
 
     /**
      * 16 位 salt 加密
      * 
      * @param plainText
      *            明文
      * @return 索引 0 是密文，索引 1 是二次密钥
      */
     public String[] salt16(String plainText) throws Exception {
         return salt("cell16", plainText);
     }
 
     /**
      * 根据调用的方法名称执行不同的方法
      * 
      * @param saltFunctionName
      *            加密的方法名称
      */
     private String[] salt(String saltFunctionName, String plainText)
             throws Exception {
         String _keyt = getRandomKeyt(_KEYTLENGTH);
         return new String[] {(String) this.getClass().getMethod(saltFunctionName, Class.forName("java.lang.String")).invoke(this, (cell32(plainText) + _keyt)), _keyt };
     }
        
}