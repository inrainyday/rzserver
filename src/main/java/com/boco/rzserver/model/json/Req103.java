package com.boco.rzserver.model.json;

/**
 * 首页右下角获取范围统计表统计项
 * @author lij
 *
 */
public class Req103 extends BaseReq {
	public Body body = new Body();

	public static class Body {
		public String startTime;
		public String endTime;
	}
}
