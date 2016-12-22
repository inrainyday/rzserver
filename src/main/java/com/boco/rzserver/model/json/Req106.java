package com.boco.rzserver.model.json;

/**
 * 
 * top5表格统计
 *
 */
public class Req106 extends BaseReq {
	public Body body = new Body();

	public static class Body {
		public String startTime;
		public String endTime;
		public String domain;
		public String city;
	}
}
