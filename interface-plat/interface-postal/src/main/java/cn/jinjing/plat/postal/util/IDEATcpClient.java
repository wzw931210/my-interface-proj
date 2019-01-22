package cn.jinjing.plat.postal.util;

import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class IDEATcpClient {

    private static Log log = LogFactory.getLog(IDEATcpClient.class);
    private static String ADDRESS = ConfigUtil.getProperties("postal_address");
    private static int PORT = Integer.parseInt(ConfigUtil.getProperties("postal_port"));

    private static Socket socket = null;

    /**
     * tcpClient
     * @param xml String 格式的 xml报文
     * @return 返回的xml报文
     */
    public static String ConnectionClient(String xml){
        String result = null ;
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            byte[] b = xml.getBytes("GBK");
            int len = b.length;
            byte[] idea = "IDEA".getBytes("GBK");
            byte[] length1 = XmlUtil.intToBytes2(len);
            byte[] version = XmlUtil.intToBytes2(0);
            byte[] reserve = "abc".getBytes("GBK");

            byte[] xmlbt = new byte[len + 12];
            //给请求报文添加包头
            System.arraycopy(idea, 0, xmlbt, 0, 4);
            System.arraycopy(length1, 0, xmlbt, 4, 4);
            System.arraycopy(version, 0, xmlbt, 8, 1);
            System.arraycopy(reserve, 0, xmlbt, 9, 3);
            System.arraycopy(b, 0, xmlbt, 12, len);

            socket = new Socket(ADDRESS,PORT);
            out = new DataOutputStream(socket.getOutputStream());
            out.write(xmlbt);
            out.flush();

            // 接收来自服务器的字节类型数据
            System.out.println("*************接收*****************");
            in = new DataInputStream(socket.getInputStream());

            //获取头信息
            byte[] header = new byte[12];
            in.read(header);
            String lengthStr = new String(header,4,4);

            int bodyLen = Integer.valueOf(lengthStr);//得到包体长度
            byte[] buf = new byte[bodyLen+12];
            in.read(buf);//获得包体+包头

            //前12位是标志位，去掉前12位
            byte [] xmlBody = new byte[bodyLen];
            System.arraycopy(buf, 12, xmlBody, 0, bodyLen);

            result = new String(xmlBody,"GBK");

        }catch(Exception e){
            e.printStackTrace();
            log.error(ExceptionUtil.printStackTraceToString(e));
        }finally {
            try {
                socket.close();
                if( null != out){
                    out.close();
                }
                if( null != in){
                    in.close();
                }
            }catch (IOException e){
                e.printStackTrace();
                log.error(ExceptionUtil.printStackTraceToString(e));
            }
        }
         return result;
    }
}
