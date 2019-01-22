package com.jjdata.batch.service;

import com.jjdata.batch.config.ExcelConfig;
import com.jjdata.batch.config.PageTagConfig;
import com.jjdata.batch.config.UserInfoConfig;
import com.jjdata.batch.model.TagTypeModel;
import com.jjdata.batch.util.ConfigUtils;
import com.jjdata.batch.util.excel.PoiUtil;
import com.jjdata.batch.util.excel.WriteExcelRedTextDataDelegated;
import com.jjdata.batch.util.FileUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.io.File;
import java.util.List;

/**
 * @author shipeien
 * @version 1.0
 * @Title: TaskRegFormatService
 * @ProjectName interface-plat
 * @Description: 结果处理
 * @email shipeien@jinjingdata.com
 * @date 2019/1/15 17:03
 */
public class TaskRegFormatService {

    /**
     * 根据约定目录检测约定文件生成Excel文件
     * @return
     */
    public static boolean regFormatByFile(){
        boolean isReg=true;
        File[] files= FileUtils.findPathFileNoDir(ExcelConfig.data_source_file_path,".E");
        try {
            for(File file:files){
                //读文件方式默认内存可以承受一次性读完文件
                String exportPath=file.getCanonicalPath().replace(".E","")+".xlsx";
                PoiUtil.exportExcelToLocalPath(file,exportPath, new WriteExcelRedTextDataDelegated() {
                    @Override
                    public void writeExcelRedTXTData(SXSSFSheet eachSheet, File file) throws Exception {
                        List<String> dataAll=FileUtils.readFile(file.getCanonicalPath());
//                        List<TagTypeModel> tagTypeModellist=PageTagConfig.TAG_TYPE_MODEL_MAP.get(TaskConfig.BATCH_TYPE);
                        List<TagTypeModel> tagTypeModellist=PageTagConfig.TAG_TYPE_MODEL_MAP.get("HJS");
                        SXSSFRow eachDataRowTitleBegin = eachSheet.createRow(0);
                        SXSSFRow eachDataRowEnd = eachSheet.createRow(1);
                        String[] titleLine=ExcelConfig.excel_column.split(",");
                        int col_title=titleLine.length;
                        for(TagTypeModel tagTypeModel:tagTypeModellist){
                            eachDataRowTitleBegin.createCell(col_title).setCellValue(tagTypeModel.getTagName());
                            eachDataRowEnd.createCell(col_title).setCellValue(tagTypeModel.getTagChName());
                            col_title++;
                        }
                        for(int i=0;i<titleLine.length;i++){
                            eachDataRowTitleBegin.createCell(i).setCellValue(titleLine[i]);
                            CellRangeAddress region = new CellRangeAddress(0, 1, i, i);
                            eachSheet.addMergedRegion(region);
                        }
                        for(int row=2;row<dataAll.size()+2;row++){
                            String str=dataAll.get(row-2);
                            SXSSFRow eachDataRow = eachSheet.createRow(row);
                            if(null!=str&&!"".equals(str)){
                                String[] dataArr=str.split("\\^A");
                                for(int col=0;col<dataArr.length;col++){
                                    eachDataRow.createCell(col).setCellValue(dataArr[col]+"");
                                }
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            isReg=false;
            e.printStackTrace();
        }
        return  isReg;
    }

    public static void main(String[] args) {
        try {
            ConfigUtils.loadClasspathConfig("get-tag.properties");
            ConfigUtils.loadClasspathConfig("excel.properties");
            //初始化标签信息
            PageTagConfig.initTagInfo();
            //初始化用户信息
            UserInfoConfig.initUserInfo();
            TaskRegFormatService.regFormatByFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
