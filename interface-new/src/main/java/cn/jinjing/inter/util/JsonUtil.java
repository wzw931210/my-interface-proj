package cn.jinjing.inter.util;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

	@SuppressWarnings("unchecked")
	public static Map<String,Object> jsonToMap(String json) {
		return (Map<String, Object>)JSONObject.parse(json);
	}
	
	public static void main(String [] args) {
		String json = "{\r\n" + 
				"    \"userCode\": \"YGBX\",\r\n" + 
				"    \"accessKey\": \"22ca8686bfa31a2ae5f55a7f60009e14\",\r\n" + 
				"    \"month\": \"201811\",\r\n" +
				"    \"encrypt\": 1,\r\n" + 
				"    \"batch_id\": \"sig00000001\",\r\n" + 
				"    \"batch_nums\": \"3\",\r\n" +
				"    \"batch_sub_id\": \"4\",\r\n" + 
				"    \"batch_sub_nums\": \"3\",\r\n" +
				"    \"data\": [\r\n" + 
				"        {\r\n" + 
				"            \"mdn\": \"4120be6537c447cbcb424f35102cbfc3\",\r\n" + 
				"            \"telCom\": 1\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"mdn\": \"251b9d177604fe642b2708dad0ec045a\",\r\n" + 
				"            \"telCom\": 0\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"mdn\": \"d855072565a43d0034030081cdcf3385\",\r\n" + 
				"            \"telCom\": 1\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		Map<String,Object> map = jsonToMap(json);
		List<JSONObject> jsonArray = (List<JSONObject>)map.get("data");
		String batch_id = (String)map.get("batch_id");
		long batch_nums = Integer.parseInt((String)map.get("batch_nums"));
		String batch_sub_id = (String)map.get("batch_sub_id");
		long batch_sub_nums = Integer.parseInt((String)map.get("batch_sub_nums"));
		String userCode = (String)map.get("userCode");
		String month = (String)map.get("month");
		try {
			if (jsonArray != null && jsonArray.size() > 0) {
				if (batch_sub_nums == jsonArray.size()) {    //接收数量跟报文中数量一致
					String mon = StringUtil.getStrFromDate(new Date(), "yyyyMMdd");
					String tmpName = userCode + "_" + month + "_" + batch_id + "_" + batch_nums + "_" + batch_sub_id + "_" + batch_sub_nums + "_"
							+ mon + "." + batch_id.substring(0, 3) + ".WAIT";//文件命名
					File tmpFile = new File("C:\\Users\\wzw\\Desktop\\mdn\\" + tmpName);
					if (!tmpFile.exists()) {
						tmpFile.createNewFile();
					}
					for (JSONObject json1 : jsonArray) {
						String data = json1.getString("telCom") + "," + json1.getString("mdn");
						FileUtil.writeToFile(data, tmpFile);
					}
					String fileName = userCode + "_" + month + "_" + batch_id + "_" + batch_nums + "_" + batch_sub_id + "_" + batch_sub_nums + "_"
							+ mon + "." + batch_id.substring(0, 3) + ".OK";//文件重命名
					tmpFile.renameTo(new File("C:\\Users\\wzw\\Desktop\\mdn\\"+fileName));

				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
