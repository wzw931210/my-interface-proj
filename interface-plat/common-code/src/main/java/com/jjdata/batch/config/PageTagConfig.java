package com.jjdata.batch.config;

import com.jjdata.batch.model.TagTypeModel;
import com.jjdata.batch.util.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ho.yaml.Yaml;

import java.io.InputStreamReader;
import java.util.*;

/**
 * @author shipeien
 * @version 1.0
 * @Title: PageTagConfig
 * @ProjectName run-batch
 * @Description: 为了参数化改为配置方式这里解析配置文件 tag-type.yml
 * @email shipeien@jinjingdata.com
 * @date 2018/12/17 11:08
 */
public class PageTagConfig {
    protected static Log logger = LogFactory.getLog(PageTagConfig.class);
    public static Map<String, List<TagTypeModel>> TAG_TYPE_MODEL_MAP = new Hashtable<>();
    //初始化要处理的标签信息
    public static void initTagInfo(){
        try {
            logger.info("初始化标签信息...");
            InputStreamReader is = new InputStreamReader(ConfigUtils.class.getClassLoader().getResourceAsStream("tag-type.yml"), "UTF-8");
            Map father = Yaml.loadType(is, HashMap.class);
            for(Object key:father.keySet()){
                if("tag_type".equals(key)){
                    HashMap tag_type= (HashMap) father.get(key);
                    for(Object type_info_obj:tag_type.keySet()){
                        //先填入
                        List<TagTypeModel> tagTypeModelList=new ArrayList<TagTypeModel>();
                        List<HashMap<String,Object>> tagTypeMapList= (List<HashMap<String, Object>>) tag_type.get(type_info_obj);
                        for(HashMap tagTypeMap:tagTypeMapList ){
                            TagTypeModel tagTypeModel=new TagTypeModel((Integer) tagTypeMap.get("id"),(String) tagTypeMap.get("tag_name"),(String)tagTypeMap.get("tag_chname"));
                            tagTypeModelList.add(tagTypeModel);
                        }
                        //再排序
                        Collections.sort(tagTypeModelList,new Comparator<TagTypeModel>(){
                            @Override
                            public int compare(TagTypeModel arg0, TagTypeModel arg1) {
                                return arg0.getId().compareTo(arg1.getId());
                            }
                        });
                        tagTypeModelList.toString();
                        String type_info_str=type_info_obj.toString();
                        //转大写
                        String type_info_str_UC=type_info_str.toUpperCase();
                        TAG_TYPE_MODEL_MAP.put(type_info_str_UC,tagTypeModelList);
                    }
                }
            }
            for(String tagFatherName:TAG_TYPE_MODEL_MAP.keySet()){
                logger.info(tagFatherName+" 标签信息（注意顺序）---->"+TAG_TYPE_MODEL_MAP.get(tagFatherName).toString());
            }
            logger.info("初始化标签信息完成...");
        }catch (Exception e){
            logger.error("初始化异常",e);
            e.printStackTrace();
        }
    }
}
