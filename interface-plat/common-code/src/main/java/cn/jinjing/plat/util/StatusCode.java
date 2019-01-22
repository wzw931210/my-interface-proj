package cn.jinjing.plat.util;

public enum StatusCode {

    SUCCESS("00","成功！"),
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
    SYSERR2_1("2423","超速"),//电信基础-超速 超速-次数用完了
    SYSERR2_2("2501","超速"),//电信内部我们自己的程序报错
    SYSERR3("13","系统异常3"),//电信公共
    SYSERR4("14","系统异常4"),//联通内部
    SYSERR4_0("1400","无效请求"),//联通风控14错误码和动态标签400错误码
    SYSERR4_3("1403","无标签值"),//联通风控14错误码和动态标签403错误码
    SYSERR4_8("1408","本月标签未入库"),//联通风控错误码和动态标签408错误码
    SYSERR4_7("1414","非归属运营商号码"),//联通风控错误码和动态标签408错误码
    SYSERR4_8_1("1408_1","本月版本无该标签"),//系统内部版本错误提示
    SYSERR4_9("1409","线程忙，请求暂时不能处理"),//联通风控14错误码和动态标签
    SYSERR5("15","系统异常5"),//联通公共
    SYSERR6("16","系统异常6"),//数尊
    SYSERR7("17","系统异常7"),//三要素
    SYSERR8("18","系统异常8"),//地址库
    SYSERR9("19","系统异常9"),//企业搜索
    PROVINCEERR("20","未能获取号码省份信息")//电信公共-是否实名
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
