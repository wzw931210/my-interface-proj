package cn.jinjing.inter.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StringUtil {
	
	/**
	 * 日期格式
	 */
	private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMddHHss");
	
	
	public static boolean isEmpty(String str) {
		if(str != null && !"".equals(str)) {
			return false;
		}else {
			return true;
		}
	}
	
	public static String getStrFromDate(Date date, String pattern){
        DateFormat df = new SimpleDateFormat(pattern);
        return  df.format(date);
    }
	
	public static String getMsecStrFromDate(Date date){
        String pattern = "yyyyMMddhhmmssSSS";
        return getStrFromDate(date, pattern);
    }
	
	public static String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static String lowerCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'A' && ch[0] <= 'Z') {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }

    /**
     * 获取流水号
     * 格式建议  yyyyMMddHHmmss+10位随机字符串
     * @return
     */
    public static String getTranNo() {
        return DATE_FMT.format(new Date()) + UUID.randomUUID().toString().substring(0, 10);
    }
}
