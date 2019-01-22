package com.jjdata.batch.enumj;

/**
 * @author shipeien
 * @version 1.0
 * @Title: MessageEnum
 * @ProjectName run-batch
 * @Description: 消息枚举
 * @email shipeien@jinjingdata.com
 * @date 2018/12/812:53
 */
public enum MessageEnum {

    /**
     * 预置消息
     */
    //表示本次操作成功
    SUCCESS("0000","成功！",""),
    SYSERR1("SYS0001","系统异常",""),

    //ftp操作系统异常值
    FTPERR1("FTP0001","未指定下载文件名称或者文件类型！",""),
    FTPERR2("FTP0002","文件下载失败！","");
    /**
     * 消息码
     */
    String messageCode;
    /**
     * 消息内容
     */
    String messageInfo;
    /**
     * 自定义消息内容
     */
    String customInfo;

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }

    public String getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(String customInfo) {
        this.customInfo = customInfo;
    }

    MessageEnum(String messageCode, String messageInfo, String customInfo) {
        this.messageCode = messageCode;
        this.messageInfo = messageInfo;
        this.customInfo = customInfo;
    }
}
