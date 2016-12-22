package com.boco.rzserver.model.json;

/**
 * 获取预警数据
 *
 */
public class Req107 extends BaseReq {
	public Body body = new Body();

	public static class Body {
		public int index;
		public int size;
		public String domain;
	}
}
