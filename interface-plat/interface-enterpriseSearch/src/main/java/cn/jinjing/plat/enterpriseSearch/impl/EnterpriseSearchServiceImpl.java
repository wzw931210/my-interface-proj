package cn.jinjing.plat.enterpriseSearch.impl;

import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.enterpriseSearch.util.DES;
import cn.jinjing.plat.enterpriseSearch.util.EnterpriseUtil;
import cn.jinjing.plat.service.enterpriseSearch.EnterpriseSearchService;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.StatusCode;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EnterpriseSearchServiceImpl implements EnterpriseSearchService {

    private static Log log = LogFactory.getLog(EnterpriseSearchServiceImpl.class);
    private static String ENTERPRISE_USER = ConfigUtil.getProperties("user_name");
    private static String API_KEY = ConfigUtil.getProperties("enterprise_api_key");


    @Override
    public ReObject getEnterprise(String enterprise ){
        ReObject reObject = new ReObject(false);
        JSONObject labelValue ;

        try{
            log.info(">>>>>>>>>>>>>>企业搜索接口》》》>>>>");
            String keywords = DES.encrypt(enterprise,API_KEY); //DES加密
            if(keywords != null){
                Map<String, String> httpParams = new HashMap<>();
                httpParams.put("userName",ENTERPRISE_USER);
                httpParams.put("keywords",keywords);

                ReObject result = EnterpriseUtil.getReObject(httpParams);
                if(result.isFlag()){
                    labelValue = result.getData();
                    reObject.setFlag(true);
                    reObject.setData(labelValue);
                    reObject.setMessage(StatusCode.SUCCESS.getValue());
                    reObject.setCode(StatusCode.SUCCESS.getCode());
                }else{
                    reObject.setFlag(false);
                    reObject.setMessage(StatusCode.SYSERR9.getValue());
                    reObject.setCode(StatusCode.SYSERR9.getCode());
                }
            }else {
                log.error("+++++++++++++++++keywords生成失败！");
                reObject.setFlag(false);
                reObject.setMessage(StatusCode.SYSERR9.getValue());
                reObject.setCode(StatusCode.SYSERR9.getCode());
            }
        }catch(Exception e){
            reObject.setFlag(false);
            reObject.setMessage(StatusCode.SYSERR9.getValue());
            reObject.setCode(StatusCode.SYSERR9.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return reObject;
    }

    public static void main(String [] args ){
        EnterpriseSearchServiceImpl en = new EnterpriseSearchServiceImpl();
        en.getEnterprise("晋景");
    }
}
