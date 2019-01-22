package cn.jinjing.plat.user.init;

import java.util.Map;
import java.util.Map.Entry;

import cn.jinjing.plat.user.pojo.CacheKey;
import cn.jinjing.plat.user.util.CacheUtil;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.JsonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClearKey extends Thread{
	
	public static Log log = LogFactory.getLog(ClearKey.class);
	private static int KEY_CLEAR_MIN = Integer.parseInt(ConfigUtil.getProperties("key_clear_min"));

	@Override
	public void run() {
		
		long curTime;
		while(true)
		{
			curTime = System.currentTimeMillis();
			try {
				Map<String, String> map = CacheUtil.getAllAccessKey();
				if(map !=null){
					for(Entry<String, String> entry : map.entrySet())
					{
						//超过 KEY_CLEAR_MIN 分钟失效
						String userCode = entry.getKey();
						CacheKey cacheKey = JsonUtil.StringToBean(entry.getValue(), CacheKey.class);
						if(cacheKey != null && ((curTime - cacheKey.getCurtime()) > KEY_CLEAR_MIN * 60 * 1000))
						{
							//清除key
							log.info(">>>>>>>>>>>>>>>>>>>>>>>清理key： " + userCode);
							CacheUtil.removeAccessKey(userCode);
						}
					}
				}
			} catch (Exception e) {
				log.error(ExceptionUtil.printStackTraceToString(e));
			}
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				log.error(ExceptionUtil.printStackTraceToString(e));
			}
		}
		
		
	}
	
}
