package cn.jinjing.plat.unicominner.pojo;

import java.util.List;

/**
 * @author shipeien
 * @version 1.0
 * @Title: LabelRule
 * @ProjectName interface-plat
 * @Description: 标签规则对象
 * @email shipeien@jinjingdata.com
 * @date 2018/12/2611:03
 */
public class LabelRule {
    /**
     * 规则处理方法名称
     */
    private String ruleMethod;
    /**
     * 规则参数
     */
    private List<LabelRuleMethodParam> ruleMethodParams;

    public String getRuleMethod() {
        return ruleMethod;
    }

    public void setRuleMethod(String ruleMethod) {
        this.ruleMethod = ruleMethod;
    }

    public List<LabelRuleMethodParam> getRuleMethodParams() {
        return ruleMethodParams;
    }

    public void setRuleMethodParams(List<LabelRuleMethodParam> ruleMethodParams) {
        this.ruleMethodParams = ruleMethodParams;
    }

    @Override
    public String toString() {
        return "LabelRule{" +
                "ruleMethod='" + ruleMethod + '\'' +
                ", ruleMethodParams=" + ruleMethodParams +
                '}';
    }
}
