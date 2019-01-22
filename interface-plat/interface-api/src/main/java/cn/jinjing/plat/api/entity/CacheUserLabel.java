package cn.jinjing.plat.api.entity;

import java.io.Serializable;

public class CacheUserLabel implements Serializable {

	private String userCode;
	
	private String securityKey; //16位秘钥

	private double remain; //余额

	private String accessKey; //动态口令

	private String labelCode;

	private String type; //0:mdn; 1:imei

	private String telcom; //0:电信; 1:联通；2:移动; 3:其他

	private String dataSource;

	private double price;

	
	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public double getRemain() {
		return remain;
	}

	public void setRemain(double remain) {
		this.remain = remain;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getLabelCode() {
		return labelCode;
	}

	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTelcom() {
		return telcom;
	}

	public void setTelcom(String telcom) {
		this.telcom = telcom;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
