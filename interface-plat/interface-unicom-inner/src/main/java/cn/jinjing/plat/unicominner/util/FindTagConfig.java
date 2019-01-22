package cn.jinjing.plat.unicominner.util;

import cn.jinjing.plat.unicominner.pojo.LabelInfo;
import cn.jinjing.plat.unicominner.pojo.LabelRule;
import cn.jinjing.plat.unicominner.pojo.LabelRuleMethodParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ho.yaml.Yaml;

import java.io.InputStreamReader;
import java.util.*;

/**
 * @author shipeien
 * @version 1.0
 * @Title: FindAppTag
 * @ProjectName interface-plat
 * @Description: TODO
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1820:27
 */
public class FindTagConfig {
    public static Log log = LogFactory.getLog(FindTagConfig.class);
    public static Map<String, String> TAG_TYPE_INFO = new HashMap<String, String>();
    public static Map<String, LabelInfo> MAP_LABEL_INFO_ALL = new Hashtable<>();
    //风控url
    public static String HTTP_REQUEST_URL_RC = Config.getString("http_request_url_rc");
    //动态
    public static String HTTP_REQUEST_URL_DC = Config.getString("http_request_url_dc");
    /**
     * tag和app对应关系初始化
     */
    public static void initFAT(){
        try {
            InputStreamReader is = new InputStreamReader(Config.class.getClassLoader().getResourceAsStream("find-app-tag.yml"), "UTF-8");
            Map father = Yaml.loadType(is, HashMap.class);
            for(Object key:father.keySet()){
                if("tag-app".equals(key)){
                    //先填入
                    List<HashMap<String,String>> tagMapList= (List<HashMap<String, String>>) father.get(key);
                    for(HashMap tagType:tagMapList ){
                        TAG_TYPE_INFO.put((String)tagType.get("tagName"),(String)tagType.get("tagType"));
                    }
                }
            }
            log.info("加载数据...");
            for(Object key:TAG_TYPE_INFO.keySet()){
                log.info("标签-->"+key+"--"+TAG_TYPE_INFO.get(key));
            }
            log.info("要获取app名称的标签初始化完成...");
        }catch (Exception e){
            log.error("要获取app名称的标签初始化异常...");
            e.printStackTrace();
        }
    }

    /**
     * 所有标签信息初始化-版本信息,标签特殊处理规则信息
     */
    public static void initLabelInfo(){
        try {
            InputStreamReader is = new InputStreamReader(Config.class.getClassLoader().getResourceAsStream("label-info.yml"), "UTF-8");
            Map father = Yaml.loadType(is, HashMap.class);
            for(Object key:father.keySet()){
                if("label-info".equals(key)){
                    //先填入
                    List<HashMap<String,String>> labelMapList= (List<HashMap<String, String>>) father.get(key);
                    for(HashMap tagType:labelMapList ){
                        Map<String,LabelInfo> hashMap=new HashMap<String, LabelInfo>();
                        String labelName=(String) tagType.get("labelName");
                        LabelInfo labelInfo=new LabelInfo();
                        labelInfo.setId((Integer) tagType.get("id"));
                        labelInfo.setVersion(tagType.get("version").toString());
                        labelInfo.setLabelName(labelName);
                        //判断有没有规则
                        if(null!=tagType.get("labelRule")){
                            LabelRule labelRule=new LabelRule();
                            HashMap<String,Object> labelRuleMap= (HashMap<String, Object>) tagType.get("labelRule");
                            labelRule.setRuleMethod((String) labelRuleMap.get("method"));
                            List<HashMap<String,String>> labelRuleMethodParamMap=(List<HashMap<String, String>>) labelRuleMap.get("param");
                            List<LabelRuleMethodParam> labelRuleMethodParamList=new ArrayList<>();
                            for(HashMap paramMap:labelRuleMethodParamMap ){
                                LabelRuleMethodParam labelRuleMethodParam=new LabelRuleMethodParam();
                                labelRuleMethodParam.setOrderDesc((Integer) paramMap.get("ord"));
                                labelRuleMethodParam.setPam(paramMap.get("pam"));
                                labelRuleMethodParam.setType(paramMap.get("type").toString());
                                labelRuleMethodParamList.add(labelRuleMethodParam);
                            }
                            labelRule.setRuleMethodParams(labelRuleMethodParamList);
                            labelInfo.setLabelRule(labelRule);
                        }
                        MAP_LABEL_INFO_ALL.put(labelName,labelInfo);
                    }
                }
            }
            log.info("加载数据...");
            for(Object key:MAP_LABEL_INFO_ALL.keySet()){
                log.info("标签-->"+key+"--"+MAP_LABEL_INFO_ALL.get(key));
            }
            log.info("标签信息初始化完成...");
        }catch (Exception e){
            log.error("标签信息的标签初始化异常...");
            e.printStackTrace();
        }
    }

}
