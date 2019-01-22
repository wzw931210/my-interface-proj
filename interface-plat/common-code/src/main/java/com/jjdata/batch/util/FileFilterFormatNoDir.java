package com.jjdata.batch.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author shipeien
 * @version 1.0
 * @Title: ExcelFile
 * @ProjectName interface-plat
 * @Description: 文件过滤类，根据文件类型，且不是目录过滤文件
 * @email shipeien@jinjingdata.com
 * @date 2019/1/1517:39
 */
public class FileFilterFormatNoDir implements FileFilter {
    private String fileFormat;

    public FileFilterFormatNoDir(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public boolean accept(File file) {
        String filename = file.getName();
        return filename.endsWith(fileFormat)&&!file.isDirectory();
    }
}
