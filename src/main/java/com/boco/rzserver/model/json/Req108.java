package com.boco.rzserver.model.json;

/**
 * 获取首页漏斗图数据
 *
 */
public class Req108 extends BaseReq {
	public Body body = new Body();

	public static class Body {
		public String startTime;
		public String endTime;
	}
}
