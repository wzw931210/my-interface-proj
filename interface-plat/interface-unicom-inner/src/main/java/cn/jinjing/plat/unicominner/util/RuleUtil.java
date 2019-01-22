package cn.jinjing.plat.unicominner.util;

import cn.jinjing.plat.unicominner.pojo.LabelInfo;
import cn.jinjing.plat.unicominner.pojo.LabelRuleMethodParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author shipeien
 * @version 1.0
 * @Title: RuleUtil
 * @ProjectName interface-plat
 * @Description: TODO
 * @email shipeien@jinjingdata.com
 * @date 2018/12/2517:24
 */
public class RuleUtil {
    public static Log log = LogFactory.getLog(RuleUtil.class);
    /**
     *
     * @param data 要处理的数据
     * @param begin 截取开始位置  --从零开始
     * @param end   截取结束位置
     * @param eqValue  如果等于这个值则替换成replaceValue值，否则不替换
     * @param replaceValue 将要替换的值
     * @return
     */
    public static String subStrEqNewStr(String data,Integer begin,Integer end,String eqValue,String replaceValue){
        String reg=data;
        try {
            if(null!=data&&data.length()>=end){
                //判断是否需要为等于某值才替换
                if(null!=eqValue){
                    if(eqValue.equals(data.substring(begin,end))){
                        StringBuilder sb = new StringBuilder(data);
                        reg=sb.replace(begin, end, replaceValue).toString();
                    }else{
                        reg=data;
                    }
                }else{
                    StringBuilder sb = new StringBuilder(data);
                    reg=sb.replace(begin, end, replaceValue).toString();
                }
            }
            return  reg;
        }catch (Exception e){
            e.printStackTrace();
            log.error("替换值失败--->"+data);
        }
        return  reg;
    }

    /**
     *
     * @param labelVersion 标签版本
     * @param selMonth   查询月份
     * @return  true 通过可以查询，否则查询版本不对
     */
    public static boolean checkLabelVersion (String labelVersion,String selMonth){
        boolean isSuccess=true;
        try {
            if(null!=labelVersion&&null!=selMonth){
               int labelVersionInt= Integer.parseInt(labelVersion);
               int selMonthInt= Integer.parseInt(selMonth);
               //如果标签版本小于请求月份则标识无法处理
               if(selMonthInt<labelVersionInt){
                   isSuccess=false;
               }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("标签版本校验失败",e);
        }
        return  isSuccess;
    }

    /**
     *
     * @param method  方法名称
     * @param ruleMethodParams 参数列表
     * @return
     */
    public static Object excMethod(String method,List<LabelRuleMethodParam> ruleMethodParams){
        Object result = null;
        try {
            String className = "cn.jinjing.plat.unicominner.util.RuleUtil";
            Class clz = Class.forName(className);
            Object obj = clz.newInstance();
            int params=ruleMethodParams.size();
            Class[] classes=null;
            Object[] objects=null;
            if(null!=ruleMethodParams&&ruleMethodParams.size()>0){
                classes =new Class[params];
                objects=new Object[params];
                for(int i=0;i<params;i++){
                    LabelRuleMethodParam labelRuleMethodParam =ruleMethodParams.get(i);
                    classes[i]= Class.forName(labelRuleMethodParam.getType());
                    objects[i] = labelRuleMethodParam.getPam();
                }
            }
            //获取方法
            Method m = obj.getClass().getDeclaredMethod(method, classes);
            //调用方法
             result =  m.invoke(obj, objects);
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
            log.error("执行方法出错",e);

        }
        return  result;
    }

    /**
     * 校验标签值是否需要特殊处理
     * @param label 标签名称
     * @param data 标签值
     * @return
     */
    public static String checkHandleValue(String label,String data){
        String reg=data;
        try {
            //职业类型需要特殊处理值的标签
            LabelInfo labelInfo=FindTagConfig.MAP_LABEL_INFO_ALL.get(label);
            //除非错误否则肯定不会为null
            if(null!=labelInfo){
                if(null!=labelInfo.getLabelRule()){
                    List<LabelRuleMethodParam> ruleMethodParams = null;
                    if(null!=labelInfo.getLabelRule().getRuleMethodParams()&&labelInfo.getLabelRule().getRuleMethodParams().size()>0){
                        ruleMethodParams=labelInfo.getLabelRule().getRuleMethodParams();
                        for(LabelRuleMethodParam labelRuleMethodParam:ruleMethodParams){
                            if("value".equals(labelRuleMethodParam.getPam().toString())){
                                labelRuleMethodParam.setPam(data);
                            }
                            if("java.lang.String".equals(labelRuleMethodParam.getType())){
                                labelRuleMethodParam.setPam(labelRuleMethodParam.getPam().toString());
                            }
                        }
                    }
                    if(null!=RuleUtil.excMethod(labelInfo.getLabelRule().getRuleMethod(),ruleMethodParams)){
                        reg= (String) RuleUtil.excMethod(labelInfo.getLabelRule().getRuleMethod(),ruleMethodParams);
                    }
                }
            }else{
                try {
                    throw new Exception("未记录【"+label+"】标签信息，请注意......");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("特殊处理出错",e);
        }
        return  reg;
    }

}
