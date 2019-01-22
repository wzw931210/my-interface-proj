package cn.jinjing.plat.comlabel.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProvinceMap {

    public final static Map<String,String> provinceMap ;

    static{
        Map<String,String> map = new HashMap<>();
        map.put("province_11", "BJ");
        map.put("province_12", "TJ");
        map.put("province_13", "HE");
        map.put("province_14", "SX");
        map.put("province_15", "NM");
        map.put("province_21", "LN");
        map.put("province_22", "JL");
        map.put("province_23", "HL");
        map.put("province_31", "SH");
        map.put("province_32", "JS");
        map.put("province_33", "ZJ");
        map.put("province_34", "AH");
        map.put("province_35", "FJ");
        map.put("province_36", "JX");
        map.put("province_37", "SD");
        map.put("province_41", "HA");
        map.put("province_42", "HB");
        map.put("province_43", "HN");
        map.put("province_44", "GD");
        map.put("province_45", "GX");
        map.put("province_46", "HI");
        map.put("province_50", "CQ");
        map.put("province_51", "SC");
        map.put("province_52", "GZ");
        map.put("province_53", "YN");
        map.put("province_54", "XZ");
        map.put("province_61", "SN");
        map.put("province_62", "GS");
        map.put("province_63", "QH");
        map.put("province_64", "NX");
        map.put("province_65", "XJ");
        provinceMap = Collections.unmodifiableMap(map);
    }

}
