package cn.jinjing.plat.digital.init;

import java.util.HashMap;

import cn.jinjing.plat.digital.util.CacheUtil;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.StringUtil;
import cn.jinjing.plat.util.UHttpClient;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TokenThread implements Runnable {
	
	public static Log log = LogFactory.getLog(TokenThread.class);

    //更换成自己的apikey
    private static final String APIKEY = ConfigUtil.getProperties("request_apikey");
   //更换成自己的秘钥
    private static final String SECRETKEY = ConfigUtil.getProperties("request_pwd");
    //访问的IP地址或域名
    private static final String HOST = ConfigUtil.getProperties("request_host");
    //端口
    private static final int PORT = Integer.parseInt(ConfigUtil.getProperties("request_port"));
    // 协议
    private static final String PROTOCOL = "http://";
    // 分隔符
    private static final String SEPARATOR = "/";
    //获取Token失败重试次数
    private static int  RETRY = 3;
    //过期时间
    private long expireTime = 0L;

    public TokenThread(long expireTime){
        this.expireTime = expireTime;
    }

    public void run() {
        try {
            String token_time = CacheUtil.getTokenAndTime();
            boolean update = false;
            if(StringUtil.isEmpty(token_time)){
                update = true;
            }else if(token_time.split("#").length == 2){
                String ms = token_time.split("#")[1];
                if((System.currentTimeMillis() - Long.valueOf(ms)) >= (expireTime * 1000 * 60)){
                    update = true;
                }
            }else{
                update = true;
            }
            if(update){
                // 重置token
                String tokenId = getToken(APIKEY);
                if(tokenId != null && !"".equals(tokenId)){
                    log.info(">>>>>>>>>>>>>>>>>>>>>>> Reset Token: "+tokenId);
                    CacheUtil.putTokenAndTime(tokenId);
                }else{
                    log.info(">>>>>>>>>>>>>>>>>>>>>>> TokenId is Empty and Reset Roken Fail...");
                }
            }
        } catch (Exception e) {
            log.error(">>>>>>>>>>>>>>>>>>>>>>> Reset Token Error...");
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
    }


    /**
     * 获取tokenID
     * @param apiKey2
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String getToken(String apiKey2) {
        int retry = RETRY;
        boolean flag = false;
        String token = null;
        String url = getUrl(2);
        HashMap<String, String> params = new HashMap<String, String>();

        while(retry-- > 0){
            try {
                String publicKey = getPublicKey(APIKEY);
                if(publicKey != null && !"".equals(publicKey)){
                    String sign = DigestUtils.md5Hex(APIKEY.concat(SECRETKEY).concat(publicKey));
                    params.put("apiKey", apiKey2);
                    params.put("authCode", publicKey);
                    params.put("sign", sign);
                    UHttpClient.Res res ;
                    res = UHttpClient.doRequest(UHttpClient.Method.get, url, params, "UTF-8", true);
                    if(res != null){
                        JSONObject valueMap = JSONObject.parseObject(res.content);
                        if (res.statusCode == 200) {
                            flag = true;
                            JSONObject  tokenMap=  valueMap.getJSONObject("data");
                            token = tokenMap.getString("token");
                        }
                        else{
                            log.error("Get Token Error and Message is: " + valueMap);
                        }
                    }
                }else {
                    log.error("PublicKey is Empty...");
                }
            }catch (Exception e){
                log.error("Get Token Internal Error...");
                log.error(ExceptionUtil.printStackTraceToString(e));
            }

            if(flag){
                break;
            }else{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
                log.error("Get Token Error and Retry...");
            }
        }
        return token;
    }


    /**
     * 拼接请求url
     * @param type url 类型
     * @return
     */
    private static String getUrl(int type) {
        StringBuilder sb = new StringBuilder();
        sb.append(PROTOCOL).append(HOST).append(":").append(PORT);
        sb = type == 1 ? sb.append("/restful/system/publicKey.json") : sb.append("/restful/system/token.json");
        return sb.toString();

    }

    /**
     * 获取 authcode
     * @param userApiKey
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static String getPublicKey(String userApiKey) {
        String url = getUrl(1);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("apiKey", userApiKey);
        try{
            UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.get, url, params, "UTF-8", true);
            if(res != null){
                JSONObject valueMap = JSONObject.parseObject(res.content);
                if (res.statusCode == 200) {
                    return valueMap.getString("data");
                }else{
                    log.error("Get PublicKey Error and Message is: " + valueMap);
                }
            }
        } catch (Exception e) {
            log.error("Get PublicKey Internal Error...");
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return null;
    }

    /**
     * 获得请求业务接口的url
     * @param product
     * @param module
     * @param method
     * @return
     */
    public static String getReqUrl(String product, String module, String method) {
        String tokenId = CacheUtil.getToken();
        if(StringUtil.isEmpty(tokenId)){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(PROTOCOL).append(HOST).append(":").append(PORT).append("/restful");
        sb.append(SEPARATOR).append(product).append(SEPARATOR).append(module).append(SEPARATOR).append(method)
                .append(SEPARATOR).append(APIKEY).append(SEPARATOR).append(tokenId).append(".json");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(Long.valueOf("1522234814702"));
//        TokenThread.getToken("564AB8A48117B61C208060DDF26CA365");
        new TokenThread(1).run();
    }

}
