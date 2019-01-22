package cn.jinjing.inter.util;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InterLimit {
	public static Log log = LogFactory.getLog(InterLimit.class);
	public static ConcurrentHashMap<String, RateLimiter> loginLimit = new ConcurrentHashMap<String, RateLimiter>();
    public static ConcurrentHashMap<String, RateLimiter> interLimit = new ConcurrentHashMap<String, RateLimiter>();

    //interCode --> userCodeinterfaceCode
    public static void createLimiter(String userCode, double qps){
        if(interLimit.contains(userCode)){
            interLimit.get(userCode).setRate(qps);
            log.info("update interLimit: " + userCode + "--" + qps);
        }else{
            RateLimiter rateLimiter = RateLimiter.create(qps);
            interLimit.putIfAbsent(userCode, rateLimiter);
            log.info("add interLimit: " + userCode + "--" + qps);
        }
    }
    
    public static void loginLimiter(String userCode,double qps) {
    	if(loginLimit.containsKey(userCode)) {
    		loginLimit.get(userCode).setRate(qps);
    		log.info("login interLimit: " + userCode + "--" + qps);
    	}else{
    		RateLimiter rateLimiter = RateLimiter.create(qps);
    		loginLimit.putIfAbsent(userCode, rateLimiter);
    		log.info("add login interLimit: " + userCode + "--" + qps);
    	}
    }

    public static boolean getPermiss(String userCode){
        boolean flag = false;
        if (interLimit.containsKey(userCode) && interLimit.get(userCode).tryAcquire()) {
            flag = true;
        }
       return flag;
    }

    public static boolean getLoginPermiss(String userCode){
        boolean flag = false;
        if (loginLimit.containsKey(userCode) && loginLimit.get(userCode).tryAcquire()) {
            flag = true;
        }
       return flag;
    }
}
