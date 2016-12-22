package com.boco.rzserver.model.json;

public class LogObj {
	private String neName;
	private String vendor;
	private String neType;
	private String domain;
	private String fileName;
	private String eventTime;
//	private String province;
	private String city;
	private int count;
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "LogObj [neName=" + neName + ", vendor=" + vendor + ", neType="
				+ neType + ", domain=" + domain + ", fileName=" + fileName
				+ ", eventTime=" + eventTime + ", city=" + city  + ", count=" + count + "]";
	}
	
}
