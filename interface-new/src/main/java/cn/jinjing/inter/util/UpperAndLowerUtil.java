package cn.jinjing.inter.util;

public class UpperAndLowerUtil {
	
	/**
	 * 把首字母转小写
	 * @param str
	 * @return
	 */
	public static String lowerCase(String str) {  
	    char[] ch = str.toCharArray();  
	    if (ch[0] >= 'A' && ch[0] <= 'Z') {  
	        ch[0] = (char) (ch[0] + 32);  
	    }  
	    return new String(ch);  
	}

}
