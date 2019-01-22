package cn.jinjing.plat.api.entity;

import java.io.Serializable;

public class ReLabel implements Serializable {

    private String code;//错误码

    private boolean flag; //成功或失败标志

    private String message; //失败原因

    private String data; //返回的数据

    public ReLabel(){}

    public ReLabel(boolean flag)
    {
        this.flag = flag;
    }

    public ReLabel(boolean flag, String message)
    {
        this.flag = flag;
        this.message = message;
    }

    public ReLabel(boolean flag, String message, String data)
    {
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    public ReLabel(boolean flag, String message, String data,String code)
    {
        this.code = code;
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
