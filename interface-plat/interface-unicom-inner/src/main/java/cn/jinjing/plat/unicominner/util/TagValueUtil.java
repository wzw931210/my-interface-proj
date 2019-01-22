package cn.jinjing.plat.unicominner.util;

/**
 * Created by Joy.M on 2018/11/15 20:26
 */
public class TagValueUtil {

    /**
     * 以后可以扩展这个方法，做值的处理
     * @param tagName
     * @param tagValue
     * @return
     */
    public static String handleTagValue(String tagName,String tagValue){
        String tag_type = FindTagConfig.TAG_TYPE_INFO.get(tagName);
         if(null!=tag_type&&!"".equals(tag_type)){
            tagName=tag_type;
            return appCode2Name(tagName,tagValue);
        }else if(tagName.endsWith("TopOne") || tagName.endsWith("TopThree")){
            return appCode2Name(tagName,tagValue);
        }else{
            return tagValue;
        }
    }

    /**
     * 把appCode 转为appName
     * @param tagName
     * @param appCode
     * @return
     */
    public static String appCode2Name(String tagName,String appCode){
        if(appCode.equals("000")){
            return "无";
        }else {
            String appCodeCat = Config.getString("app_tag2codecat_" + tagName);
            return Config.getString(appCodeCat + "_" + appCode);
        }
    }
}
