package com.jjdata.batch.config;

import com.jjdata.batch.util.ConfigUtils;

/**
 * @author shipeien
 * @version 1.0
 * @Title: ExcelConfig
 * @ProjectName interface-plat
 * @Description: Excel配置参数
 * @email shipeien@jinjingdata.com
 * @date 2019/1/1616:30
 */
public class ExcelConfig {
    public static String data_source_file_path = ConfigUtils.getString("data_source_file_path");
    public static String excel_column = ConfigUtils.getString("excel_column");
}
