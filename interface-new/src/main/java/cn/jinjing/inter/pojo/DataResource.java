package cn.jinjing.inter.pojo;

import java.util.HashMap;
import java.util.Map;

public class DataResource {
	
	public final static Map<String, Object> dataResource = new HashMap<>();
	
	static {
		dataResource.put("00", "");//电信内部
		dataResource.put("01", "");//电信公共
		dataResource.put("02", "");//联通内部
		dataResource.put("03", "");//联通公共
		dataResource.put("04", "");//数尊
		dataResource.put("05", "");//邮政
	}
}
