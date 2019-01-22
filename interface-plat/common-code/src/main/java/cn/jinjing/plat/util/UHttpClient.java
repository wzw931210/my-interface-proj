package cn.jinjing.plat.util;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Map;

/**
 * HTTP工具箱
 * 用于发送http请求
 */
public final class UHttpClient {

    public enum Method {
        get, post
    }

    /**
     * 表示请求结果封装
     * 
     */
    public static class Res {

        // HTTP状态码
        public final int statusCode;

        // 内容
        public final String content;


        /**
         * 
         * @param statusCode
         * @param content
         */
        public Res(int statusCode, String content) {
            this.statusCode = statusCode;
            this.content = content;
        }


        /**
         * 无参
         */
        public String toString() {
            return "[" + statusCode + "] \n" + content;
        }
    }

    /**
     * 句柄
     */
    private static HttpClient httpClient = null;

    private static Log log = LogFactory.getLog(UHttpClient.class);


    /**
     * 执行HTTP请求
     * 
     * @param reqMethod 方法get, post
     * @param url
     * @param params 参数
     * @param charset 字符集
     * @param pretty 是否加换行美化
     * @return 请求结果
     */
    public static Res doRequest(Method reqMethod, String url, Map<String, String> params, String charset,
            boolean pretty) throws Exception {
        // 初始化HttpClient句柄
        if (null == httpClient) {
            HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams httpParams = httpConnectionManager.getParams();
            httpParams.setConnectionTimeout(5000); 
            httpParams.setSoTimeout(10000); 
            httpParams.setDefaultMaxConnectionsPerHost(32);
            httpParams.setMaxTotalConnections(256);
            httpClient = new HttpClient(httpConnectionManager);
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);// 设置编码格式
        }

        HttpMethod method = null;
        BufferedReader reader = null;
        try {
            // 执行
            method = getMethod(reqMethod, url, params);
            method.setRequestHeader("Connection", "close");
            httpClient.executeMethod(method);
            // 处理返回值
            reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
                if (pretty) {
                    response.append(System.getProperty("line.separator"));
                }
            }
            return new Res(method.getStatusCode(), response.toString());
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new Exception(e);
                }
            }
            if (null != method) {
                method.releaseConnection();
            }
        }
    }


    /**
     * 根据请求类型创建方法
     * 
     * @param reqMethod
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static HttpMethod getMethod(Method reqMethod, String url, Map<String, String> params) throws Exception {
        switch (reqMethod) {
        case get:
            StringBuffer sb = new StringBuffer(url).append("?");
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String encodeStr = URLEncoder.encode(entry.getValue(),"UTF-8");
                    sb.append(entry.getKey()).append("=").append(encodeStr).append("&");
                }
            }
            String getUrl = sb.toString();
            log.info("电信url："+getUrl);
            return new GetMethod(getUrl);
        case post:
            PostMethod pm = new PostMethod(url);
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    pm.addParameter(entry.getKey(), entry.getValue());
                }
            }
            return pm;
        }
        throw new Exception("Unkown Method: " + reqMethod);
    }

}
