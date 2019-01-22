package cn.jinjing.inter.pojo;

public enum Result {

	SUCCESS("SUCCESS"), ERROR("ERROR");
	
	String value;
	
	Result(String value) {  
        this.value = value;  
    }  
      
    public String getValue() {  
        return value;  
    }  
}
