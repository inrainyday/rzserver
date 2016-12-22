package com.boco.rzserver.model.json;

/**
 * 获取时间趋势图
 * @author lij
 *
 */
public class Resp104 extends BaseResp {
	public Body body = new Body();

	public static class Body {
		public TrendItem[] trendItems;
	}
	
	public static class TrendItem {
		public String trendName;
		public String[] timePoints;
		public long[] values;
	}
}
