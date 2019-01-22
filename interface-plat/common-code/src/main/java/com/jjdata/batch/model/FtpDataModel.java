package com.jjdata.batch.model;


/**
 * @author shipeien
 * @version 1.0
 * @Title: FtpDataModel
 * @ProjectName run-batch
 * @Description: ftp下载文件模型
 * @email shipeien@jinjingdata.com
 * @date 2018/12/812:51
 */
public class FtpDataModel  {

    /**
     * host
     */
    private String host;
    /**
     * 端口
     */
    private int port;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 下载/上传文件路径
     */
    private String sftpPath;

    /**
     * 本地存放文件路径
     */
    private String localPath;
    /**
     *下载文件名称（优先级高于文件类型）
     */
    private String fileName;
    /**
     *下载文件类型（优先级低于文件名称），下载填写该参数无效
     */
    private String fileType;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSftpPath() {
        return sftpPath;
    }

    public void setSftpPath(String sftpPath) {
        this.sftpPath = sftpPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public FtpDataModel(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public FtpDataModel(String host, int port, String username, String password, String sftpPath, String localPath, String fileName, String fileType) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.sftpPath = sftpPath;
        this.localPath = localPath;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "FtpDataModel{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sftpPath='" + sftpPath + '\'' +
                ", localPath='" + localPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
