package com.jjdata.batch.util.excel;

import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.io.File;

/**
 * @author shipeien
 * @version 1.0
 * @Title: WriteExcelRedTextDataDelegated
 * @ProjectName interface-plat
 * @Description: TODO
 * @email shipeien@jinjingdata.com
 * @date 2019/1/1518:39
 */
public interface WriteExcelRedTextDataDelegated {
    /**
     * EXCEL写数据委托类  针对不同的情况自行实现
     *
     * @param eachSheet     指定SHEET
     * @throws Exception
     */
    public void writeExcelRedTXTData(SXSSFSheet eachSheet,File file) throws Exception;
}
