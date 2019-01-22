package cn.jinjing.plat.unicominner.pojo;

/**
 * @author shipeien
 * @version 1.0
 * @Title: LabelInfo
 * @ProjectName interface-plat
 * @Description: TODO
 * @email shipeien@jinjingdata.com
 * @date 2018/12/26 11:01
 */
public class LabelInfo {
    private String version;
    private Integer id;
    private String labelName;
    private LabelRule labelRule;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public LabelRule getLabelRule() {
        return labelRule;
    }

    public void setLabelRule(LabelRule labelRule) {
        this.labelRule = labelRule;
    }

    @Override
    public String toString() {
        return "LabelInfo{" +
                "version='" + version + '\'' +
                ", id=" + id +
                ", labelName='" + labelName + '\'' +
                ", labelRule=" + labelRule +
                '}';
    }
}
