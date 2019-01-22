package com.jjdata.batch.service;

import com.jjdata.batch.config.TaskConfig;
import com.jjdata.batch.model.UserInfoModel;
import com.jjdata.batch.task.BatchDataTask;
import com.jjdata.batch.util.FileUtils;
import com.jjdata.batch.util.MD5;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.mapreduce.HashTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shipeien
 * @version 1.0
 * @Title: TaskServver
 * @ProjectName interface-plat
 * @Description: 任务处理
 * @email shipeien@jinjingdata.com
 * @date 2019/1/1119:03
 */
public class TaskService {
    protected static Log logger = LogFactory.getLog(BatchDataTask.class);

    private String telTileTable=TaskConfig.save_data_db+TaskConfig.data_file_name;
    private String telTileTable_over=TaskConfig.save_data_db+TaskConfig.data_file_name+"_over";
    private String priceTable=TaskConfig.save_data_db+"price.txt";
    private static Map<String,Long> nowNum=new HashMap<>();

    /**
     * 执行标签方法
     * @param mdnList
     * @throws InterruptedException
     */
    public boolean excTag(List<String> mdnList)  {
        boolean isRun=true;
        String priceTable=TaskConfig.save_data_db+"price.txt";
        try {
            String userCode=TaskConfig.HTTP_DEFAULT_USERCODE;
            //记录可以掉用次数，如果次数用完
            //先登录系统同步秘钥
            UserInfoModel userInfoModel = TagRequestService.findRequestUser(userCode);
            logger.info("判断秘钥有没有同步成功--------------------------"+userInfoModel.getUsercode());
            String flag= TagRequestService.loginSys(userCode);
            if(null!=flag&&!"ERROR".equals(flag)){
                logger.info("开始获取标签--------------------------"+ TaskConfig.BATCH_TYPE);
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String fileName=format.format(System.currentTimeMillis())+"_"+TaskConfig.LABEL_FILE_NAME+"_"+TaskConfig.RUN_MONTH;
                boolean exc= TagRequestService.findTelTag(mdnList,fileName,TaskConfig.RUN_MONTH,userCode,TaskConfig.TELE_UNICOM,TaskConfig.BATCH_TYPE);
                logger.info("获取标签结束--------------------------"+TaskConfig.RUN_MONTH);

            }else{
                logger.error("登录失败..."+userInfoModel.getUsercode());
                Thread.sleep(1000*60);
            }
        }catch (Exception e){
            e.printStackTrace();
            isRun=false;
        }
        return isRun;
    }

    /**
     * 读取号段
     * @param telTileTable
     * @return
     */
    public  List<String> listTelTitle(String telTileTable){
        List<String> list=null;
        if(nowNum.keySet().size()>0){
            list=new ArrayList();
            for(String key:nowNum.keySet()){
                list.add(key);
            }
            logger.info("读取MAP："+list.toString());
        }else{
           list = FileUtils.readFileLine(3,telTileTable);
           logger.info("读取号码："+list.toString());
           FileUtils.delTextFileContent(3,telTileTable);
        }
        return list;
    }

    /**
     * 判断当前值是否已经是最大值
     * @param nowTel
     * @param maxTel
     * @param numKey
     * @return
     */
    public boolean genMaxCheck(Long nowTel,Long maxTel,String numKey){
        boolean isMax=false;
        if(nowTel >= maxTel){
            //已经到最大值不需要生成号码只需要存库就行
            nowNum.remove(numKey);
            FileUtils.writeFile(numKey,telTileTable_over);
            isMax=true;
        }
        return isMax;
    }

    /**
     * 生产号码
     * @param mdnList
     * @return
     */
    public List<String> genMobile(List<String> mdnList){
        try {
            if(null==mdnList){
                mdnList=new ArrayList<>();
            }
            //号码数量
            int telNum=0;
            int runTelNum=TaskConfig.BATCH_GENERATE_TEL_NUM;
            if(TaskConfig.price_check){
                if(TaskConfig.publicPrice>0){
                    FileUtils.writeTxtFile(TaskConfig.publicPrice.toString(),priceTable);
                    if(TaskConfig.publicPrice>runTelNum){
                        telNum=runTelNum;
                    }else{
                        telNum=TaskConfig.publicPrice;
                    }
                }else{
                    FileUtils.writeTxtFile(TaskConfig.publicPrice.toString(),priceTable);
                    throw new Exception("额度用完了..");
                }
            }else{
                telNum=runTelNum-mdnList.size();
            }
            //获取号段
            List<String> listTelTitle = listTelTitle(telTileTable);
            //号段没有了
            if(null!=listTelTitle&&listTelTitle.size()>0){
                //如果大于最大值了
                if(mdnList.size()>runTelNum){
                    return  mdnList;
                }else{
                    for(String numKey:listTelTitle){
                        //生成可生成的最大值
                        String maxTelStr= StringUtils.rightPad(numKey, 11, "9");
                        Long maxTel = new Long(maxTelStr);
                        Long nowTel = null;
                        if(null==nowNum.get(numKey)){
                            nowTel =  new Long( StringUtils.rightPad(numKey, 11, "0"));
                            nowNum.put(numKey,nowTel);
                        }else{
                            nowTel = nowNum.get(numKey);
                        }
                        //如果生成完了只要记录值就行了
                        if(mdnList.size()>=runTelNum){
                            continue;
                        }else{
                            if(!genMaxCheck(nowTel,maxTel,numKey)){
                                for(int im=0;im<telNum;im++){
                                    //从xxxxxxx2100开始生成号码-根据配置
                                    if("0000".equals((nowTel+"").substring(7))){
                                        nowTel=nowTel+TaskConfig.num_begin_telTile;
                                    }else{
                                        nowTel++;
                                    }
                                    //判断生成好号码是否大于最大值了切换号段
                                    if(genMaxCheck(nowTel,maxTel,numKey)){
                                        //跳过不要生成了，
                                        break;
                                    }
                                    String mdnMdnSrc=nowTel+","+ MD5.encryption(nowTel+"").toUpperCase();
                                    mdnList.add(mdnMdnSrc);
                                }
                                //如果生成值还没有到最大值则记录值
                                if(!genMaxCheck(nowTel,maxTel,numKey)){
                                    nowNum.put(numKey,nowTel);
                                }
                            }
                        }
                    }
                    //如果生成的数量达到要求了就返回，否则继续生成
                    if(mdnList.size()>=runTelNum){
                        return  mdnList;
                    }else{
                        genMobile(mdnList);
                    }
                }
            }else{
                logger.info("号段没有了.....");
                return  mdnList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return mdnList;
        }
        return mdnList;
    }
    /**
     * 生产号码无加密
     * @return
     */
    public List<String> selMobileNoMd5(){
        List<String> mdnList = FileUtils.readFileLine(TaskConfig.BATCH_GENERATE_TEL_NUM,telTileTable);
        FileUtils.delTextFileContent(TaskConfig.BATCH_GENERATE_TEL_NUM,telTileTable);
        List<String> mdnList_MD5 = new ArrayList<>();
        for(String str:mdnList){
            String mdnMdnSrc=str+","+ MD5.encryption(str+"").toUpperCase();
            mdnList_MD5.add(mdnMdnSrc);
        }
        return mdnList_MD5;
    }
    /**
     * 生产号码已經加密處理
     * @return
     */
    public List<String> selMobileMd5(){
        List<String> mdnList = FileUtils.readFileLine(TaskConfig.BATCH_GENERATE_TEL_NUM,telTileTable);
        FileUtils.delTextFileContent(TaskConfig.BATCH_GENERATE_TEL_NUM,telTileTable);
        return mdnList;
    }
    /**
     * 号码段生成方法
     * @return
     */
    public  List<String> mdnList()  {

        ///生成1000个号码，一次调用1000个
        List<String> mdnList = null;
        try {
            //号码数量
            int telNum=0;
            int runTelNum=TaskConfig.BATCH_GENERATE_TEL_NUM;
            if(TaskConfig.price_check){
                if(TaskConfig.publicPrice>0){
                    FileUtils.writeTxtFile(TaskConfig.publicPrice+"",priceTable);
                    if(TaskConfig.publicPrice>runTelNum){
                        telNum=runTelNum;
                    }else{
                        telNum=TaskConfig.publicPrice;
                    }
                }else{
                    FileUtils.writeTxtFile(TaskConfig.publicPrice+"",priceTable);
                    throw new Exception("额度用完了..");
//                    return;
                }
            }else{
                telNum=runTelNum;
            }
            mdnList =new ArrayList<>();
            //校验号码生成到哪里了
            String telTileTable=TaskConfig.save_data_db+TaskConfig.data_file_name;
            //判断map里面是否有值-如果有值则从map里面读取

            List<String> listTelTitle = listTelTitle(telTileTable);
            String telDbContent="";
            String telDbContent_over="";

            if(listTelTitle.size()==1){
                String telTitleStr=listTelTitle.get(0);
                logger.info("读取号码段信息"+telTitleStr.substring(0,100));
                if(null!=telTitleStr){
                    String[] telTitleArr=telTitleStr.split("\\|");
//                    for(String telInfo:telTitleArr){
                    for(int ix=0;ix<telTitleArr.length;ix++){
                        String telInfo=telTitleArr[ix];
                        String[] telInfoArr=telInfo.split(",");

                        String maxTelStr= StringUtils.rightPad(telInfoArr[0], 11, "9");
                        Long maxTel = new Long(maxTelStr);
                        Long nowTel = new Long(telInfoArr[1]);
                        if(nowTel >= maxTel){
                            //已经到最大值不需要生成号码只需要存库就行
                            if(ix==0){
//                                telDbContent=telInfo;
                                telDbContent_over=telInfo;
                            }else{
                                telDbContent=telDbContent+"|"+telInfo;
                            }
                            continue;
                        }else{ //如果号码生成完毕只写入文件即可
                            if(mdnList.size()>=runTelNum){
                                telDbContent=telDbContent+"|"+telInfo;
                                continue;
                            }else{
                                telNum = telNum-mdnList.size();
                                for(int im=0;im<telNum;im++){
                                    //从xxxxxxx2100开始生成号码-根据配置
                                    if("0000".equals((nowTel+"").substring(7))){
                                        nowTel=nowTel+TaskConfig.num_begin_telTile;
                                    }else{
                                        nowTel++;
                                    }
                                    //判断生成好号码是否大于最大值了切换号段
                                    if(nowTel >= maxTel){
                                        break;
                                    }
                                    String mdnMdnSrc=nowTel+","+ MD5.encryption(nowTel+"").toUpperCase();
                                    mdnList.add(mdnMdnSrc);
                                }
                                if(ix==0){
                                    telDbContent=telInfoArr[0]+","+nowTel;
                                }else{
                                    telDbContent=telDbContent+"|"+telInfoArr[0]+","+nowTel;
                                }
                            }
                        }
                    }
                }
            }else{
                throw new Exception("获取号码段异常..");
            }
            if(null==mdnList||mdnList.size()==0){
                throw new Exception("号码段生成完了，没有号码可以生成了..");
            }
            //写回文件
            FileUtils.writeTxtFile(telDbContent,telTileTable);
            FileUtils.writeTxtFile(telDbContent_over,telTileTable+"_over");
        }catch (Exception e){
            e.printStackTrace();
        }
        return mdnList;
    }

    /**
     * 程序限制校验 时间限制，额度限制
     * @return
     */
    public static boolean checkRun(){
        String priceTable=TaskConfig.save_data_db+"price.txt";
        boolean isRun=true;
        try {
            logger.info("任务处理");
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            //校验剩余
            if(TaskConfig.price_check){
                List<String> listPrice = FileUtils.readFile(priceTable);
                if(listPrice.size()==1){
                    logger.info("当前额度----------->"+listPrice.get(0));
                    TaskConfig.publicPrice=Integer.parseInt(listPrice.get(0));
                    if(TaskConfig.publicPrice<=0){
                        throw new Exception("额度用完了..");
                    }
                }else{
                    throw new Exception("获取额度异常..");
                }

            }
            if(TaskConfig.date_check){
                int timeInfo=Integer.parseInt(format.format(System.currentTimeMillis()));
                if(timeInfo>=TaskConfig.TIME_INFO_MAX){
                    logger.info(".到达时间了不能跑了停下来...."+TaskConfig.TIME_INFO_MAX);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            isRun=false;
        }
        return  isRun;
    }

    /**
     * 隔日是否可以运行校验
     * @return
     */
    public static boolean nextDayCheck(){
        boolean isRun=true;
        try {
            //判断是否需要隔日校验
            if(TaskConfig.next_date_check){
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                int timeInfo=Integer.parseInt(format.format(System.currentTimeMillis()));
                //如果时间是今天，且今天次数已经用完了
                if(!TaskConfig.isRun&&TaskConfig.last_date==timeInfo){
                    //休眠10分钟
                    logger.info(TaskConfig.isRun_message);
                    Thread.sleep(1000*60*10);
                    isRun=false;
                }else {
                    TaskConfig.isRun=true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isRun;
    }
}
