package cn.jinjing.plat.postal.util;

import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.XML;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class XmlUtil {

    private static Log log = LogFactory.getLog(XmlUtil.class);
    private static String ACCOUNT = ConfigUtil.getProperties("postal_account");
    private static String CLIENT = ConfigUtil.getProperties("postal_client");
    private static String MATCH_COUNT = ConfigUtil.getProperties("postal_match_count");
    private static String ASS_COUNT = ConfigUtil.getProperties("postal_ass_count");
    private static String AUTO_CHILD = ConfigUtil.getProperties("postal_auto_child");

    /**
     * 生成xml报文
     * @param list 待验证地址
     * @param label 接口名称
     * @param divLimit 接口名称为AddrAssociation时，需要填写区域限定
     * @return String类型的xml
     */
    public static String createXml(List<String> list, String label, String divLimit) {
        String xml = "";
        String interfaceCode = StringUtil.upperCase(label);//开头大写
        Document doc;

        try {
            doc = DocumentHelper.createDocument();
            //<root>
            Element root = doc.addElement("root");
            //在根标签添加元素
            Element head = root.addElement("Head");
            head.addAttribute("Account", ACCOUNT);
            head.addAttribute("Client", CLIENT);
            head.addAttribute("Interface", interfaceCode);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(new Date());
            head.addAttribute("Time", time);

            //<Body>
            Element body = root.addElement("Body");
            Element content = body.addElement(interfaceCode);

            //内容标签，标签名就是interfaceCode
            content.addAttribute("Amount", "AddrAssociation".equals(interfaceCode) ? "1" : Integer.toString(list.size()));

            //<Node>
            for(String addr : list) {
                Element node = content.addElement("Node");
                node.addAttribute("SrcAddr", addr);
                if("AddrAssociation".equals(interfaceCode)) {//地址联想
                    node.addAttribute("DivLimit", divLimit);
                    node.addAttribute("MatchCount", MATCH_COUNT);
                    node.addAttribute("AssCount", ASS_COUNT);
                    node.addAttribute("AutoChild", AUTO_CHILD);
                }
            }

            xml = doc.getRootElement().asXML();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return xml;
    }

    /**
     * xml 转 json
     * @param xmlReq String 类型的 xml
     * @return String 类型的 json
     */
    public static String xml2jsonString(String xmlReq) {
        String result = null;
        org.json.JSONObject xmlJSONObj ;
        try {
            byte[] b = xmlReq.getBytes();
            InputStream in = new ByteArrayInputStream(b);

            String xml = IOUtils.toString(in);
            xmlJSONObj = XML.toJSONObject(xml);
            result = xmlJSONObj.toString();
        }catch (Exception e){
            e.printStackTrace();
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return result;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("北京市朝阳区三里屯路19号");
        list.add("北京市西城区地安门东大街125号");
        list.add("北京市西城区松树街18号");

        String xml1 = XmlUtil.createXml(list, "VerifyAddr", "");

        System.out.println(xml1);

    }
}
