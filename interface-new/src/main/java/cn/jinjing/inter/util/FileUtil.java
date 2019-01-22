package cn.jinjing.inter.util;

import java.io.*;
import java.util.Date;

import cn.jinjing.inter.pojo.Result;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileUtil {
	
	private static Log log = LogFactory.getLog(FileUtil.class);
    private static int bufferSize = Integer.parseInt(ConfigUtil.getProperties("buffer_size"));
	
	public static File writeToFile(String data ,File tmpFile){
        FileOutputStream outputStream = null;
        try{
            if(!StringUtil.isEmpty(data)){
                data = data+"\r\n";
                outputStream = new FileOutputStream(tmpFile,true);//true 可以追加文件
                byte [] dataBytes = data.getBytes("UTF-8");
                outputStream.write(dataBytes,0,dataBytes.length);
                outputStream.flush();
            }
        }catch (IOException e){
            System.exit(1);
            log.error("<<<<<<<<<<<保存文件失败！"+e.getMessage());
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                    log.error("<<<<<<<<<<<输出流关闭失败！"+e.getMessage());
                }
            }
        }
        return tmpFile;
    }

    public static JSONObject readFile2(File inputFile) {
        JSONObject result = new JSONObject();
        result.put("flag", Result.SUCCESS.getValue());
        try {
            String fileName = inputFile.getName();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), bufferSize * 1024 * 1024);// 5M缓存
            JSONArray jsonArray = new JSONArray();
            String batch_id = fileName.split("_")[2];
            String batch_nums = fileName.split("_")[3];
            String batch_sub_id = fileName.split("_")[4];
            String batch_sub_nums = fileName.split("_")[5];
            String label ;
            while (in.ready()) {
                String line = in.readLine();
                String[] array = line.split(",");
                JSONObject jsonData = new JSONObject();
                for (int i = 0; i < array.length; i++) {
                    if (fileName.endsWith("U_sun.OK")) {
                        label = ConfigUtil.getLabelValue(String.valueOf(i));
                    }else{
                        label = ConfigUtil.getDebitValue(String.valueOf(i));
                    }
                    jsonData.put(label, array[i]);
                }
                jsonArray.add(jsonData);
            }
            result.put("flag", Result.SUCCESS.getValue());
            result.put("message", "成功！");
            result.put("code", "00");
            result.put("month", StringUtil.getStrFromDate(new Date(), "yyyyMM"));
            result.put("batch_id", batch_id);
            result.put("batch_nums", batch_nums);
            result.put("batch_sub_id", batch_sub_id);
            result.put("batch_sub_nums", batch_sub_nums);
            result.put("data", jsonArray);
        } catch (Exception e) {
            log.error("》》》》》》》》》读文件获取结果失败--"+inputFile.getName());
            e.printStackTrace();
        }
        return result;
    }

}
