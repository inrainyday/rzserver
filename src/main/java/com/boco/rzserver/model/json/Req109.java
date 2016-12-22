package com.boco.rzserver.model.json;

/**
 * 获取首页右下的数据量动态趋势图
 *
 */
public class Req109 extends BaseReq {
	public Body body = new Body();

	public static class Body {
		public String startTime;
		public String endTime;
	}
}
