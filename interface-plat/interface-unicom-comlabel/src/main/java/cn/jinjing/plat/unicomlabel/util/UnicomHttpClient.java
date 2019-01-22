package cn.jinjing.plat.unicomlabel.util;

import cn.jinjing.plat.util.UHttpClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public final class UnicomHttpClient {


    /**
     * 句柄
     */
    private static HttpClient httpClient = null;


    public static UHttpClient.Res doRequest(UHttpClient.Method reqMethod , String reqUrl, Map<String, String> httpParams, String charset, boolean pretty)throws Exception{

        if(httpClient == null){
            MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams httpConnectionManagerParams = httpConnectionManager.getParams();
            httpConnectionManagerParams.setConnectionTimeout(5000);
            httpConnectionManagerParams.setSoTimeout(10000);
            httpConnectionManagerParams.setDefaultMaxConnectionsPerHost(32);
            httpConnectionManagerParams.setMaxTotalConnections(256);
            httpClient = new HttpClient(httpConnectionManager);
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,charset);
        }
        HttpMethod httpMethod = null;
        BufferedReader reader = null;

        // 执行
        try {
            httpMethod = UHttpClient.getMethod(reqMethod, reqUrl, httpParams);
            httpMethod.setRequestHeader("Connection", "close");
            httpMethod.setRequestHeader(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded;charset=utf-8");
            httpClient.executeMethod(httpMethod);
            reader = new BufferedReader(new InputStreamReader(httpMethod.getResponseBodyAsStream(),charset));

            String line;
            StringBuffer response = new StringBuffer();
            while((line = reader.readLine()) != null){
                response.append(line);
                if(pretty) {//需要美化
                    response.append(System.getProperty("line.separator"));//换行
                }
            }
            return new UHttpClient.Res(httpMethod.getStatusCode(),response.toString());
        }catch(Exception e){
            throw new Exception(e);
        }finally {
            if(reader != null){
                reader.close();
            }

            if(httpMethod != null){
                httpMethod.releaseConnection();
            }
        }
    }
}
