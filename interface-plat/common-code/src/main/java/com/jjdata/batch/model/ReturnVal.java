package com.jjdata.batch.model;

/**
 * @author shipeien
 * @version 1.0
 * @Title: RetunVal
 * @ProjectName run-batch
 * @Description: 返回值类
 * @email shipeien@jinjingdata.com
 * @date 2018/12/2916:01
 */
public class ReturnVal {
    private boolean reflag;
    private String code;
    private String reg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public boolean isReflag() {
        return reflag;
    }

    public void setReflag(boolean reflag) {
        this.reflag = reflag;
    }
}
