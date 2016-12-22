package com.boco.rzserver.model.json;

public class Resp103 extends BaseResp {
	public Body body = new Body();

	public static class Body {
		public StatItem[] statItems;
	}
	
	public static class StatItem {
		public String rangeName;
		public long neCount;
		public long logCount;
		public double size; // 大小，单位GB
	}
}
