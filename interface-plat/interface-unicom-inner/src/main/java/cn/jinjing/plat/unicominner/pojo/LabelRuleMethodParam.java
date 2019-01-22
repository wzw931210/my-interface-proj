package cn.jinjing.plat.unicominner.pojo;

/**
 * @author shipeien
 * @version 1.0
 * @Title: LabelRuleMethodParam
 * @ProjectName interface-plat
 * @Description: 标签方法对象
 * @email shipeien@jinjingdata.com
 * @date 2018/12/2611:05
 */
public class LabelRuleMethodParam {
    /**
     * 方法顺序
     */
    private Integer orderDesc;
    /**
     * 方法参数
     */
    private Object pam;
    /**
     * 方法类型
     */
    private String type;

    public Integer getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(Integer orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getPam() {
        return pam;
    }

    public void setPam(Object pam) {
        this.pam = pam;
    }

    @Override
    public String toString() {
        return "LabelRuleMethodParam{" +
                "orderDesc=" + orderDesc +
                ", pam=" + pam +
                ", type='" + type + '\'' +
                '}';
    }
}
