package cn.jinjing.plat.digital.util;

import cn.jinjing.plat.digital.pojo.LabelInfo;
import cn.jinjing.plat.util.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class LabelMap {

    private static Log log = LogFactory.getLog(LabelMap.class);

    public final static Map<String,String> labelTypeMap = new HashMap<>();
    public final static Map<String,List<LabelInfo>> typeLabelMap = new HashMap<>();

    static{
        try{
                File file = new File("/interface/conf/labelInfo.txt");
//            File file = new File("D:\\batch\\telecom\\labelInfo.txt");
            if( file.exists()){
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line ;
                String type = "";
                String type1 = "";//此时对应的是第一行数据
                List<LabelInfo> labelList = new ArrayList<>();
                while( (line = reader.readLine()) != null ) {
                    String [] array = line.split(",");
                    String label = array[0];
                    String indexStart = array[1];
                    String indexEnd = array[2];//indexStart-indexEnd=0时，表示不用拆分

                    type = array[3];//type_length
                    labelTypeMap.put(label, type);

                    //type变化时，之前labelList数据插入typeLabelMap中，并重新new一个labelList
                    if ( !"".equals(type1) && !type.equals(type1) ) {
                        typeLabelMap.put(type1, labelList);
                        labelList = new ArrayList<>();
                    }
                    LabelInfo labelInfo = new LabelInfo(label,indexStart, indexEnd, type);
                    labelList.add(labelInfo);
                    type1 = array[3];
                }
                if(labelList.size() > 0 ){
                    typeLabelMap.put(type, labelList);
                }
            }else{
                log.error("《《《《《《《《《《《读取labelInfo.txt配置文件失败！找不到配置文件！");
            }
        }catch (Exception e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            log.error("《《《《《《《《《《《读取labelInfo.txt配置文件失败！");
        }
    }

    public static void main (String [] args){
        String types = LabelMap.labelTypeMap.get("s3ConsumerFinaAppTopThree");
        List<LabelInfo> labelList = LabelMap.typeLabelMap.get(types);
        for(LabelInfo labelInfo : labelList){
            String typeAndLength = labelInfo.getType();
            String type = typeAndLength.split("_")[0];
            System.out.println("label="+labelInfo.getLabel()+";"+"type="+type+"开始结束"+labelInfo.getIndexStart()+labelInfo.getIndexEnd());
        }
    }
}
