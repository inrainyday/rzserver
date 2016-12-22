package com.boco.rzserver.model.json;

/**
 * 
 * @author lij
 *
 */
public class Resp107 extends BaseResp {
	public Body body = new Body();

	public static class Body {
		public PreAlarm[] preAlarms;
	}
		
	
	public static class PreAlarm {
		public String city;
		public String domain;
		public String vendor;
		public String neType;
		public String neName;
		public String logType; //告警对象类型
		public String keys; //告警对象名称
		public String alarmTitle; 
		public String alarmTime;
		public String alarmLevel;
		public String alarmText;
		@Override
		public String toString() {
			return "PreAlarm [city=" + city + ", domain=" + domain + ", vendor=" + vendor + ", neType=" + neType
					+ ", neName=" + neName + ", logType=" + logType + ", keys=" + keys + ", alarmTitle=" + alarmTitle
					+ ", alarmTime=" + alarmTime + ", alarmLevel=" + alarmLevel + ", alarmText=" + alarmText + "]";
		}
		
	}

	@Override
	public String toString() {
		return "Resp107 [body=" + body + "]";
	}
	
}
