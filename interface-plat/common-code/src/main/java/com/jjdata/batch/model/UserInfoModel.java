package com.jjdata.batch.model;

/**
 * @author shipeien
 * @version 1.0
 * @Title: UserInfoModel
 * @ProjectName run-batch
 * @Description: 用户信息类
 * @email shipeien@jinjingdata.com
 * @date 2018/12/11 19:46
 */
public class UserInfoModel {
    private String usercode;
    private String password;

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
