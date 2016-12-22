package com.boco.rzserver.model.json;

/**
 * 获取时间趋势图
 * @author lij
 *
 */
public class Req104 extends BaseReq {
	public Body body = new Body();

	public static class Body {
		public String domain;
		public String startTime;
		public String endTime;
	}
}
