package cn.jinjing.inter.util;

import cn.jinjing.inter.pojo.StatusCode;
import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.ReObject;

public class CheckUserUtil {
	
	public static ReObject checkUser(CacheUserLabel cacheUser, String accessKey, String interfaceCode) throws Exception
	{
		ReObject result = new ReObject();
		//验证key
		result = checkAccessKey(cacheUser, accessKey);
		if(!result.isFlag())
		{
			return result;
		}
		//验证接口权限
//		result = checkInterfaces(cacheUser, interfaceCode);
//		if(!result.isFlag())
//		{
//			return result;
//		}
		//验证余额
		result = checkRemain(cacheUser);
		if(!result.isFlag())
		{
			return result;
		}
		result.setFlag(true);
		return result;
	}

	//验证QPS
	public static ReObject checkQPS(String userCode)
	{
		ReObject result = new ReObject();
		boolean flag = false;
		if(InterLimit.interLimit.containsKey(userCode)) 
		{
			if(InterLimit.getPermiss(userCode)){
				flag = true;
			}else{
				result.setMessage("QPS Over Maximum Access Speed");
				result.setCode(StatusCode.OVERSPEED.getCode());
				flag = false;
			}
		}else {
			result.setMessage("QPS Has no Info of This user");
			result.setCode(StatusCode.USERINFO.getCode());
			flag = false;
		}
		
		result.setFlag(flag);
		return result;
	}

	//验证AccessKey
	public static ReObject checkAccessKey(CacheUserLabel cacheUser, String accessKey) 
	{
		ReObject result = new ReObject();
		boolean flag = false;
		String cachekey = cacheUser.getAccessKey();
		if(cachekey != null && accessKey.equals(cachekey))
		{
			flag = true;
		}
		else 
		{
			result.setMessage("User Error or AccessKey is Expired...");
			result.setCode(StatusCode.ACCESSKEYERR.getCode());
			flag = false;
		}
		result.setFlag(flag);
		return result;
	}
	
/*	//验证接口权限
	public static ReObject checkInterfaces(CacheUserLabel cacheUser, String interfaceCode)
	{
		ReObject result = new ReObject();
		boolean flag = false;
		List<CacheInterface> list = cacheUser.getInterfaces();
		if(list!=null && list.size()>0) 
		{
			boolean sign = false;
			for(CacheInterface cacheInterface : list) 
			{
				if(interfaceCode.equals(cacheInterface.getInterfaceCode())) 
				{
					//有权限，返回接口信息
					sign = true;
					break;
				}
			}
			if(sign) 
			{
				flag = true;
			}
			else 
			{
				result.setMessage("No Interface Authority");
				flag = false;
			}
		}
		else 
		{
			result.setMessage("No Interface Authority");
			flag = false;
		}
		result.setFlag(flag);
		return result;
	}
*/
	//验证是否有余额
	public static ReObject checkRemain(CacheUserLabel cacheUser)
	{
		ReObject result = new ReObject();
		boolean flag = false;
		if(cacheUser.getRemain() > 0) {
			flag = true;
		}else{
			result.setMessage("Not Sufficient Funds");
			result.setCode(StatusCode.ARRREARS.getCode());
			flag = false;
		}
		result.setFlag(flag);
		return result;
	}

}
