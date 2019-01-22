package cn.jinjing.plat.user.pojo;

import java.util.Date;

public class BaseLabelSource {
    private Long autoId;

    private String labelCode;

    private String dataSource;

    private String labelDesc;

    private String mdn;

    private String imei;

    private String ctcc;

    private String cucc;

    private String cmcc;

    private String delSign;

    private String createUser;

    private Date createDate;

    private String remark;

    public Long getAutoId() {
        return autoId;
    }

    public void setAutoId(Long autoId) {
        this.autoId = autoId;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode == null ? null : labelCode.trim();
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource == null ? null : dataSource.trim();
    }

    public String getLabelDesc() {
        return labelDesc;
    }

    public void setLabelDesc(String labelDesc) {
        this.labelDesc = labelDesc == null ? null : labelDesc.trim();
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn == null ? null : mdn.trim();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public String getCtcc() {
        return ctcc;
    }

    public void setCtcc(String ctcc) {
        this.ctcc = ctcc == null ? null : ctcc.trim();
    }

    public String getCucc() {
        return cucc;
    }

    public void setCucc(String cucc) {
        this.cucc = cucc == null ? null : cucc.trim();
    }

    public String getCmcc() {
        return cmcc;
    }

    public void setCmcc(String cmcc) {
        this.cmcc = cmcc == null ? null : cmcc.trim();
    }

    public String getDelSign() {
        return delSign;
    }

    public void setDelSign(String delSign) {
        this.delSign = delSign == null ? null : delSign.trim();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}