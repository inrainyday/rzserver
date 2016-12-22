package com.boco.rzserver.service.cache;

public class ParamObj {
	private String startTime;
	private String endTime;
	private String domain;
	
	public ParamObj(String domain, String startTime, String endTime) {
		this.domain = domain;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
}
