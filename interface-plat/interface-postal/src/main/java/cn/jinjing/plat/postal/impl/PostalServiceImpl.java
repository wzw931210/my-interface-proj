package cn.jinjing.plat.postal.impl;

import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.entity.SourceLog;
import cn.jinjing.plat.postal.util.IDEATcpClient;
import cn.jinjing.plat.postal.util.XmlUtil;
import cn.jinjing.plat.service.postal.PostalService;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.SourceLogUtil;
import cn.jinjing.plat.util.StatusCode;
import cn.jinjing.plat.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PostalServiceImpl implements PostalService {

    private static Log log = LogFactory.getLog(PostalServiceImpl.class);

    @Override
    public ReObject getPostalLabel(List<String> address, String label, String divLimit){
        ReObject reObj = new ReObject(false);
        SourceLog postalLog = new SourceLog();
        postalLog.setDataSource("05");//邮政
        postalLog.setLabel(label);

        //邮政接口
        try{
            log.info(">>>>>>>>>>>>>>邮政接口>>>>>>>>>"+label);
            //生成xml请求报文
            String xml = XmlUtil.createXml(address,label,divLimit);
            //转成bgk格式
            String xmlGbk = new String(xml.getBytes("GBK"),"GBK");

            postalLog.setStartTime(new Date());
            String xmlBody = IDEATcpClient.ConnectionClient(xmlGbk);//socket客户端
            postalLog.setEndTime(new Date());

            if(xmlBody != null ){
                String jsonResult = XmlUtil.xml2jsonString(xmlBody);//返回的xml报文转为json
                JSONObject  json = JSONObject.parseObject(jsonResult);
                String interfaceCode = StringUtil.upperCase(label);
                JSONObject node = json.getJSONObject("root").getJSONObject("Body").getJSONObject(interfaceCode).getJSONObject("Node");

                reObj.setFlag(true);
                reObj.setData(node);
                reObj.setMessage(StatusCode.SUCCESS.getValue());
                reObj.setCode(StatusCode.SUCCESS.getCode());
            }else {
                reObj.setFlag(false);
                reObj.setMessage(StatusCode.SYSERR8.getValue());
                reObj.setCode(StatusCode.SYSERR8.getCode());
            }

        }catch(Exception e){
            reObj.setFlag(false);
            reObj.setMessage(StatusCode.SYSERR8.getValue());
            reObj.setCode(StatusCode.SYSERR8.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        SourceLogUtil.saveDxInterfaceLog(postalLog);
        return reObj;
    }
}
