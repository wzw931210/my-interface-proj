package cn.jinjing.inter.pojo;

public enum StatusCode {
	
	USERORPASSWD("01","用户名或密码错误"),
	LOGINOVERTIMES("02","用户登录超过次数限制"),
	SYSERR1("03","系统异常1"),
	
	OVERSPEED("04","调用超速"),
	PARAMSERR("05","参数异常"),
	OVERMONTH("06","查询时间超出范围"),
	USERINFO("07","用户信息未同步"),//获取不到缓存：用户信息未同步
	ACCESSKEYERR("08","口令超时"),
	OVERPERMISSION("09","接口调用权限未开通"),
	ARRREARS("10","账户余额不足"),
	
	TELCOMERR("11","非归属运营商号码"),
	SYSERR2("12","系统异常2"),//电信基础
	SYSERR3("13","系统异常3"),//电信公共
	SYSERR4("14","系统异常4"),//联通内部
//    SYSERR4_2("1402","系统异常402"),//联通动态
	SYSERR5("15","系统异常5"),//联通公共
	SYSERR6("16","系统异常6"),//数尊
	SYSERR7("17","系统异常7"),//三要素
	SYSERR8("18","系统异常8"),//地址库
	SYSERR9("19","系统异常9"),//企业搜索
	PROVINCEERR("20","未能获取号码省份信息"),//电信公共-是否实名
	YGNUMERR("21","接收信息数量不一致"),//接收信息数量不一致-YG
	YGNUMERR1("22","暂无阳光数据结果")
	;
	
	String code; 
	String value;
	
	StatusCode(String code,String value){
		this.code = code;
		this.value = value;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getValue() {
		return this.value;
	}

}
