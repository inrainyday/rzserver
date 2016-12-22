package com.boco.rzserver.model.json;

/**
 * 返回漏斗图数据
 * @author lij
 *
 */
public class Resp108 extends BaseResp {
	public Body body = new Body();

	public static class Body {
		public Item[] items;
	}
	
	public static class Item {
		public String name;
		public long value;
	}
}
