package com.boco.rzserver.model;

public class PreAlarmRule {
	private String name; //规则名称
	private String domain;
	private String neType;
	private String vendor;
	private String logType;
	private String keys;
	private String alarmTitle;
	private String alarmText;
	private String alarmLevel;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getLogType() {
		return logType;
	}
	public void setLogType(String logType) {
		this.logType = logType;
	}
	public String getKeys() {
		return keys;
	}
	public void setKeys(String keys) {
		this.keys = keys;
	}
	public String getAlarmTitle() {
		return alarmTitle;
	}
	public void setAlarmTitle(String alarmTitle) {
		this.alarmTitle = alarmTitle;
	}
	public String getAlarmText() {
		return alarmText;
	}
	public void setAlarmText(String alarmText) {
		this.alarmText = alarmText;
	}
	public String getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	@Override
	public String toString() {
		return "PreAlarmRule [name=" + name + ", domain=" + domain + ", neType=" + neType + ", vendor=" + vendor
				+ ", logType=" + logType + ", keys=" + keys + ", alarmTitle=" + alarmTitle + ", alarmText=" + alarmText
				+ ", alarmLevel=" + alarmLevel + "]";
	}
	
	
	
}
