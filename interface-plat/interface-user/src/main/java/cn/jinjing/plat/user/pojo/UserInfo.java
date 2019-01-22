package cn.jinjing.plat.user.pojo;

public class UserInfo extends BaseUserInfo {

    private String accessKey;

    private double charge;

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

}