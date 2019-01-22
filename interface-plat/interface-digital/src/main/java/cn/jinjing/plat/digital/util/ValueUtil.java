package cn.jinjing.plat.digital.util;

import cn.jinjing.plat.digital.pojo.LabelInfo;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.LocalLabelData;
import cn.jinjing.plat.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueUtil {


    private static String SAVE_KEY= ConfigUtil.getProperties("save_key");
    /**
     * 拆分value值，并按标签名存盘，返回需要的某个标签值
     */
    public static String splitAndSave(List<LabelInfo> labelList,String column, String value, String localTableName,int length, String mdn, String month) {
        String returnValue = "";
        int indexStart;
        int indexEnd;
        String label;
        Map<String, String> map = new HashMap<>();
        if (length != 0) {//length不为0，表示需要拆分
            String labelValue = value.replaceAll("\\.","");
            while(labelValue.length() < length){//长度不正确在左边补0
                labelValue = "0" + labelValue;
            }
            for (LabelInfo labelInfo : labelList) {
                label = labelInfo.getLabel();
                indexStart = Integer.parseInt(labelInfo.getIndexStart());
                indexEnd = Integer.parseInt(labelInfo.getIndexEnd());
                map.put(label, labelValue.substring(indexStart, indexEnd));

                if (label.equals(column)) {
                    returnValue = labelValue.substring(indexStart, indexEnd);//获取返回值
                }
            }
            if("true".equals(SAVE_KEY)){//存盘开关为开
                LocalLabelData.putLocalLabels(localTableName, mdn, month, map);
            }
        }else{
            //无需拆分的结果,value即为需要的返回值，直接存盘
            returnValue = value;
            if("true".equals(SAVE_KEY)){//存盘开关为开
                LocalLabelData.putLocalLabel(localTableName, mdn, month, column, value);
            }
        }
        return returnValue;
    }

    public static void main(String[] args) {
        String value = "1.2";
        String labelValue = value.replaceAll("\\.","");
        System.out.println(labelValue);
    }
}
