package cn.jinjing.inter.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jinjing.plat.api.entity.ReObject;

public class CheckParams {
	
	private static int DEALY_MONTH = Integer.parseInt(ConfigUtil.getProperties("dealy_month"));
	private static int DURA_MONTH = Integer.parseInt(ConfigUtil.getProperties("dura_month"));
	
	public static ReObject checkMonth(String month) 
	{
		ReObject result = new ReObject();
		boolean flag = true;
		
		DateFormat df = new SimpleDateFormat("yyyyMM");
		if(month != null && !"".equals(month)) 
		{
			try {
				if((new Date().getTime() - df.parse(month).getTime()) < DEALY_MONTH*30*24*60*60*1000L  ||  (new Date().getTime() - df.parse(month).getTime()) > (DEALY_MONTH + DURA_MONTH)*30*24*60*60*1000L)
				{
					flag = false;
					result.setMessage("Month Must be Time Range " + DEALY_MONTH + " Months Ago and Within " + DURA_MONTH + " Months");
				}
			} catch (ParseException e1) {
				flag = false;
				result.setMessage("Incorrect Month Format");
			}
		}
		else {
			flag = false;
			result.setMessage("Month Can't be Empty");
		}
		result.setFlag(flag);
		return result;
	}

	public static ReObject checkMonth1(String month){
		ReObject result = new ReObject();
		boolean flag = true;
		if(StringUtil.isEmpty(month) || month.length() != 6 ){
			flag = false;
			result.setMessage("Incorrect Month Format");
		}
		result.setFlag(flag);
		return result;
	}

}
