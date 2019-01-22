package com.jjdata.batch.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shipeien
 * @version 1.0
 * @Title: FileUtils
 * @ProjectName run-batch
 * @Description: 文件处理类
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1114:24
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    /**
     * 读取一个文本 一行一行读取
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readFile(String path){
        List<String> list = null;
        FileInputStream fis=null;
        InputStreamReader isr =null;
        BufferedReader br =null;
        try {
            File file=new File(path);
            if(!file.exists()){//判断文件是否存在
                //文件不存在
                logger.error("文件不存在..."+path);
                return list;
            }
            // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
            list = new ArrayList<String>();
            fis = new FileInputStream(path);
            // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
            isr = new InputStreamReader(fis,"GBK");
            br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
                if (line!=null&&line.length()>0) {
                    list.add(line);
                }
            }
        }catch (Exception e){
            logger.error("文件读取异常..."+path, e);
            e.printStackTrace();
        }finally {
            try {
                if(null!=br){
                    br.close();
                }
                if(null!=isr){
                    isr.close();
                }
                if(null!=fis){
                    fis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return list;
    }

    /**
     * 读取一个文本 一行一行读取
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readFile(String path,String charsetName){
        List<String> list = null;
        FileInputStream fis=null;
        InputStreamReader isr =null;
        BufferedReader br =null;
        try {
            File file=new File(path);
            if(!file.exists()){//判断文件是否存在
                //文件不存在
                logger.error("文件不存在..."+path);
                return list;
            }
            // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
            list = new ArrayList<String>();
            fis = new FileInputStream(path);
            // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
            isr = new InputStreamReader(fis,charsetName);
            br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
                if (line!=null&&line.length()>0) {
                    list.add(line);
                }
            }
        }catch (Exception e){
            logger.error("文件读取异常..."+path, e);
            e.printStackTrace();
        }finally {
            try {
                if(null!=br){
                    br.close();
                }
                if(null!=isr){
                    isr.close();
                }
                if(null!=fis){
                    fis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return list;
    }

    public static void moveFile(String pathName, String fileName, String ansPath, String asnFileName){
        String startPath =  pathName + File.separator + fileName;
        String endPath = ansPath;
        try {
            File startFile = new File(startPath);
            File tmpFile = new File(endPath);//获取文件夹路径
            if(!tmpFile.exists()){//判断文件夹是否创建，没有创建则创建新文件夹
                tmpFile.mkdirs();
            }
            if (startFile.renameTo(new File(endPath + asnFileName))) {
                System.out.println("File is moved successful!");
                logger.info("文件移动成功！文件名：《{}》 目标路径：{}"+fileName+endPath);
            } else {
                System.out.println("File is failed to move!");
                logger.error("文件移动失败！文件名：《{}》 起始路径：{}"+fileName+startPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件移动异常！文件名：《{}》 起始路径：{}"+fileName+startPath,e);

        }
    }

    public static void moveFile(String srcPathName, String ansPathName){
        try {
            File startFile = new File(srcPathName);
            if (startFile.renameTo(new File(ansPathName))) {
                System.out.println("File is moved successful!");
                logger.info("文件移动成功！文件名：《{}》 目标路径：{}"+srcPathName+ansPathName);
            } else {
                System.out.println("File is failed to move!");
                logger.error("文件移动失败！文件名：《{}》 起始路径：{}"+srcPathName+ansPathName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件移动异常！文件名：《{}》 起始路径：{}"+srcPathName+ansPathName,e);

        }
    }

    /**
     * 追写文件
     * @param lineContent
     */
    public static void writeFile(String lineContent, String filePath){
        FileOutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        try {
            File outputFile = new File(filePath);
            if(!outputFile.exists()){
                outputFile.createNewFile();
            }
            out = new FileOutputStream(outputFile, true);
            writer = new OutputStreamWriter(out, "GBK");
            bw = new BufferedWriter(writer);
            bw.write(lineContent);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            logger.error(filePath,e);
            e.printStackTrace();
        }finally{
            try {
                if(bw!=null){
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 追写文件
     * @param lineContent
     */
    public static void writeFile(String lineContent, String filePath,String charsetName){
        FileOutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter bw = null;
        try {
            File outputFile = new File(filePath);
            if(!outputFile.exists()){
                outputFile.createNewFile();
            }
            out = new FileOutputStream(outputFile, true);
            writer = new OutputStreamWriter(out, charsetName);
            bw = new BufferedWriter(writer);
            bw.write(lineContent);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            logger.error(filePath,e);
            e.printStackTrace();
        }finally{
            try {
                if(bw!=null){
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 删除文件
     * @param fileNamePath
     */
    public static void removeFile(String fileNamePath){
        try {
            File file = new File(fileNamePath);
            if(file.exists()){
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入TXT，覆盖原内容
     * @param content
     * @param fileNamePath
     * @return
     * @throws Exception
     */
    public static boolean writeTxtFile(String content,String fileNamePath)throws Exception{
        RandomAccessFile mm=null;
        boolean flag=false;
        File file = new File(fileNamePath);
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes("GBK"));
            fileOutputStream.close();
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 写入TXT，覆盖原内容
     * @param content
     * @param fileNamePath
     * @return
     * @throws Exception
     */
    public static boolean writeTxtFile(String content,String fileNamePath,String charsetName)throws Exception{
        RandomAccessFile mm=null;
        boolean flag=false;
        File file = new File(fileNamePath);
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes(charsetName));
            fileOutputStream.close();
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 删除文本中前几行
     * @return
     */
    public static boolean delTextFileContent(int lineDel,String fileNamePath){
        boolean flag=false;
        BufferedReader br=null;
        BufferedWriter bw=null;
        try {
            br=new   BufferedReader(new   FileReader( fileNamePath));
            StringBuffer sb=new   StringBuffer(4096);
            String   temp=null;
            int   line=0;
            while((temp=br.readLine())!=null){
                line++;
                if(line<=lineDel)   {
                    continue;
                }
                sb.append(temp).append( "\r\n");
            }
            bw=new   BufferedWriter(new   FileWriter( fileNamePath));
            bw.write(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(bw!=null){
                    bw.close();
                }
                if(br!=null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
    /**
     * 读取文本多少行
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readFileLine(int lineNum,String path){
        List<String> list = null;
        FileInputStream fis=null;
        InputStreamReader isr =null;
        BufferedReader br =null;
        try {
            File file=new File(path);
            if(!file.exists()){//判断文件是否存在
                //文件不存在
                logger.error("文件不存在..."+path);
                return list;
            }
            // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
            list = new ArrayList<String>();
            fis = new FileInputStream(path);
            // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
            isr = new InputStreamReader(fis,"GBK");
            br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
                if (line!=null&&line.length()>0) {
                    if(list.size()>=lineNum){
                        break;
                    }else {
                        list.add(line);
                    }

                }
            }
        }catch (Exception e){
            logger.error("文件读取异常..."+path, e);
            e.printStackTrace();
        }finally {
            try {
                if(null!=br){
                    br.close();
                }
                if(null!=isr){
                    isr.close();
                }
                if(null!=fis){
                    fis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return list;
    }

    /**
     *  获取文件行数
     * @param fileNamePath 文件路径
     * @return
     */
    public int findFileLine(String fileNamePath){
        int linenumber=0;
        try{
            File file =new File(fileNamePath);
            if(file.exists()){
                FileReader fr = new FileReader(file);
                LineNumberReader lnr = new LineNumberReader(fr);
                linenumber = 0;
                while (lnr.readLine() != null){
                    linenumber++;
                }
                logger.info("Total number of lines : " + linenumber);
                lnr.close();
            }else{
                logger.info("File does not exists!");
            }
        }catch(IOException e){
            e.printStackTrace();
            logger.error("获取文件行数失败...",e);
        }
        return linenumber;
    }


    /**
     * 获取目录下所有文件，排除文件夹
     * @param path 路径
     * @param format 文件格式，如果为空不需要过滤文件格式
     * @return
     */
    public static File[] findPathFileNoDir(String path, String format){
        File[] files =null;
        try {
            // 创建 File对象
            File file = new File(path);
            // 取 文件/文件夹
            //如果格式不为空过滤格式
            if(null!=format&&!"".equals(format)){
                files = file.listFiles(new FileFilterFormatNoDir(format));
            }else{
                files = file.listFiles();
            }
            // 目录下文件
            if(files.length == 0){
                logger.info(path + "该文件夹下没有文件");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  files;
    }


}
