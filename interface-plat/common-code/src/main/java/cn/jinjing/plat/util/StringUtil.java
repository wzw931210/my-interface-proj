package cn.jinjing.plat.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class StringUtil {
    /**
     * 日期格式
     */
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static boolean isEmpty(String str){
        if(str !=null && !"".equals(str)){
            return false;
        }
        else{
            return true;
        }
    }

    public static String getString(String str){
        if(str == null){
            return "";
        }
        else{
            return str;
        }
    }

    public static String getStrFromDate(Date date, String pattern){
        DateFormat df = new SimpleDateFormat(pattern);
        return  df.format(date);
    }

    public static String getMsecStrFromDate(Date date){
        String pattern = "yyyyMMddHHmmssSSS";
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

    public static String getLastMonEN() {
        DateFormat df = new SimpleDateFormat("MMM_yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);//上个月
        return df.format(calendar.getTime());
    }

    /**
     * 获取上一个月
     *
     * @return
     */
    public static String getLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
        String lastMonth = dft.format(cal.getTime());
        return lastMonth;
    }

    /**
     *
     * 描述:获取下一个月.
     *
     * @return
     */
    public static String getPreMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
        String preMonth = dft.format(cal.getTime());
        return preMonth;
    }


    /**
     * 获取流水号
     * 格式建议  yyyyMMddHHmmss+10位随机字符串
     * @return
     */
    public static String getTranNo() {
        return DATE_FMT.format(new Date()) + UUID.randomUUID().toString().substring(0, 10);
    }


    public static void main(String[] args) {
        String month = StringUtil.getLastMonEN();
        System.out.println(month);
    }

}
