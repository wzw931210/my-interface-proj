package com.jjdata.batch.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shipeien
 * @version 1.0
 * @Title: DateUtil
 * @ProjectName interface-plat
 * @Description: TODO
 * @email shipeien@jinjingdata.com
 * @date 2019/1/1515:41
 */
public class DateUtil {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    /**
     * 将日期转换为字符串
     *
     * @param date   DATE日期
     * @param format 转换格式
     * @return 字符串日期
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

}
