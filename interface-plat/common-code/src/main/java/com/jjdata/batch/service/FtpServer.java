package com.jjdata.batch.service;

import com.jcraft.jsch.ChannelSftp;
import com.jjdata.batch.enumj.MessageEnum;
import com.jjdata.batch.model.FtpDataModel;
import com.jjdata.batch.model.MessageModel;
import com.jjdata.batch.util.ftp.SFTPUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author shipeien
 * @version 1.0
 * @Title: FtpServer
 * @ProjectName interface-plat-not-edit
 * @Description: ftp下载文件
 * @email shipeien@jinjingdata.com
 * @date 2019/1/1715:21
 */
public class FtpServer {
    protected static Log logger = LogFactory.getLog(FtpServer.class);
    public static MessageModel downLoadFile(FtpDataModel ftpDataModel) {
        MessageModel messageModel=new MessageModel();
        SFTPUtils sftp =null;
        // 本地存放地址
        String localPath = ftpDataModel.getLocalPath();
        // Sftp下载路径
        String sftpPath = ftpDataModel.getSftpPath();
        try
        {
            sftp = new SFTPUtils(ftpDataModel.getHost(),ftpDataModel.getPort(), ftpDataModel.getUsername(), ftpDataModel.getPassword());
            sftp.connect();
            String fileName=ftpDataModel.getFileName();
            //没有文件名则批量下载
            if(StringUtils.isBlank(fileName)){
                //判断有没有下载文件类型
                System.out.println(ftpDataModel.getFileType()+"-----------------------"+StringUtils.isBlank(ftpDataModel.getFileType()));
                if(!StringUtils.isBlank(ftpDataModel.getFileType())){
                    // 下载
                    sftp.batchDownLoadFile(sftpPath, localPath, "", ftpDataModel.getFileType(), false);
                }else{
                    logger.error("文件下载失败..."+ftpDataModel);
                    messageModel.initMessageEnum(MessageEnum.FTPERR1);
                }
            }else {
                //单个文件下载
                if(sftp.downloadFile(sftpPath,fileName,localPath,fileName)){
                    messageModel.initMessageEnum(MessageEnum.SUCCESS,false);
                    logger.info("文件下载成功..."+ftpDataModel.toString());
                }else{
                    messageModel.initMessageEnum(MessageEnum.FTPERR2);
                    logger.error("文件下载失败..."+ftpDataModel);
                }
            }
            messageModel.initMessageEnum(MessageEnum.SUCCESS,false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("文件下载异常...",e);
            messageModel.initMessageEnum(MessageEnum.SYSERR1);
        }
        finally
        {
            sftp.disconnect();
        }
        logger.info(messageModel.toString());
        return messageModel;
    }

    public static MessageModel uploadFile(FtpDataModel ftpDataModel) {
        MessageModel messageModel=new MessageModel();
        SFTPUtils sftp= null;
        // 本地存放地址
        String localPath = ftpDataModel.getLocalPath();
        // Sftp下载路径
        String sftpPath = ftpDataModel.getSftpPath();
        try
        {
            sftp = new SFTPUtils(ftpDataModel.getHost(),ftpDataModel.getPort(), ftpDataModel.getUsername(), ftpDataModel.getPassword());
            sftp.connect();
            String fileName=ftpDataModel.getFileName();
            //没有文件名则批量下载
            if(StringUtils.isBlank(fileName)){
                // 批量上传
                sftp.bacthUploadFile(sftpPath, localPath, false);
            }else {
                //单个文件下载
                sftp.uploadFile(sftpPath,fileName,localPath,fileName);

            }
            logger.info("文件上传完成..."+ftpDataModel.toString());
            messageModel.initMessageEnum(MessageEnum.SUCCESS,false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error(ftpDataModel.toString());
            logger.error("文件上传异常...",e);
            messageModel.initMessageEnum(MessageEnum.SYSERR1);
        }
        finally
        {
            sftp.disconnect();
        }

        return messageModel;
    }

    public static List<String> findFileList(FtpDataModel ftpDataModel) {
        SFTPUtils sftp= null;
        // 本地存放地址
        String localPath = ftpDataModel.getLocalPath();
        // Sftp下载路径
        String sftpPath = ftpDataModel.getSftpPath();
        List<String> list =null;
        try
        {
            sftp = new SFTPUtils(ftpDataModel.getHost(),ftpDataModel.getPort(), ftpDataModel.getUsername(), ftpDataModel.getPassword());
            sftp.connect();
            Vector vector=sftp.listFiles(sftpPath);
            if (vector.size() > 0)
            {
                list= new ArrayList();
                Iterator it = vector.iterator();
                String fileType=ftpDataModel.getFileType();
                while (it.hasNext())
                {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) it.next();
                    String filename = entry.getFilename();
                    if(!StringUtils.isBlank(fileType)){
                        if (filename.endsWith(fileType))
                        {
                            list.add(filename);
                        }
                    }else{
                        list.add(filename);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("文件查询异常...",e);
        }
        finally
        {
            sftp.disconnect();
        }

        return list;
    }

}
