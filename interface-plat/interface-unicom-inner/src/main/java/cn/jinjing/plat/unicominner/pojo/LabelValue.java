package cn.jinjing.plat.unicominner.pojo;

/**
 * Created by Joy.M on 2018/9/26 11:39
 */
public class LabelValue {
    private boolean flag; //成功或失败标志

    private String message; //失败原因

    private String data; //返回的数据

    private String code; // 返回代码



    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
