package cn.jinjing.plat.user.pojo;

public class CacheKey {

	private String accessKey;
	
	private long curtime;

	public CacheKey() {}
	
	public CacheKey(String accessKey, long curtime) {
		this.accessKey = accessKey;
		this.curtime = curtime;
	}
	
	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public long getCurtime() {
		return curtime;
	}

	public void setCurtime(long curtime) {
		this.curtime = curtime;
	}
	
}
