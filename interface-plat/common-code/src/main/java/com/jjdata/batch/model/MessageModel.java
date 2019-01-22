package com.jjdata.batch.model;


import com.jjdata.batch.enumj.MessageEnum;

/**
 * @author shipeien
 * @version 1.0
 * @Title: MessageModel
 * @ProjectName run-batch
 * @Description: 返回消息
 * @email shipeien@jinjingdata.com
 * @date 2018/12/812:51
 */
public class MessageModel {

    /**
     * 消息码
     */
    private String messageCode;
    /**
     * 消息内容
     */
    private String messageInfo;
    /**
     * 返回数据
     */
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     *
     * @param messageEnum 信息
     * @param isOverride 是否覆盖原来消息
     *
     */
    public void initMessageEnum(MessageEnum messageEnum, boolean isOverride ){
        if(null==this.getMessageCode()||"".equals(this.getMessageCode())||isOverride){
            this.setMessageCode(messageEnum.getMessageCode());
            this.setMessageInfo(messageEnum.getMessageInfo());
        }
    }
    /**
     * @param messageEnum 信息
     */
    public void initMessageEnum(MessageEnum messageEnum){
            this.setMessageCode(messageEnum.getMessageCode());
            this.setMessageInfo(messageEnum.getMessageInfo());
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "messageCode='" + messageCode + '\'' +
                ", messageInfo='" + messageInfo + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
