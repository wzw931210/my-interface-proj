package cn.jinjing.plat.api.entity;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class ReObject implements Serializable{

    private String code;//错误码

    private boolean flag; //成功或失败标志

    private String message; //失败原因

    private JSONObject data; //返回的数据

    public ReObject(){}

    public ReObject(boolean flag)
    {
        this.flag = flag;
    }

    public ReObject(boolean flag, String message)
    {
        this.flag = flag;
        this.message = message;
    }

    public ReObject(boolean flag, JSONObject data)
    {
        this.flag = flag;
        this.data = data;
    }

    public ReObject(boolean flag, String message, JSONObject data)
    {
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    public ReObject(boolean flag, String message, JSONObject dat,String code)
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

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "flag:" + flag + ", message:" + message + ", data:" + data;
    }
}
